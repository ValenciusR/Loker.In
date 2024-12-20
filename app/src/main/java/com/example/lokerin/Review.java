package com.example.lokerin;

public class Review {

    private String rateReviewId;
    private String jobId;
    private String pelangganId;
    private String pelangganName;
    private String pelangganImageUrl;
    private String pekerjaId;
    private Float rate;
    private Boolean recommend;
    private String review;

    public Review(){

    }

    public Review(String jobId, String pelangganId, String pelangganName, String pekerjaId, Float rate, Boolean recommended, String review) {
        this.jobId = jobId;
        this.pelangganId = pelangganId;
        this.pelangganName = pelangganName;
        this.pekerjaId = pekerjaId;
        this.rate = rate;
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

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
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
