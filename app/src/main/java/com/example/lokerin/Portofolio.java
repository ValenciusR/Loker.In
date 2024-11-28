package com.example.lokerin;

import java.util.Date;

public class Portofolio {
    String jobTitle, jobDescription;
    Date date;
    public Portofolio(String jobTitle, Date date, String jobDescription) {
        this.jobTitle = jobTitle;
        this.date = date;
        this.jobDescription = jobDescription;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public Date getDate() {
        return date;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}
