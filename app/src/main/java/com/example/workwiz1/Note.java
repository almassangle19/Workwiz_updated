package com.example.workwiz1;

import com.google.firebase.firestore.Exclude;

public class Note {
    private String documentId;
    private  String name;
    private String salary;
    private String location;
    private  String description;

    public Note ()
    {

    }
    @Exclude
    public String getDocumentId()
    {
        return documentId;
    }
    public void setDocumentId(String documentId)
    {
        this.documentId = documentId;
    }
    public Note(String name,String location,String salary,String description)
    {
        this.name = name;
        this.location = location;
        this.salary = salary;
        this.description = description;
    }
    public String getName()
    {
        return name;
    }
    public String getSalary()
    {
        return salary;
    }
    public String getLocation()
    {
        return location;
    }
    public String getDescription()
    {
        return description;
    }
}
