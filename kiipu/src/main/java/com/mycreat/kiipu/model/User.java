package com.mycreat.kiipu.model;

/**
 * Created by liangyanqiao on 2017/3/29.
 */
public class User {

    private final String firstName;
    private final String lastName;


    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


}
