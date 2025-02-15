package com.lokerin.app;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String email;
    private String password;
    private String type;
    private String name;
    private String nameLowerCase;
    private String phoneNumber;
    private String location;
    private int age;
    private String gender;
    private String aboutMe;
    private ArrayList<String> skill;
    private String skillDesc;
    private String job;
    private String jobDesc;
    private String id;
    private String imageUrl;
    private ArrayList<PortofolioJob> portofolioJob;
    private ArrayList<String> reviews;

    public User(){

    }

    public User(String id,String email, String password, String type, String name, String nameLowerCase, ArrayList<PortofolioJob> portofolioJob, String phoneNumber, String location, int age, String gender, String aboutMe, ArrayList<String> skill, String skillDesc, String job, String jobDesc, String imageUrl) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.type = type;
        this.name = name;
        this.nameLowerCase = nameLowerCase;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.age = age;
        this.gender = gender;
        this.aboutMe = aboutMe;
        this.skill = skill;
        this.skillDesc = skillDesc;
        this.job = job;
        this.jobDesc = jobDesc;
        this.imageUrl = imageUrl;
        this.portofolioJob = portofolioJob;
    }

    public ArrayList<PortofolioJob> getPortofolioJob() {
        return portofolioJob;
    }

    public void setPortofolioJob(ArrayList<PortofolioJob> portofolioJob) {
        this.portofolioJob = portofolioJob;
    }

    public ArrayList<String> getSkill() {
        return skill;
    }

    public void setSkill(ArrayList<String> skill) {
        this.skill = skill;
    }

    public User(String id, String email, String name){
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public User(String email, String name){
        this.email = email;
        this.name = name;
    }

    public String getNameLowerCase() {
        return nameLowerCase;
    }

    public void setNameLowerCase(String nameLowerCase) {
        this.nameLowerCase = nameLowerCase;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getSkillDesc() {
        return skillDesc;
    }

    public void setSkillDesc(String skillDesc) {
        this.skillDesc = skillDesc;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<String> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<String> reviews) {
        this.reviews = reviews;
    }
}

