package com.medyi.whoisup.model;

import java.io.Serializable;
import java.util.Date;

public class Room implements Serializable {

    private String id;
    private String name;
    private Date createdTime;
    private String url;

    public Room() {
    }

    public Room(String id, String name, Date createdTime, String url) {
        this.id = id;
        this.name = name;
        this.createdTime = createdTime;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createdTime=" + createdTime +
                ", url='" + url + '\'' +
                '}';
    }
}
