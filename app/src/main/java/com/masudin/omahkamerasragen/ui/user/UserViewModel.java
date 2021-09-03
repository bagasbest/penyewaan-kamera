package com.masudin.omahkamerasragen.ui.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<UserModel>> listUser = new MutableLiveData<>();
    final ArrayList<UserModel> userModelArrayList = new ArrayList<>();

    private static final String TAG = UserViewModel.class.getSimpleName();

    public void setListUser(String uid) {
        userModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .whereNotEqualTo("uid", uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                UserModel model = new UserModel();

                                model.setAddress("" + document.get("address"));
                                model.setDp("" + document.get("dp"));
                                model.setEmail("" + document.get("email"));
                                model.setGender("" + document.get("gender"));
                                model.setName("" + document.get("name"));
                                model.setNik("" + document.get("nik"));
                                model.setPassword("" + document.get("password"));
                                model.setPhone("" + document.get("phone"));
                                model.setRole("" + document.get("role"));
                                model.setUid("" + document.get("uid"));
                                model.setUsername("" + document.get("username"));

                                userModelArrayList.add(model);
                            }
                            listUser.postValue(userModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public LiveData<ArrayList<UserModel>> getUser() {
        return listUser;
    }


}
