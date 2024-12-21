package com.example.lokerin;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PortofolioJob {
    private String title, location, category, imageURI;
    private Date date;
    private Boolean is_chosen, is_old_work_experience;

    public PortofolioJob(String title, String location, String category, Date date, String imageURI, Boolean is_chosen, Boolean is_old_work_experience) {
        this.title = title;
        this.location = location;
        this.category = category;
        this.date = date;
        this.imageURI = imageURI;
        this.is_chosen = is_chosen;
        this.is_old_work_experience = is_old_work_experience;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }

    public Boolean getIs_chosen() {
        return is_chosen;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setIs_chosen(Boolean is_chosen) {
        this.is_chosen = is_chosen;
    }

    public void setIs_old_work_experience(Boolean is_old_work_experience) {
        this.is_old_work_experience = is_old_work_experience;
    }
}
