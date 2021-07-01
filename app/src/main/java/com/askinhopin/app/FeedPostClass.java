package com.askinhopin.app;

public class FeedPostClass {

    String uid;
    String discription;
    String url;
    String key;
    String time;
    long rev_timemillies;

    public long getRev_timemillies() {
        return rev_timemillies;
    }

    public void setRev_timemillies(long rev_timemillies) {
        this.rev_timemillies = rev_timemillies;
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



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
