package com.darincritchlow.assignment7.cs3270a7;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.sql.SQLException;


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
    private String rowID;

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_course_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_add){
            mListener.onAddCourse();
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
}
