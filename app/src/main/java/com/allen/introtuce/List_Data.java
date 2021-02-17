package com.allen.introtuce;

public class List_Data {
    private String first_name;
    private String last_name;
    private String birthday;
    private String hometown;
    private String country;
    private String photo;
    private String state;
    private String phone;


    public  List_Data(){

    }

    public List_Data(String first_name, String last_name, String birthday, String hometown,
                     String country, String state, String photo, String phone) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.birthday = birthday;
        this.hometown = hometown;
        this.country = country;
        this.state = state;
        this.photo = photo;
        this.phone = phone;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getHometown() {
        return hometown;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getPhoto() {
        return photo;
    }

    public String getPhone() {
        return phone;
    }
}
