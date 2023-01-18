package com.example.loginandregistration.model;

public enum Rating {
    BEGINNER("Beginner"), INTERMEDIATE("Intermediate"), ADVANCED("Advanced"), PROFICIENCY("Proficiency");
    private final String str;
    Rating(String str){
        this.str=str;
    }

    @Override
    public String toString(){
        return str;
    }

    public static Rating valueOfLabel(String label) {
        for (Rating e : values()) {
            if (e.str.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
