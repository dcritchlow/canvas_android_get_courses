package com.darincritchlow.assignment9.cs3270a9;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.SQLException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailsFragment.DetailsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String COURSE_CODE = "course_code";
    private static final String COURSE_START = "course_start";
    private static final String COURSE_END = "course_end";

    private long rowID = -1;
    private TextView idTextView;
    private TextView nameTextView;
    private TextView courseCodeTextView;
    private TextView startAtTextView;
    private TextView endAtTextView;

    private DetailsFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param rowID long rowID.
     * @return A new instance of fragment DetailsFragment.
     */
    public static DetailsFragment newInstance(long rowID) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.ROW_ID, rowID);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rowID = getArguments().getLong(MainActivity.ROW_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);
        if(savedInstanceState != null){
            rowID = savedInstanceState.getLong(MainActivity.ROW_ID);
        }
        else {
            Bundle arguments = getArguments();
            if(arguments != null){
                rowID = arguments.getLong(MainActivity.ROW_ID);
            }
        }
        View detailsFragment = inflater.inflate(R.layout.fragment_details, container, false);
        setHasOptionsMenu(true);

        idTextView = (TextView) detailsFragment.findViewById(R.id.txvId);
        nameTextView = (TextView) detailsFragment.findViewById(R.id.txvName);
        courseCodeTextView = (TextView) detailsFragment.findViewById(R.id.txvCourseCode);
        startAtTextView = (TextView) detailsFragment.findViewById(R.id.txvStartAt);
        endAtTextView = (TextView) detailsFragment.findViewById(R.id.txvEndAt);

        return detailsFragment;
    }

    @Override
    public void onResume(){
        super.onResume();
        new LoadCourseTask().execute(rowID);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putLong(MainActivity.ROW_ID, rowID);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_edit){
            Bundle arguments = new Bundle();
            arguments.putLong(MainActivity.ROW_ID, rowID);
            arguments.putCharSequence(ID, idTextView.getText());
            arguments.putCharSequence(NAME, nameTextView.getText());
            arguments.putCharSequence(COURSE_CODE, courseCodeTextView.getText());
            arguments.putCharSequence(COURSE_START, startAtTextView.getText());
            arguments.putCharSequence(COURSE_END, endAtTextView.getText());
            mListener.onEditCourse(arguments);
            return true;
        }
        if(item.getItemId() == R.id.action_delete){
            deleteCourse();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteCourse() {
        confirmDelete.show(getFragmentManager(), "confirm delete");
    }

    private final DialogFragment confirmDelete = new DialogFragment(){
        @Override
        public Dialog onCreateDialog(Bundle bundle){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(com.darincritchlow.assignment9.cs3270a9.R.string.confirm_title);
            builder.setMessage(com.darincritchlow.assignment9.cs3270a9.R.string.confirm_message);
            builder.setPositiveButton(com.darincritchlow.assignment9.cs3270a9.R.string.button_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
                    AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>() {
                        @Override
                        protected Object doInBackground(Long... params) {
                            try {
                                databaseConnector.deleteCourse(params[0]);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object result) {
                            mListener.onCourseDeleted();
                        }
                    };
                    deleteTask.execute(new Long[]{rowID});
                }
            });
            builder.setNegativeButton(com.darincritchlow.assignment9.cs3270a9.R.string.button_cancel, null);
            return builder.create();
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DetailsFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface DetailsFragmentInteractionListener {
        public void displayCourse(long rowID, int viewID);
        public void onCourseDeleted();
        public void onEditCourse(Bundle arguments);
    }

    private class LoadCourseTask extends AsyncTask<Long, Object, Cursor> {

        final DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

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
            int idIndex = result.getColumnIndex(ID);
            int nameIndex = result.getColumnIndex(NAME);
            int courseCodeIndex = result.getColumnIndex(COURSE_CODE);
            int startAtIndex = result.getColumnIndex(COURSE_START);
            int endAtIndex = result.getColumnIndex(COURSE_END);

            idTextView.setText(result.getString(idIndex));
            nameTextView.setText(result.getString(nameIndex));
            courseCodeTextView.setText(result.getString(courseCodeIndex));
            startAtTextView.setText(result.getString(startAtIndex));
            endAtTextView.setText(result.getString(endAtIndex));
            result.close();
            databaseConnector.close();
        }
    }
}
