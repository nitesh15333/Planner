package com.planner.models;

public class modelTask {

    String id, submission_date, task_description, task_name,task_status,task_id;

    public modelTask() {
    }

    public modelTask(String id, String submission_date, String task_description, String task_name, String task_status, String task_id) {
        this.id = id;
        this.submission_date = submission_date;
        this.task_description = task_description;
        this.task_name = task_name;
        this.task_status = task_status;
        this.task_id = task_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubmission_date() {
        return submission_date;
    }

    public void setSubmission_date(String submission_date) {
        this.submission_date = submission_date;
    }

    public String getTask_description() {
        return task_description;
    }

    public void setTask_description(String task_description) {
        this.task_description = task_description;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_status() {
        return task_status;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }
}