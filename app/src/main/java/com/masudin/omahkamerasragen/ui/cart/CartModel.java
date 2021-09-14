package com.masudin.omahkamerasragen.ui.cart;


import android.os.Parcel;
import android.os.Parcelable;

public class CartModel implements Parcelable {

    private String cartId;
    private String category;
    private String customerName;
    private String customerUid;
    private String dateFinish;
    private String dateStart;
    private String dp;
    private String duration;
    private String merk;
    private String name;
    private String price;
    private String totalPrice;
    private long durationEnd;

    public CartModel(){}

    protected CartModel(Parcel in) {
        cartId = in.readString();
        category = in.readString();
        customerName = in.readString();
        customerUid = in.readString();
        dateFinish = in.readString();
        dateStart = in.readString();
        dp = in.readString();
        duration = in.readString();
        merk = in.readString();
        name = in.readString();
        price = in.readString();
        totalPrice = in.readString();
        durationEnd = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cartId);
        dest.writeString(category);
        dest.writeString(customerName);
        dest.writeString(customerUid);
        dest.writeString(dateFinish);
        dest.writeString(dateStart);
        dest.writeString(dp);
        dest.writeString(duration);
        dest.writeString(merk);
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(totalPrice);
        dest.writeLong(durationEnd);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CartModel> CREATOR = new Creator<CartModel>() {
        @Override
        public CartModel createFromParcel(Parcel in) {
            return new CartModel(in);
        }

        @Override
        public CartModel[] newArray(int size) {
            return new CartModel[size];
        }
    };

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerUid() {
        return customerUid;
    }

    public void setCustomerUid(String customerUid) {
        this.customerUid = customerUid;
    }

    public String getDateFinish() {
        return dateFinish;
    }

    public void setDateFinish(String dateFinish) {
        this.dateFinish = dateFinish;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getDurationEnd() {
        return durationEnd;
    }

    public void setDurationEnd(long durationEnd) {
        this.durationEnd = durationEnd;
    }
}
