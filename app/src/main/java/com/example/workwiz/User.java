package com.example.workwiz;

import android.net.Uri;

public class Users{
    private String name,email,phoneNo,documentId,profile;
    public Users(String name, String email, String phoneNo, String documentId, String profile)
    {

        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.documentId = documentId;
        this.profile = profile;

    }

    public Users(){


    }

    public String getName(){
        return name;
    }

    public void setName(String name){

        this.name = name;
    }

    public String getDocumentId(){
        return documentId;
    }

    public void setDocumentId(String documentId){

        this.documentId = documentId;
    }

    public String getPhoneNo(){
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo){

        this.phoneNo = phoneNo;
    }
    public String getEmail(){
        return email;
    }

    public void setEmail(String email){

        this.email = email;
    }

    public String getProfile(){
        return profile;
    }

    public void setProfile(String profile){

        this.profile = profile;
    }


}
