package com.masudin.omahkamerasragen.ui.user;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {

    /// KELAS MODEL BERFUNGSI UNTUK TEMPAT MENAMPUNG FIELD DATA DARI FIREBASE, KEMUDIAN FIELD - FIELD DI BAWAH INI DAPAT DI PANGGIL PADA ACTIVITY YANG DIINGINKAN

    private String address;
    private String dp;
    private String email;
    private String gender;
    private String name;
    private String nik;
    private String password;
    private String phone;
    private String role;
    private String uid;
    private String username;

    public UserModel(){}

    protected UserModel(Parcel in) {
        address = in.readString();
        dp = in.readString();
        email = in.readString();
        gender = in.readString();
        name = in.readString();
        nik = in.readString();
        password = in.readString();
        phone = in.readString();
        role = in.readString();
        uid = in.readString();
        username = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(dp);
        dest.writeString(email);
        dest.writeString(gender);
        dest.writeString(name);
        dest.writeString(nik);
        dest.writeString(password);
        dest.writeString(phone);
        dest.writeString(role);
        dest.writeString(uid);
        dest.writeString(username);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
