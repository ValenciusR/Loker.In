package com.example.lokerin;

import java.util.List;

public class JobData {
    private String jobTitle;
    private String jobLocation;
    private String jobDateUpload;
    private String jobCategory;
    private String jobStatus;
    private List<User> applicants;

    public JobData(String jobTitle, String jobLocation, String jobDateUpload, String jobCategory, String jobStatus, List<User> applicants) {
        this.jobTitle = jobTitle;
        this.jobLocation = jobLocation;
        this.jobDateUpload = jobDateUpload;
        this.jobCategory = jobCategory;
        this.jobStatus = jobStatus;
        this.applicants = applicants;
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

    public String getJobCategory() {
        return jobCategory;
    }

    public String getJobStatus() {
        return jobStatus;
    }
    
    public List<User> getApplicants() {
        return applicants;
    }

    public void addApplicant(User applicant) {
        this.applicants.add(applicant);
    }

    public void removeApplicant(User applicant) {
        this.applicants.remove(applicant);
    }
}
