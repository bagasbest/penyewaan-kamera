package com.masudin.omahkamerasragen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.databinding.ActivityHomepageBinding;
import com.masudin.omahkamerasragen.ui.camera.CameraActivity;
import com.masudin.omahkamerasragen.ui.cart.CartActivity;
import com.masudin.omahkamerasragen.ui.denda.DendaActivity;
import com.masudin.omahkamerasragen.ui.history_transaction.HistoryTransactionActivity;
import com.masudin.omahkamerasragen.ui.product.ProductActivity;

public class HomepageActivity extends AppCompatActivity {

    private ActivityHomepageBinding binding;
    private FirebaseUser user;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = FirebaseAuth.getInstance().getCurrentUser();

        binding.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout navDrawer = binding.drawerLayout;
                if(!navDrawer.isDrawerOpen(GravityCompat.START)) navDrawer.openDrawer(GravityCompat.START);
                else navDrawer.closeDrawer(GravityCompat.END);
            }
        });


        // Klik navigasi pada drawer
        NavigationView navView = binding.navView;
        navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_camera: {
                    startActivity(new Intent(HomepageActivity.this, CameraActivity.class));
                    break;
                }
                case R.id.nav_cart: {
                    startActivity(new Intent(HomepageActivity.this, CartActivity.class));
                    break;
                }
                case R.id.nav_denda: {
                    startActivity(new Intent(HomepageActivity.this, DendaActivity.class));
                    break;
                }
                case R.id.nav_product: {
                    startActivity(new Intent(HomepageActivity.this, ProductActivity.class));
                    break;
                }
                case R.id.nav_history_transaction: {
                    startActivity(new Intent(HomepageActivity.this, HistoryTransactionActivity.class));
                    break;
                }
            }
            return true;
        });

        // ambil username dan email
        populateHeader();

        // edit data pribadi
         binding.account.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 
             }
         });

    }


    private void populateHeader() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        NavigationView navView = binding.navView;
                        View hView = navView.getHeaderView(0);

                        TextView nameHeader = hView.findViewById(R.id.username);
                        nameHeader.setText(""+documentSnapshot.get("name"));

                        TextView emailHeader = hView.findViewById(R.id.email);
                        emailHeader.setText(""+documentSnapshot.get("email"));

                        ImageView userDp = hView.findViewById(R.id.userDp);
                        Glide.with(HomepageActivity.this)
                                .load(""+documentSnapshot.get("dp"))
                                .error(R.drawable.ic_baseline_face_24)
                                .into(userDp);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}