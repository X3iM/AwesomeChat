package com.android.greena.awesomechat;

public class User {

    private String id;
    private String name;
    private String email;
    private int avatarMockUpResources;

    public User() {}

    public User(String id, String name, String email, int avatarMockUpResources) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatarMockUpResources = avatarMockUpResources;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public int getAvatarMockUpResources() {return avatarMockUpResources;}

    public void setAvatarMockUpResources(int avatarMockUpResources) {this.avatarMockUpResources = avatarMockUpResources;}
}