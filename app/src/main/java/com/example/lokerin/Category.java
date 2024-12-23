package com.example.lokerin;

public class Category {

    private int imageResId;
    private String title;

    public Category(int imageResId, String title) {
        this.imageResId = imageResId;
        this.title = title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

}
