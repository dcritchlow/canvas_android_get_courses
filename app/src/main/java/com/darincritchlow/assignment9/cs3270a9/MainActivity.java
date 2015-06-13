package com.darincritchlow.assignment9.cs3270a9;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity
        implements  CourseListFragment.CourseListFragmentListener,
                    DetailsFragment.DetailsFragmentInteractionListener,
                    AddEditFragment.AddEditFragmentListener,
                    AssignmentListFragment.AssignmentListFragmentListener
{

    public static final String ROW_ID = "row_id";
    private FragmentTransaction transaction;
    private boolean twoPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twoPaneLayout = findViewById(R.id.rightPaneContainer) != null;

        if(savedInstanceState != null){
            return;
        }
        getCourseListFragment();

    }

    private void getCourseListFragment() {
        CourseListFragment courseListFragment = CourseListFragment.newInstance();
        transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentContainer, courseListFragment);
        transaction.commit();
    }

    @Override
    public void onCourseSelected(long rowID) {
        if(twoPaneLayout){
            getFragmentManager().popBackStack();
            displayCourse(rowID, R.id.rightPaneContainer);
        }
        else
        {
            displayCourse(rowID, R.id.fragmentContainer);
        }
    }

    @Override
    public void onAddEditCompleted(long rowID) {
        if(twoPaneLayout){
            CourseListFragment cf = (CourseListFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer);
            cf.updateContactList();
            getFragmentManager().popBackStack();
            displayCourse(rowID, R.id.rightPaneContainer);
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void displayCourse(long rowID, int viewID) {
        DetailsFragment detailsFragment = DetailsFragment.newInstance(rowID);
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(viewID, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCourseDeleted() {

        if(twoPaneLayout){
            CourseListFragment cf = (CourseListFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer);
            cf.updateContactList();
        }
        getFragmentManager().popBackStack();
    }

    @Override
    public void onEditCourse(Bundle arguments) {

        if(twoPaneLayout){
            displayAddEditFragment(R.id.rightPaneContainer, arguments);
        }
        else
        {
            displayAddEditFragment(R.id.fragmentContainer, arguments);
        }
    }

    @Override
    public void onAddCourse() {
        if(twoPaneLayout){
            displayAddEditFragment(R.id.rightPaneContainer, null);
        }
        else {
            displayAddEditFragment(R.id.fragmentContainer, null);
        }
    }

    @Override
    public void displayAddEditFragment(int viewID, Bundle arguments) {
        AddEditFragment addEditFragment = AddEditFragment.newInstance(arguments);
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onListLongClick(long rowID) {

        if(twoPaneLayout){
            showAssignments(rowID, R.id.rightPaneContainer);
        }
        else
        {
            showAssignments(rowID, R.id.fragmentContainer);
        }
    }


    @Override
    public void showAssignments(long rowID, int viewID) {
        AssignmentListFragment assignmentListFragment = AssignmentListFragment.newInstance(rowID);
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(viewID, assignmentListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle state)
    {
        getFragmentManager().popBackStack();
        super.onSaveInstanceState(state);
    }

    @Override
    public void onBackPressed(){
        if(getFragmentManager().getBackStackEntryCount() != 0){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
