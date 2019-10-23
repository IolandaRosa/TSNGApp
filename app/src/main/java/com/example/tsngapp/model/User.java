package com.example.tsngapp.model;

public class User {
    private int id;
    private String name;
    private String username;
    private String email;
    private UserType type;
    private int elder_id;
    private String acessToken;


    public User(){
    }

    public User(int id, String name, String username, String email, String type, int elder_id) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        switch (type){
            case "admin": this.type=UserType.ADMIN;
            break;
            case "normal":this.type=UserType.NORMAL;
            default: this.type=UserType.UNDEFINED;
        }
        this.elder_id = elder_id;
    }

    public String getAcessToken() {
        return acessToken;
    }

    public void setAcessToken(String acessToken) {
        this.acessToken = acessToken;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public int getElder_id() {
        return elder_id;
    }

    public void setElder_id(int elder_id) {
        this.elder_id = elder_id;
    }
}
