package com.thread.con.vo;


public class Message {

    public Message() {
    }

    public Message(String id, String name, String name2) {
        this.id = id;
        this.name = name;
        this.name2 = name2;
    }

    private String id;
    private String name;
    private String name2;


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

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }
}
