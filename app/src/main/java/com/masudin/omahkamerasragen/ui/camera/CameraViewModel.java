package com.masudin.omahkamerasragen.ui.camera;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class CameraViewModel extends ViewModel {

    /// KELAS VIEW MODEL BERFUNGSI UNTUK MENGAMBIL DATA DARI FIRESTORE KEMUDIAN MENERUSKANNYA KEPADA ACTIVITY YANG DI TUJU
    /// CONTOH KELAS KAMERA VIEW MODEL MENGAMBIL DATA DARI COLLECTION "camera", KEMUDIAN SETELAH DI AMBIL, DATA DIMASUKKAN KEDALAM MODEL, SETELAH ITU DITERUSKAN KEPADA ACTIVITY KAMERA, SEHINGGA ACTIVITY DAPAT MENAMPILKAN DATA KAMERA

    private final MutableLiveData<ArrayList<CameraModel>> listCamera = new MutableLiveData<>();
    final ArrayList<CameraModel> cameraModelArrayList = new ArrayList<>();

    private static final String TAG = CameraViewModel.class.getSimpleName();

    public void setCameraList() {
        cameraModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("camera")
                    .orderBy("name", Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                CameraModel model = new CameraModel();

                                model.setName("" + document.get("name"));
                                model.setDescription("" + document.get("description"));
                                model.setDp("" + document.get("dp"));
                                model.setMerk("" + document.get("merk"));
                                model.setFacility("" + document.get("facility"));
                                model.setStatus("" + document.get("status"));
                                model.setPrice("" + document.get("price"));
                                model.setPrice2("" + document.get("price2"));
                                model.setPrice3("" + document.get("price3"));
                                model.setUid("" + document.get("uid"));
                                model.setStatus("" + document.get("status"));
                                model.setTotalSewa((long) document.get("totalSewa"));

                                cameraModelArrayList.add(model);
                            }
                            listCamera.postValue(cameraModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public void setCameraTerlaris() {
        cameraModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("camera")
                    .orderBy("totalSewa", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                CameraModel model = new CameraModel();

                                model.setName("" + document.get("name"));
                                model.setDescription("" + document.get("description"));
                                model.setDp("" + document.get("dp"));
                                model.setMerk("" + document.get("merk"));
                                model.setFacility("" + document.get("facility"));
                                model.setStatus("" + document.get("status"));
                                model.setPrice("" + document.get("price"));
                                model.setPrice2("" + document.get("price2"));
                                model.setPrice3("" + document.get("price3"));
                                model.setUid("" + document.get("uid"));
                                model.setStatus("" + document.get("status"));
                                model.setTotalSewa((long) document.get("totalSewa"));

                                cameraModelArrayList.add(model);
                            }
                            listCamera.postValue(cameraModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public LiveData<ArrayList<CameraModel>> getCameraList() {
        return listCamera;
    }


}
