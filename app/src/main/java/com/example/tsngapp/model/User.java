package com.example.tsngapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private int id;
    private String name;
    private String username;
    private String email;
    private UserType type;
    private int elder_id;
    private String acessToken;
    private String photoUrl;

    public User() {}

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
        this.photoUrl="";
    }

    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
        username = in.readString();
        email = in.readString();
        type = UserType.valueOf(in.readString());
        elder_id = in.readInt();
        acessToken = in.readString();
        photoUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(type.name());
        dest.writeInt(elder_id);
        dest.writeString(acessToken);
        dest.writeString(photoUrl);
    }
}
