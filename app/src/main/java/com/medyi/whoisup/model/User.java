package com.medyi.whoisup.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Date joinDate;
    private String upForWhat;
    private String instagram;
    private String discord;
    private String phone;

    //friendsList- request ile mantığı bağlanacak.
    private List<User> friendsList;

    public User() {}


    public User(String id, String email, String firstName, String lastName, Date joinDate,String upForWhat, String instagram, String discord, String phone) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.joinDate = joinDate;
        this.upForWhat = upForWhat;
        this.instagram = instagram;
        this.discord = discord;
        this.phone = phone;
    }



    public String getUpForWhat() {
        return upForWhat;
    }

    public void setUpForWhat(String upForWhat) {
        this.upForWhat = upForWhat;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getDiscord() {
        return discord;
    }

    public void setDiscord(String discord) {
        this.discord = discord;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }



    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", joinDate=" + joinDate +
                ", upForWhat='" + upForWhat + '\'' +
                ", instagram='" + instagram + '\'' +
                ", discord='" + discord + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
