package com.example.tsngapp.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.tsngapp.helpers.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Elder implements Parcelable {
    private int id;
    private int treaterId;
    private String name;
    private Date birthDate;
    private UserGender gender;
    private String photoUrl;

    public Elder(int id, int treaterId, String name, Date birthDate, UserGender gender, String photoUrl) {
        this.id = id;
        this.treaterId = treaterId;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.photoUrl = photoUrl;
    }

    protected Elder(Parcel in) {
        id = in.readInt();
        treaterId = in.readInt();
        name = in.readString();
        birthDate = new Date(in.readLong());
        gender = UserGender.valueOf(in.readString());
        photoUrl = in.readString();
    }

    public static final Creator<Elder> CREATOR = new Creator<Elder>() {
        @Override
        public Elder createFromParcel(Parcel in) {
            return new Elder(in);
        }

        @Override
        public Elder[] newArray(int size) {
            return new Elder[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getTreaterId() {
        return treaterId;
    }

    public String getName() {
        return name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTreaterId(int treaterId) {
        this.treaterId = treaterId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setGender(UserGender gender) {
        this.gender = gender;
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
        dest.writeInt(treaterId);
        dest.writeString(name);
        dest.writeLong(birthDate.getTime());
        dest.writeString(gender.name());
        dest.writeString(photoUrl);
    }

    @SuppressLint("SimpleDateFormat")
    public static Elder fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
        final String dateString = jsonObject.getString("birth_date");
        final Date birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        final UserGender gender = UserGender.valueOfAbreviature(jsonObject.getString("gender"));

        return new Elder(jsonObject.getInt("id"),
                jsonObject.getInt("treater_id"),
                jsonObject.getString("name"),
                birthDate, gender,
                Constants.STORAGE_URL + jsonObject.getString("photo_url"));
    }
}
