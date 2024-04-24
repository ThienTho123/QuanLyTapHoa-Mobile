package com.example.quanlytaphoa_mobile;

public class UserAccount {
    private String id;
    private String username;
    private String password;
    private String roll;

    public UserAccount() {
        // Default constructor required for calls to DataSnapshot.getValue(UserAccount.class)
    }

    public UserAccount(String id, String username, String password, String roll) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roll = roll;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }
}
