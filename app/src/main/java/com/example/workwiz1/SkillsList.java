package com.example.workwiz1;

public class SkillsList {
    String skill;

    public SkillsList(String skill)
    {
        this.skill = skill;
    }
    public String getSkill(){
        return skill;
    }
    public String setSkill(String skill){
        this.skill = skill;
       return skill;
    }

    public SkillsList(){
    }
}
