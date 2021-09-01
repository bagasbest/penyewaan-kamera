package com.masudin.omahkamerasragen.ui.profile;

import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileDatabase {
    public static void uploadImageToDatabase(Uri data, FragmentActivity activity, String uid) {

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        ProgressDialog mProgressDialog = new ProgressDialog(activity);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        String imageFileName = "users/userdp_" + System.currentTimeMillis() + ".png";

        mStorageRef.child(imageFileName).putFile(data)
                .addOnSuccessListener(taskSnapshot ->
                        mStorageRef.child(imageFileName).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String userDp = uri.toString();

                                    // SIMPAN LINK USER DP KE DATABASE
                                    saveUriToDatabase(userDp, mProgressDialog, activity, uid);

                                })
                                .addOnFailureListener(e -> {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(activity, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
                                    Log.d("userDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(activity, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
                    Log.d("userDp: ", e.toString());
                });
    }


    private static void saveUriToDatabase(String userDp, ProgressDialog mProgressDialog, FragmentActivity activity, String uid) {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .update("dp", userDp)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mProgressDialog.dismiss();
                        Toast.makeText(activity, "Berhasil memperbarui profil", Toast.LENGTH_SHORT).show();
                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(activity, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
