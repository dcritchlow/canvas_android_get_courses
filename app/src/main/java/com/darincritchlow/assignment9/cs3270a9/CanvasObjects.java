package com.darincritchlow.assignment9.cs3270a9;


public class CanvasObjects {

    protected class Course {
        protected String id;
        protected String sis_course_id;
        protected String name;
        protected String course_code;
        protected String account_id;
        protected String start_at;
        protected String end_at;
        protected Enrollment[] enrollments;
        protected Calendar calendar;
        protected String syllabus_body;
        protected String needs_grading_count;
        protected Term term;
    }

    protected class Term{
        protected String id;
        protected String name;
        protected String start_at;
        protected String end_at;
    }

    protected class Calendar{
        protected String ics;
    }

    protected class Enrollment{
        protected String type;
        protected String role;
        protected String computed_final_score;
        protected String computed_current_score;
        protected String computed_final_grade;
    }

    protected class Assignment{
        protected String id;
        protected String name;
        protected String description;
        protected String due_at;
        protected String course_id;
    }
}
