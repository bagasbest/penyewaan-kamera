package com.masudin.omahkamerasragen.ui.booking;

import com.masudin.omahkamerasragen.ui.cart.CartModel;

import java.util.List;

public class BookingModel {

    /// KELAS MODEL BERFUNGSI UNTUK TEMPAT MENAMPUNG FIELD DATA DARI FIREBASE, KEMUDIAN FIELD - FIELD DI BAWAH INI DAPAT DI PANGGIL PADA ACTIVITY YANG DIINGINKAN

    private String transactionId;
    private String dateStart;
    private String dateFinish;
    private String productName;
    private String category;

   public BookingModel(){}

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
