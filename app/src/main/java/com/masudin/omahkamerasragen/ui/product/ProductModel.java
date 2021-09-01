package com.masudin.omahkamerasragen.ui.product;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductModel implements Parcelable {

    private String name;
    private String merk;
    private String description;
    private String dp;
    private String price;
    private String price2;
    private String price3;
    private String uid;

    protected ProductModel(Parcel in) {
        name = in.readString();
        merk = in.readString();
        description = in.readString();
        dp = in.readString();
        price = in.readString();
        price2 = in.readString();
        price3 = in.readString();
        uid = in.readString();
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public ProductModel() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(merk);
        parcel.writeString(description);
        parcel.writeString(dp);
        parcel.writeString(price);
        parcel.writeString(price2);
        parcel.writeString(price3);
        parcel.writeString(uid);
    }
}
