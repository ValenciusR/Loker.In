package com.example.lokerin;

import java.util.HashMap;
import java.util.Map;

public class JobData {
    private String jobId;
    private String jobMakerId;
    private String jobTitle;
    private String jobCategory;
    private String jobDescription;
    private int jobSalary;
    private String jobSalaryFrequent;
    private String jobProvince;
    private String jobRegency;
    private String jobAddress;
    private String jobDateUpload;
    private String jobStatus;
    private Map<String, User> jobApplicants;

    public JobData() {
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobMakerId() {
        return jobMakerId;
    }

    public void setJobMakerId(String jobMakerId) {
        this.jobMakerId = jobMakerId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public int getJobSalary() {
        return jobSalary;
    }

    public void setJobSalary(int jobSalary) {
        this.jobSalary = jobSalary;
    }

    public String getJobSalaryFrequent() {
        return jobSalaryFrequent;
    }

    public void setJobSalaryFrequent(String jobSalaryFrequent) {
        this.jobSalaryFrequent = jobSalaryFrequent;
    }

    public String getJobProvince() {
        return jobProvince;
    }

    public void setJobProvince(String jobProvince) {
        this.jobProvince = jobProvince;
    }

    public String getJobRegency() {
        return jobRegency;
    }

    public void setJobRegency(String jobRegency) {
        this.jobRegency = jobRegency;
    }

    public String getJobAddress() {
        return jobAddress;
    }

    public void setJobAddress(String jobAddress) {
        this.jobAddress = jobAddress;
    }

    public String getJobDateUpload() {
        return jobDateUpload;
    }

    public void setJobDateUpload(String jobDateUpload) {
        this.jobDateUpload = jobDateUpload;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Map<String, User> getJobApplicants() {
        return jobApplicants;
    }

    public void setJobApplicants(Map<String, User> jobApplicants) {
        this.jobApplicants = jobApplicants;
    }
}
