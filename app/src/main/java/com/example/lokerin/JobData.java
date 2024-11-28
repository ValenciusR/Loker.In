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

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public void setJobDateUpload(String jobDateUpload) {
        this.jobDateUpload = jobDateUpload;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public void setApplicants(List<User> applicants) {
        this.applicants = applicants;
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
