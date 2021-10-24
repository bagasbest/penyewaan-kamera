package com.masudin.omahkamerasragen.ui.booking;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class BookingViewModel extends ViewModel {

    /// KELAS VIEW MODEL BERFUNGSI UNTUK MENGAMBIL DATA DARI FIRESTORE KEMUDIAN MENERUSKANNYA KEPADA ACTIVITY YANG DI TUJU
    /// CONTOH KELAS BOOKING VIEW MODEL MENGAMBIL DATA DARI COLLECTION "booking", KEMUDIAN SETELAH DI AMBIL, DATA DIMASUKKAN KEDALAM MODEL, SETELAH ITU DITERUSKAN KEPADA ACTIVITY BOOKING, SEHINGGA ACTIVITY DAPAT MENAMPILKAN DATA BOOKING

    private final MutableLiveData<ArrayList<BookingModel>> listBooking = new MutableLiveData<>();
    final ArrayList<BookingModel> bookingModelArrayList = new ArrayList<>();

    private static final String TAG = BookingViewModel.class.getSimpleName();

    public void setListBookingCamera() {
        bookingModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("booking")
                    .whereEqualTo("category", "Kamera")
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                BookingModel model = new BookingModel();

                                model.setTransactionId("" + document.get("transactionId"));
                                model.setDateStart("" + document.get("dateStart"));
                                model.setDateFinish("" + document.get("dateFinish"));
                                model.setProductName("" + document.get("productName"));

                                bookingModelArrayList.add(model);
                            }
                            listBooking.postValue(bookingModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void setListBookingAksesoris() {
        bookingModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("booking")
                    .whereEqualTo("category", "Aksesoris")
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                BookingModel model = new BookingModel();

                                model.setTransactionId("" + document.get("transactionId"));
                                model.setDateStart("" + document.get("dateStart"));
                                model.setDateFinish("" + document.get("dateFinish"));
                                model.setProductName("" + document.get("productName"));

                                bookingModelArrayList.add(model);
                            }
                            listBooking.postValue(bookingModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public LiveData<ArrayList<BookingModel>> getBooking() {
        return listBooking;
    }

}
