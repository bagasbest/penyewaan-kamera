package com.masudin.omahkamerasragen.ui.booking;

public class BookingModel {

    /// KELAS MODEL BERFUNGSI UNTUK TEMPAT MENAMPUNG FIELD DATA DARI FIREBASE, KEMUDIAN FIELD - FIELD DI BAWAH INI DAPAT DI PANGGIL PADA ACTIVITY YANG DIINGINKAN

    private String transactionId;
    private String dateStart;
    private String dateFinish;
    private String productName;

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
}
