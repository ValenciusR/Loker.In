package com.example.lokerin;

public class Review {

    private String rateReviewId;
    private String jobId;
    private String pelangganId;
    private String pelangganName;
    private String pelangganImageUrl;
    private String pekerjaId;
    private Float rating;
    private Boolean recommend;
    private String review;

    public Review(){

    }

    public Review(String jobId, String pelangganId, String pelangganName, String pekerjaId, Float rating, Boolean recommended, String review) {
        this.jobId = jobId;
        this.pelangganId = pelangganId;
        this.pelangganName = pelangganName;
        this.pekerjaId = pekerjaId;
        this.rating = rating;
        this.recommend = recommended;
        this.review = review;
    }

    public String getRateReviewId() {
        return rateReviewId;
    }

    public void setRateReviewId(String rateReviewId) {
        this.rateReviewId = rateReviewId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getPelangganId() {
        return pelangganId;
    }

    public void setPelangganId(String pelangganId) {
        this.pelangganId = pelangganId;
    }

    public String getPelangganName() {
        return pelangganName;
    }

    public void setPelangganName(String pelangganName) {
        this.pelangganName = pelangganName;
    }

    public String getPelangganImageUrl() {
        return pelangganImageUrl;
    }

    public void setPelangganImageUrl(String pelangganImageUrl) {
        this.pelangganImageUrl = pelangganImageUrl;
    }

    public String getPekerjaId() {
        return pekerjaId;
    }

    public void setPekerjaId(String pekerjaId) {
        this.pekerjaId = pekerjaId;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Boolean getRecommend() {
        return recommend;
    }

    public void setRecommend(Boolean recommend) {
        this.recommend = recommend;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
