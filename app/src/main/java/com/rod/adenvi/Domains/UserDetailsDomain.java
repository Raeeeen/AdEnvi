package com.rod.adenvi.Domains;

public class UserDetailsDomain {
    private String fullName;
    private String userName;
    private String passWord; // Store the hashed password

    public UserDetailsDomain() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserDetailsDomain(String fullName, String passWord) {
        this.fullName = fullName;
        this.passWord = passWord;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPasswordHash() {
        return passWord;
    }


}
