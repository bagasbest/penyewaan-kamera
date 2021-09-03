package com.masudin.omahkamerasragen.ui.history_transaction;

import android.os.Parcel;
import android.os.Parcelable;
import com.masudin.omahkamerasragen.ui.cart.CartModel;
import java.util.List;

public class HistoryTransactionModel implements Parcelable {

    private String transactionId;
    private String customerId;
    private String finalPrice;
    private String status;
    public List<CartModel> data;

    public HistoryTransactionModel(){}

    protected HistoryTransactionModel(Parcel in) {
        transactionId = in.readString();
        customerId = in.readString();
        finalPrice = in.readString();
        status = in.readString();
        data = in.createTypedArrayList(CartModel.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionId);
        dest.writeString(customerId);
        dest.writeString(finalPrice);
        dest.writeString(status);
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

    public List<CartModel> getData() {
        return data;
    }

    public void setData(List<CartModel> data) {
        this.data = data;
    }
}
