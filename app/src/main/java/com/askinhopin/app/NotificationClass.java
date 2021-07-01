package com.askinhopin.app;

public class NotificationClass {

    String type, postid, recivinguid, sentuid, posttype, content,time, key;
    String Title; String discription; String category;
    String postTime;
    long rev_timemillies;
    boolean unread;

    public long getRev_timemillies() {
        return rev_timemillies;
    }

    public void setRev_timemillies(long rev_timemillies) {
        this.rev_timemillies = rev_timemillies;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public boolean getUnread() {
        return unread;
    }
    public void setUnread(boolean unread) {
        this.unread = unread;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getRecivinguid() {
        return recivinguid;
    }

    public void setRecivinguid(String recivinguid) {
        this.recivinguid = recivinguid;
    }

    public String getSentuid() {
        return sentuid;
    }

    public void setSentuid(String sentuid) {
        this.sentuid = sentuid;
    }

    public String getPosttype() {
        return posttype;
    }

    public void setPosttype(String posttype) {
        this.posttype = posttype;
    }



}
