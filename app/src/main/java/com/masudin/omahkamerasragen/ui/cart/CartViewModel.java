package com.masudin.omahkamerasragen.ui.cart;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class CartViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<CartModel>> listCart = new MutableLiveData<>();
    final ArrayList<CartModel> cartModelArrayList = new ArrayList<>();

    private static final String TAG = CartViewModel.class.getSimpleName();

    public void setListCart(String customerUid, ArrayList<CartModel> cartArray) {
        cartModelArrayList.clear();
        cartArray.clear();


        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("cart")
                    .whereEqualTo("customerUid", customerUid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                CartModel model = new CartModel();

                                model.setCartId("" + document.get("cartId"));
                                model.setCategory("" + document.get("category"));
                                model.setCustomerName("" + document.get("customerName"));
                                model.setCustomerUid("" + document.get("customerUid"));
                                model.setDateFinish("" + document.get("dateFinish"));
                                model.setDateStart("" + document.get("dateStart"));
                                model.setDp("" + document.get("dp"));
                                model.setDuration("" + document.get("duration"));
                                model.setMerk("" + document.get("merk"));
                                model.setName("" + document.get("name"));
                                model.setPrice("" + document.get("price"));
                                model.setTotalPrice("" + document.get("totalPrice"));
                                model.setDurationEnd(document.getLong("durationEnd"));
                                model.setPickHour("" + document.get("pickHour"));

                                cartModelArrayList.add(model);
                                cartArray.add(model);
                            }
                            listCart.postValue(cartModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public LiveData<ArrayList<CartModel>> getListCart() {
        return listCart;
    }

}
