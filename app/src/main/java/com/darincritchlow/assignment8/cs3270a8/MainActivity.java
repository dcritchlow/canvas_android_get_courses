package com.darincritchlow.assignment9.cs3270a9;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity
        implements CourseListFragment.CourseListFragmentListener,
                    DetailsFragment.DetailsFragmentInteractionListener,
        AddEditFragment.AddEditFragmentListener
{

    public static final String ROW_ID = "row_id";
    private FragmentTransaction transaction;
    private CourseListFragment courseListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            return;
        }

        if (findViewById(R.id.fragmentContainer) != null){
            courseListFragment = CourseListFragment.newInstance();
            transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, courseListFragment);
            transaction.commit();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_import) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCourseSelected(long rowID) {
        if (findViewById(R.id.fragmentContainer) != null) { // phone
            displayCourse(rowID, R.id.fragmentContainer);
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
    public void onAddEditCompleted(long rowID) {
        getFragmentManager().popBackStack();

        // Tablet
//        if(findViewById(R.id.fragmentContainer) == null){
//            getFragmentManager().popBackStack();
//            courseListFragment.updateContactList();
//            displayCourse(rowID, R.id.rightPaneContainer);
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
        getFragmentManager().popBackStack();
//        if(findViewById(R.id.fragmentContainer) == null){
//            courseListFragment.updateContactList();
//        }
    }

    @Override
    public void onEditCourse(Bundle arguments) {
        if(findViewById(R.id.fragmentContainer) != null){
            displayAddEditFragment(R.id.fragmentContainer, arguments);
        }
//        else {
//            displayAddEditFragment(R.id.rightPaneContainer, arguments); // Tablet layout
//        }
    }

    @Override
    public void onAddCourse() {
        if(findViewById(R.id.fragmentContainer) != null){
            displayAddEditFragment(R.id.fragmentContainer, null);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

//        if(courseListFragment == null){
//            courseListFragment = (CourseListFragment)
//                    getFragmentManager().findFragmentById(R.id.courseListFragment);
//        }
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
