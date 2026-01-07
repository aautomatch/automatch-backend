package com.automatch.portal.enums;

public enum UserRole {
    ADMIN("ADMIN"),
    STUDENT("STUDENT"),
    INSTRUCTOR("INSTRUCTOR");

    private String role;

    UserRole(String role){
        this.role = role;
    }

    private String getRole(){
        return role;
    }
}
