package com.penyewaan.kamera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.penyewaan.kamera.databinding.ActivityHomepageBinding;
import com.penyewaan.kamera.ui.camera.CameraActivity;
import com.penyewaan.kamera.ui.cart.CartActivity;
import com.penyewaan.kamera.ui.denda.DendaActivity;
import com.penyewaan.kamera.ui.history_transaction.HistoryTransactionActivity;
import com.penyewaan.kamera.ui.product.ProductActivity;

import org.jetbrains.annotations.NotNull;

public class HomepageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityHomepageBinding binding;
    private FirebaseUser user;

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

        // ambil username dan email
        populateHeader();

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
                        NavigationView navView = (NavigationView) binding.navView.getHeaderView(0);

                        TextView nameHeader = navView.findViewById(R.id.username);
                        nameHeader.setText(""+documentSnapshot.get("name"));

                        TextView emailHeader = navView.findViewById(R.id.email);
                        emailHeader.setText(""+documentSnapshot.get("email"));

                        ImageView userDp = navView.findViewById(R.id.userDp);
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
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
    }
}