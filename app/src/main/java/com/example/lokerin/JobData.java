package com.example.lokerin;

public class JobData {
    private String jobTitle;
    private String jobLocation;
    private String jobDateUpload;
    private String jobCategory;

    public JobData(String jobTitle, String jobLocation, String jobDateUpload, String jobCategory) {
        this.jobTitle = jobTitle;
        this.jobLocation = jobLocation;
        this.jobDateUpload = jobDateUpload;
        this.jobCategory = jobCategory;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public String getJobDateUpload() {
        return jobDateUpload;
    }

    public String getJobCategory() { return jobCategory; }
}
