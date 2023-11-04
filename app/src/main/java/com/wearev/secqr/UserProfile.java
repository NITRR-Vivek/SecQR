package com.wearev.secqr;

public class UserProfile {
    private String name;
    private String email;
    private String mobile;
    private String position;

    public UserProfile(){

    }
    public UserProfile(String name, String email, String mobile, String userPosition) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.position = userPosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getMobile() {
        return mobile;
    }
    public String getEmail() {
        return email;
    }
    public String getPosition() {
        return position;
    }

}

