package com.masudin.omahkamerasragen.ui.camera;

import android.os.Parcel;
import android.os.Parcelable;

public class CameraModel implements Parcelable {

    /// KELAS MODEL BERFUNGSI UNTUK TEMPAT MENAMPUNG FIELD DATA DARI FIREBASE, KEMUDIAN FIELD - FIELD DI BAWAH INI DAPAT DI PANGGIL PADA ACTIVITY YANG DIINGINKAN

    private String name;
    private String description;
    private String facility;
    private String merk;
    private String price;
    private String price2;
    private String price3;
    private String uid;
    private String dp;
    private String status;
    private long totalSewa;

   public CameraModel() {}

    protected CameraModel(Parcel in) {
        name = in.readString();
        description = in.readString();
        facility = in.readString();
        merk = in.readString();
        price = in.readString();
        price2 = in.readString();
        price3 = in.readString();
        uid = in.readString();
        dp = in.readString();
        status = in.readString();
        totalSewa = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(facility);
        dest.writeString(merk);
        dest.writeString(price);
        dest.writeString(price2);
        dest.writeString(price3);
        dest.writeString(uid);
        dest.writeString(dp);
        dest.writeString(status);
        dest.writeLong(totalSewa);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CameraModel> CREATOR = new Creator<CameraModel>() {
        @Override
        public CameraModel createFromParcel(Parcel in) {
            return new CameraModel(in);
        }

        @Override
        public CameraModel[] newArray(int size) {
            return new CameraModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice2() {
        return price2;
    }

    public void setPrice2(String price2) {
        this.price2 = price2;
    }

    public String getPrice3() {
        return price3;
    }

    public void setPrice3(String price3) {
        this.price3 = price3;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTotalSewa() {
        return totalSewa;
    }

    public void setTotalSewa(long totalSewa) {
        this.totalSewa = totalSewa;
    }
}
