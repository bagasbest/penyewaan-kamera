package com.masudin.omahkamerasragen.ui.history_transaction;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.masudin.omahkamerasragen.ui.cart.CartModel;
import java.util.ArrayList;
import java.util.List;

public class HistoryTransactionViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<HistoryTransactionModel>> listTransaction = new MutableLiveData<>();
    final ArrayList<HistoryTransactionModel> transactionModelArrayList = new ArrayList<>();

    private static final String TAG = HistoryTransactionViewModel.class.getSimpleName();

    public void setCustomerTransaction(String uid, String status) {
        transactionModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("transaction")
                    .whereEqualTo("customerId", uid)
                    .whereEqualTo("status", status)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                HistoryTransactionModel model = new HistoryTransactionModel();

                                model.setCustomerId("" + document.get("customerId"));
                                model.setFinalPrice("" + document.get("finalPrice"));
                                model.setStatus("" + document.get("status"));
                                model.setTransactionId("" + document.get("transactionId"));
                                model.setDateStart("" + document.get("dateStart"));
                                model.setDateFinish("" + document.get("dateFinish"));
                                model.setData(document.toObject(HistoryTransactionModel.class).data);
                                model.setName((ArrayList<String>) document.get("name"));

                                transactionModelArrayList.add(model);
                            }
                            listTransaction.postValue(transactionModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void setAllTransaction(String status) {
        transactionModelArrayList.clear();


        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("transaction")
                    .whereEqualTo("status", status)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                HistoryTransactionModel model = new HistoryTransactionModel();

                                model.setCustomerId("" + document.get("customerId"));
                                model.setFinalPrice("" + document.get("finalPrice"));
                                model.setStatus("" + document.get("status"));
                                model.setTransactionId("" + document.get("transactionId"));
                                model.setDateStart("" + document.get("dateStart"));
                                model.setDateFinish("" + document.get("dateFinish"));
                                model.setData(document.toObject(HistoryTransactionModel.class).data);
                                model.setName((ArrayList<String>) document.get("name"));


                                transactionModelArrayList.add(model);
                            }
                            listTransaction.postValue(transactionModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public LiveData<ArrayList<HistoryTransactionModel>> getTransaction() {
        return listTransaction;
    }
}
