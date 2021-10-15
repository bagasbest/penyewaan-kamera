package com.masudin.omahkamerasragen.ui.product;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProductViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<ProductModel>> listProduct = new MutableLiveData<>();
    final ArrayList<ProductModel> productModelArrayList = new ArrayList<>();

    private static final String TAG = ProductViewModel.class.getSimpleName();

    public void setCameraUtilities() {
        productModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("peralatan")
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                ProductModel model = new ProductModel();

                                model.setName("" + document.get("name"));
                                model.setDescription("" + document.get("description"));
                                model.setDp("" + document.get("dp"));
                                model.setMerk("" + document.get("merk"));
                                model.setPrice("" + document.get("price"));
                                model.setPrice2("" + document.get("price2"));
                                model.setPrice3("" + document.get("price3"));
                                model.setUid("" + document.get("uid"));
                                model.setStatus("" + document.get("status"));


                                productModelArrayList.add(model);
                            }
                            listProduct.postValue(productModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public void setProductTerlaris() {
        productModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("peralatan")
                    .orderBy("totalSewa", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                ProductModel model = new ProductModel();

                                model.setName("" + document.get("name"));
                                model.setDescription("" + document.get("description"));
                                model.setDp("" + document.get("dp"));
                                model.setMerk("" + document.get("merk"));
                                model.setPrice("" + document.get("price"));
                                model.setPrice2("" + document.get("price2"));
                                model.setPrice3("" + document.get("price3"));
                                model.setUid("" + document.get("uid"));
                                model.setStatus("" + document.get("status"));


                                productModelArrayList.add(model);
                            }
                            listProduct.postValue(productModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public LiveData<ArrayList<ProductModel>> getProduct() {
        return listProduct;
    }


}
