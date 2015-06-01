package com.darincritchlow.assignment7.cs3270a8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by dcritchlow on 5/30/15.
 */
public class DatabaseConnector {

    private static final String DATABASE_NAME = "UserCourses";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String COURSE_CODE = "course_code";
    private static final String COURSE_START = "course_start";
    private static final String COURSE_END = "course_end";
    private static final String COURSES = "courses";

    private SQLiteDatabase database;
    private DatabaseOpenHelper databaseOpenHelper;

    public DatabaseConnector(Context context){
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    public void open() throws SQLException {
        database = databaseOpenHelper.getWritableDatabase();
    }

    public Cursor getAllCourses() {
        return database.query(COURSES, new String[] {"_id", "name"},
                null, null, null, null, "name");
    }

    public void close() {
        if (database != null){
            database.close();
        }
    }

    public long insertCourse(
            String id, String name, String courseCode, String startAt, String endAt) throws SQLException {

        ContentValues newCourse = new ContentValues();
        newCourse.put(ID, id);
        newCourse.put(NAME, name);
        newCourse.put(COURSE_CODE, courseCode);
        newCourse.put(COURSE_START, startAt);
        newCourse.put(COURSE_END, endAt);
        open();
        long rowID = database.insertOrThrow(COURSES, null, newCourse);
        close();
        return rowID;
    }

    public void updateCourse(
            String id, String name, String courseCode, String startAt, String endAt) throws SQLException{
        ContentValues editCourse = new ContentValues();
        editCourse.put(ID, id);
        editCourse.put(NAME, name);
        editCourse.put(COURSE_CODE, courseCode);
        editCourse.put(COURSE_START, startAt);
        editCourse.put(COURSE_END, endAt);
        open();
        database.update(COURSES, editCourse, "_id=" + id, null);
        close();
    }

    public Cursor getOneCourse(Long id) {
        return database.query(
            COURSES, null, "_id=" + id, null, null, null, null);
    }

    public void deleteCourse(Long id) throws SQLException {
        open();
        database.delete(COURSES, "_id=" + id, null);
        close();
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper{

        public DatabaseOpenHelper(Context context, String id,
                                  SQLiteDatabase.CursorFactory factory, int version){

            super(context, id, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createQuery = "CREATE TABLE courses"+
                    "(_id integer primary key autoincrement,"+
                    "id integer, name TEXT, course_code TEXT,"+
                    "course_start TEXT, course_end TEXT);";

            db.execSQL(createQuery);

//            ContentValues testData1 = new ContentValues();
//            testData1.put(ID, "1");
//            testData1.put(NAME, "Awesome New Course");
//            testData1.put(COURSE_CODE, "12345");
//            testData1.put(COURSE_START, "8/1/2015");
//            testData1.put(COURSE_END, "12/20/2015");
//
//            db.insert("courses", null, testData1);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
