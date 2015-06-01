package com.darincritchlow.assignment7.cs3270a7;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.sql.SQLException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddEditFragmentListener} interface
 * to handle interaction events.
 * Use the {@link AddEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEditFragment extends Fragment {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String COURSE_CODE = "course_code";
    private static final String COURSE_START = "course_start";
    private static final String COURSE_END = "course_end";

    private long rowID;
    private Bundle courseInfoBundle;
    private EditText idEditText;
    private EditText nameEditText;
    private EditText courseCodeEditText;
    private EditText startAtEditText;
    private EditText endAtEditText;

    private AddEditFragmentListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param arguments Bundle arguments.
     * @return A new instance of fragment AddEditFragment.
     */
    public static AddEditFragment newInstance(Bundle arguments) {
        AddEditFragment fragment = new AddEditFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public AddEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseInfoBundle = getArguments();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View addEditFragment = inflater.inflate(R.layout.fragment_add_edit, container, false);

        idEditText = (EditText) addEditFragment.findViewById(R.id.etId);
        nameEditText = (EditText) addEditFragment.findViewById(R.id.etName);
        courseCodeEditText = (EditText) addEditFragment.findViewById(R.id.etCourseCode);
        startAtEditText = (EditText) addEditFragment.findViewById(R.id.etStartAt);
        endAtEditText = (EditText) addEditFragment.findViewById(R.id.etEndAt);

        courseInfoBundle = getArguments();
        if(courseInfoBundle != null){
            rowID = courseInfoBundle.getLong(MainActivity.ROW_ID);
            idEditText.setText(courseInfoBundle.getString(ID));
            nameEditText.setText(courseInfoBundle.getString(NAME));
            courseCodeEditText.setText(courseInfoBundle.getString(COURSE_CODE));
            startAtEditText.setText(courseInfoBundle.getString(COURSE_START));
            endAtEditText.setText(courseInfoBundle.getString(COURSE_END));
        }
        Button saveContactButton = (Button) addEditFragment.findViewById(R.id.btnSaveContact);
        saveContactButton.setOnClickListener(saveContactButtonClicked);

        return addEditFragment;
    }

    private OnClickListener saveContactButtonClicked = new OnClickListener(){
        @Override
        public void onClick(View v) {
            if(idEditText.getText().toString().trim().length() != 0){
                final AsyncTask<Object, Object, Object> saveCourseTask =
                    new AsyncTask<Object, Object, Object>() {
                        @Override
                        protected Object doInBackground(Object... params) {
                            try {
                                saveCourse();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                        @Override
                        protected void onPostExecute(Object result){
                            InputMethodManager imm = (InputMethodManager)
                                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                            mListener.onAddEditCompleted(rowID);
                        }
                    };
                saveCourseTask.execute((Object[]) null);
            }
            else {
                DialogFragment errorSaving = new DialogFragment(){
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(R.string.error_message);
                        builder.setPositiveButton(R.string.ok, null);
                        return builder.create();
                    }
                };
                errorSaving.show(getFragmentManager(), "error saving course");
            }
        }
    };

    private void saveCourse() throws SQLException {
        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
        if(courseInfoBundle == null){
            rowID = databaseConnector.insertCourse(
                idEditText.getText().toString(),
                nameEditText.getText().toString(),
                courseCodeEditText.getText().toString(),
                startAtEditText.getText().toString(),
                endAtEditText.getText().toString()
            );
        }
        else {
            databaseConnector.updateCourse(
                    idEditText.getText().toString(),
                    nameEditText.getText().toString(),
                    courseCodeEditText.getText().toString(),
                    startAtEditText.getText().toString(),
                    endAtEditText.getText().toString()
            );
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AddEditFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AddEditFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface AddEditFragmentListener {
        public void displayAddEditFragment(int viewID, Bundle arguments);
        public void onAddEditCompleted(long rowID);
    }

}
