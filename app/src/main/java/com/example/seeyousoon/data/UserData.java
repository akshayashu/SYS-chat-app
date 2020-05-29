package com.example.seeyousoon.data;

public class UserData {
    private String UID,Name,Email,Password,imageUrl, status, lowercasename;

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public String getLowercaseName() {
        return lowercasename;
    }

    public void setLowercaseName(String lowercaseName) {
        this.lowercasename = lowercaseName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
