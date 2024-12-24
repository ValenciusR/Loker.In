package com.example.lokerin;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PortofolioJob implements Serializable {
    private String title, desc, location, category, imageURI;
    private Date date;

    public PortofolioJob(String title, String location, String desc, String category, Date date, String imageURI) {
        this.title = title;
        this.location = location;
        this.desc = desc;
        this.category = category;
        this.date = date;
        this.imageURI = imageURI;
    }

    public PortofolioJob(String title, String location, String desc, Date date, String imageURI) {
        this.title = title;
        this.location = location;
        this.desc = desc;
        this.date = date;
        this.imageURI = imageURI;
    }


    public PortofolioJob() {

    }
    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
}
