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
    private CourseListFragment courseListFragment;
    private boolean tabletInterface;
//    View rightPane = findViewById(R.id.rightPaneContainer);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabletInterface = findViewById(R.id.rightPaneContainer) != null;

        if(savedInstanceState != null){
//            getFragmentManager().popBackStack();
            return;
//            if(findViewById(R.id.fragmentContainer) == null){
//                getFragmentManager().popBackStack();
//            }
        }

//        Log.d("test","Fragment Container " + findViewById(R.id.fragmentContainer).toString());
//        if (findViewById(R.id.fragmentContainer) != null){
//            if(tabletInterface){
//                getFragmentManager().popBackStack();
//                return;
//            }
//            getFragmentManager().popBackStack();
        getCourseListFragment();
//        }
//        if(findViewById(R.id.courseListFragment) != null){
////            getFragmentManager().popBackStack();
//            courseListFragment = CourseListFragment.newInstance();
//            transaction = getFragmentManager().beginTransaction();
//            transaction.add(R.id.courseListFragment, courseListFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
//        }

    }

    private CourseListFragment getCourseListFragment() {
        courseListFragment = CourseListFragment.newInstance();
        transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentContainer, courseListFragment);
        transaction.commit();
        return courseListFragment;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
////        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_import) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onCourseSelected(long rowID) {
//        if (findViewById(R.id.fragmentContainer) != null) { // phone
//            displayCourse(rowID, R.id.fragmentContainer);
//        }
//        else { // tablet
////            getFragmentManager().popBackStack();
//            displayCourse(rowID, R.id.rightPaneContainer);
//        }
        if(tabletInterface){
            displayCourse(rowID, R.id.rightPaneContainer);
        }
        else
        {
            displayCourse(rowID, R.id.fragmentContainer);
        }
    }

    @Override
    public void onAddEditCompleted(long rowID) {
//        getFragmentManager().popBackStack();

//        // Tablet
//        if(findViewById(R.id.fragmentContainer) == null){
//            getFragmentManager().popBackStack();
//            courseListFragment.updateContactList();
//            displayCourse(rowID, R.id.rightPaneContainer);
//        }
//        getFragmentManager().popBackStack();
        if(tabletInterface){
            CourseListFragment cf = (CourseListFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer);
            cf.updateContactList();
            displayCourse(rowID, R.id.rightPaneContainer);
        }


//        else
//        {
//
//        }
    }

    @Override
    public void displayCourse(long rowID, int viewID) {
        DetailsFragment detailsFragment = (DetailsFragment.newInstance(rowID));
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(viewID, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCourseDeleted() {

        if(tabletInterface){ // tablet
//            if(courseListFragment == null){
//                courseListFragment = getCourseListFragment();
//            }
//            courseListFragment.updateContactList();
            CourseListFragment cf = (CourseListFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer);
            cf.updateContactList();
        }
        getFragmentManager().popBackStack();
    }

    @Override
    public void onEditCourse(Bundle arguments) {
//        if(findViewById(R.id.fragmentContainer) != null){
//            displayAddEditFragment(R.id.fragmentContainer, arguments); // phone
//        }
//        else {
//            displayAddEditFragment(R.id.rightPaneContainer, arguments); // Tablet layout
//        }

        if(tabletInterface){
            displayAddEditFragment(R.id.rightPaneContainer, arguments);
        }
        else
        {
            displayAddEditFragment(R.id.fragmentContainer, arguments);
        }
    }

    @Override
    public void onAddCourse() {
        if(tabletInterface){
            displayAddEditFragment(R.id.rightPaneContainer, null);
        }
        else { // tablet
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
//        if (findViewById(R.id.fragmentContainer) != null) { // phone
//            showAssignments(rowID, R.id.fragmentContainer);
//        }
//        else { // tablet
//            showAssignments(rowID, R.id.rightPaneContainer);
//        }

        if(tabletInterface){
            showAssignments(rowID, R.id.rightPaneContainer);
        }
        else
        {
            showAssignments(rowID, R.id.fragmentContainer);
        }
    }


    @Override
    public void showAssignments(long rowID, int viewID) {
        AssignmentListFragment assignmentListFragment = new AssignmentListFragment().newInstance(rowID);
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(viewID, assignmentListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume(){
        super.onResume();
//        Log.d("test", "CourseListFragment " + courseListFragment);
//        if(courseListFragment == null){
//            courseListFragment = (CourseListFragment)
//                    getFragmentManager().findFragmentById(R.id.courseListFragment);
//        }
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
