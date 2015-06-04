package com.darincritchlow.assignment9.cs3270a9;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.darincritchlow.assignment9.cs3270a9.CanvasObjects.Course;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CourseListFragmentListener} interface
 * to handle interaction events.
 * Use the {@link CourseListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseListFragment extends ListFragment {

    private ListView courseListView;
    private CursorAdapter courseAdapter;
//    private ArrayAdapter<String> courseArrayAdapter;
    private String rowID;
    private String AUTH_TOKEN = Authorization.AUTH_TOKEN;
    private CourseListFragmentListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment CourseListFragment.
     */
    public static CourseListFragment newInstance() {
        return new CourseListFragment();
    }

    public CourseListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rowID = getArguments().getString(MainActivity.ROW_ID);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        setEmptyText(getResources().getString(R.string.no_courses));
        courseListView = getListView();
        courseListView.setOnItemClickListener(viewCourseListener);
        courseListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        courseListView.setOnItemLongClickListener(viewAssignmentsListener);
        String[] from = new String[] {"name"};
        int[] to = new int[] {android.R.id.text1};
        courseAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, null, from, to, 0);
        setListAdapter(courseAdapter);
    }



    private OnItemClickListener viewCourseListener = new OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mListener.onCourseSelected(id);
        }
    };

    private AdapterView.OnItemLongClickListener viewAssignmentsListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(getActivity(), "Course id " + id + " was clicked", Toast.LENGTH_LONG).show();
            mListener.onListLongClick(id);
            return true;
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_course_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.d("test", "In onOptionsItemSelected");
        if(item.getItemId() == R.id.action_add){
            mListener.onAddCourse();
            return true;
        }
        else if (item.getItemId() == R.id.action_import){
            new GetCanvasCourses().execute("");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CourseListFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CourseListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        new GetCoursesTask().execute((Object[]) null);
    }

    @Override
    public void onStop(){
        Cursor cursor = courseAdapter.getCursor();
        courseAdapter.changeCursor(null);
        if(cursor != null){
            cursor.close();
        }
        super.onStop();
    }

    public void updateContactList() { // tablet interface
        new GetCoursesTask().execute((Object[]) null);
    }

    public interface CourseListFragmentListener {
        public void onCourseSelected(long rowID);
        public void onAddCourse();
        public void onListLongClick(long rowID);
    }

    private class GetCoursesTask extends AsyncTask<Object, Object, Cursor> {

        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
        @Override
        protected Cursor doInBackground(Object... params) {
            try {
                databaseConnector.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return databaseConnector.getAllCourses();
        }

        @Override
        protected void onPostExecute(Cursor result){
            courseAdapter.changeCursor(result);
            databaseConnector.close();
        }
    }

    private class GetCanvasCourses extends AsyncTask<String, Integer, String>{

        String rawJson = "";

        @Override
        protected String doInBackground(String... params) {

            Log.d("test", "In AsyncTask GetCanvasCourses");

            try {
                URL url = new URL("https://weber.instructure.com/api/v1/courses");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + AUTH_TOKEN);
                conn.connect();
                int status = conn.getResponseCode();
                if (status == 200 || status == 201){
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    rawJson = br.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return rawJson;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

            try{
                Course[] courses = jsonParseCourse(result);
                for(Course course: courses){
                    databaseConnector.insertCourse(course.id, course.name, course.course_code, course.start_at, course.end_at);
                }
                updateContactList();
            }
            catch (Exception e){
                Log.d("test", e.getMessage());
            }

        }
    }

    private Course[] jsonParseCourse(String rawJson) {
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        Course[] courses = null;

        try{
            courses = gson.fromJson(rawJson, Course[].class);
            Log.d("test", "Number of courses returned is: " + courses.length);
            Log.d("test", "First Course returned is: " + courses[0].id +
                    courses[0].name + courses[0].course_code  + courses[0].start_at + courses[0].end_at);
        }
        catch (Exception e){
            Log.d("test", e.getMessage());
        }

        return courses;
    }


}
