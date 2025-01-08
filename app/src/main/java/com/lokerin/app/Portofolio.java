package com.lokerin.app;

import java.util.Date;

public class Portofolio {
    String jobTitle, jobDescription, imageURL;
    Date date;
    public Portofolio(String jobTitle, Date date, String jobDescription, String imageURL) {
        this.jobTitle = jobTitle;
        this.date = date;
        this.jobDescription = jobDescription;
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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
