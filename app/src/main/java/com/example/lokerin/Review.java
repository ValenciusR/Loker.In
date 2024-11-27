package com.example.lokerin;

public class Review {

    String name, review;
    Float rating;

    public Review(String name, String review, Float rating) {
        this.name = name;
        this.review = review;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getReview() {
        return review;
    }

    public Float getRating() {
        return rating;
    }
}
