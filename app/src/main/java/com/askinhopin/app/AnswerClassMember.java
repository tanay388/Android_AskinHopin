package com.askinhopin.app;

public class AnswerClassMember {
    String uid, time, answer, key;
    long rev_timemillies;
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public long getRev_timemillies() {
        return rev_timemillies;
    }

    public void setRev_timemillies(long rev_timemillies) {
        this.rev_timemillies = rev_timemillies;
    }

    public AnswerClassMember(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
