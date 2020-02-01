package com.example.workwiz1;

public class Users{
    private String name,email,phoneNo,dob,profile;

    public Users(String name, String email, String phoneNo, String dob,String profile)
    {

        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.dob = dob;
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

    public String getDob(){
        return dob;
    }

    public void setDob(String dob){

        this.dob = dob;
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
