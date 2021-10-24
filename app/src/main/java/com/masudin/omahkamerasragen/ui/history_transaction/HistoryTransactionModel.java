package com.masudin.omahkamerasragen.ui.history_transaction;

import android.os.Parcel;
import android.os.Parcelable;
import com.masudin.omahkamerasragen.ui.cart.CartModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryTransactionModel implements Parcelable {

    /// KELAS MODEL BERFUNGSI UNTUK TEMPAT MENAMPUNG FIELD DATA DARI FIREBASE, KEMUDIAN FIELD - FIELD DI BAWAH INI DAPAT DI PANGGIL PADA ACTIVITY YANG DIINGINKAN

    private String transactionId;
    private String customerId;
    private String finalPrice;
    private String status;
    private String dateStart;
    private String dateFinish;
    private ArrayList<String> name;
    public List<CartModel> data;

    public HistoryTransactionModel(){}


    protected HistoryTransactionModel(Parcel in) {
        transactionId = in.readString();
        customerId = in.readString();
        finalPrice = in.readString();
        status = in.readString();
        dateStart = in.readString();
        dateFinish = in.readString();
        name = in.createStringArrayList();
        data = in.createTypedArrayList(CartModel.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionId);
        dest.writeString(customerId);
        dest.writeString(finalPrice);
        dest.writeString(status);
        dest.writeString(dateStart);
        dest.writeString(dateFinish);
        dest.writeStringList(name);
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HistoryTransactionModel> CREATOR = new Creator<HistoryTransactionModel>() {
        @Override
        public HistoryTransactionModel createFromParcel(Parcel in) {
            return new HistoryTransactionModel(in);
        }

        @Override
        public HistoryTransactionModel[] newArray(int size) {
            return new HistoryTransactionModel[size];
        }
    };

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateFinish() {
        return dateFinish;
    }

    public void setDateFinish(String dateFinish) {
        this.dateFinish = dateFinish;
    }

    public ArrayList<String> getName() {
        return name;
    }

    public void setName(ArrayList<String> name) {
        this.name = name;
    }

    public List<CartModel> getData() {
        return data;
    }

    public void setData(List<CartModel> data) {
        this.data = data;
    }
}
