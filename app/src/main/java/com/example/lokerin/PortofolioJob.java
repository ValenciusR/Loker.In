package com.example.lokerin;

import java.util.Date;

public class PortofolioJob {
    private String title, location, category;
    private Date date;
    private Boolean is_chosen;

    public PortofolioJob(String title, String location, String category, Date date, Boolean is_chosen) {
        this.title = title;
        this.location = location;
        this.category = category;
        this.date = date;
        this.is_chosen = is_chosen;
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
