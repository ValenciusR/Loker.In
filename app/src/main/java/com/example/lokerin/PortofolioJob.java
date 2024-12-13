package com.example.lokerin;

import java.util.Date;

public class PortofolioJob {
    private String title, location, category;
    private Date date;
    private Boolean is_chosen, is_old_work_experience;

    public PortofolioJob(String title, String location, String category, Date date, Boolean is_chosen, Boolean is_old_work_experience) {
        this.title = title;
        this.location = location;
        this.category = category;
        this.date = date;
        this.is_chosen = is_chosen;
        this.is_old_work_experience = is_old_work_experience;
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
}
