package com.darincritchlow.assignment9.cs3270a9;


import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.darincritchlow.assignment9.cs3270a9.CanvasObjects.Assignment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AssignmentListFragment} interface
 * to handle interaction events.
 * Use the {@link AssignmentListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssignmentListFragment extends ListFragment {

    private AssignmentListFragmentListener mListener;
    private ArrayAdapter<Assignment> assignmentAdapter;
    private long rowID = -1;
    private String AUTH_TOKEN = Authorization.AUTH_TOKEN;
    private static final String ID = "id";
    Assignment[] assignments = {};
    private List<Assignment> assignmentItems;


    public AssignmentListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment with the parameters.
     *
     * @param rowID long rowID.
     *
     * @return A new instance of fragment AssignmentListFragment.
     */
    public static AssignmentListFragment newInstance(long rowID) {
        AssignmentListFragment fragment = new AssignmentListFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.ROW_ID, rowID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rowID = getArguments().getLong(MainActivity.ROW_ID);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        getListView().setDivider(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AssignmentListFragmentListener) activity;
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
        showAssignments(rowID);
    }

    public void showAssignments(long rowID) {
        new GetCourseID().execute(rowID);
    }

    public interface AssignmentListFragmentListener {
        public void showAssignments(long rowID, int viewID);
    }

    private class GetCourseID extends AsyncTask<Long, Object, Cursor> {

        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

        @Override
        protected Cursor doInBackground(Long... params) {
            try {
                databaseConnector.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return databaseConnector.getOneCourse(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result){
            super.onPostExecute(result);
            result.moveToFirst();
            int courseCodeIndex = result.getColumnIndex(ID);
            String courseId = result.getString(courseCodeIndex);
            result.close();
            databaseConnector.close();
            new GetCanvasAssignments().execute(courseId);
        }
    }

    private class GetCanvasAssignments extends AsyncTask<String, Integer, String> {
        String rawJson = "";

        @Override
        protected String doInBackground(String... params) {
            Log.d("test", "In AsyncTask GetCanvasAssignments");

            try {
                URL url = new URL("https://weber.instructure.com/api/v1/courses/"+params[0]+"/assignments");
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            assignmentItems = new ArrayList<>();

            try{
                assignments = jsonParseAssignment(result);
                for(Assignment a: assignments){
                    assignmentItems.add(a);
                    Log.d("test", a.name + a.due_at);
                }
                assignmentAdapter = new AssignmentAdapter(getActivity(), assignmentItems);
                setListAdapter(assignmentAdapter);
            }
            catch (Exception e){
                Log.d("test", e.getMessage());
            }
        }
    }

    private Assignment[] jsonParseAssignment(String rawJson) {
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        Assignment[] assignments = null;

        try{
            assignments = gson.fromJson(rawJson, Assignment[].class);
            Log.d("test", "Number of assignments returned is: " + assignments.length);
            Log.d("test", "First Assignment returned is: " + assignments[0].id +
                    assignments[0].name + assignments[0].description  + assignments[0].due_at);
        }
        catch (Exception e){
            Log.d("test", e.getMessage());
        }

        return assignments;
    }

}
