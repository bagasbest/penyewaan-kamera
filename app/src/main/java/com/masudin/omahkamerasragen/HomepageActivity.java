package com.masudin.omahkamerasragen;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.databinding.ActivityHomepageBinding;
import com.masudin.omahkamerasragen.ui.camera.CameraActivity;
import com.masudin.omahkamerasragen.ui.camera.CameraAdapter;
import com.masudin.omahkamerasragen.ui.camera.CameraViewModel;
import com.masudin.omahkamerasragen.ui.cart.CartActivity;
import com.masudin.omahkamerasragen.ui.denda.DendaActivity;
import com.masudin.omahkamerasragen.ui.history_transaction.HistoryTransactionActivity;
import com.masudin.omahkamerasragen.ui.product.ProductActivity;
import com.masudin.omahkamerasragen.ui.profile.ProfileActivity;
import com.masudin.omahkamerasragen.ui.terlaris.TerlarisActivity;
import com.masudin.omahkamerasragen.ui.terlaris.TerlarisAdapter;
import com.masudin.omahkamerasragen.ui.user.UserActivity;

import java.util.ArrayList;

public class HomepageActivity extends AppCompatActivity {

    /// inisisasi variabel supaya tidak error aplikasinya
    private ActivityHomepageBinding binding;
    private FirebaseUser user;
    private String role = "";
    private TerlarisAdapter adapter;
    @Override
    protected void onResume() {
        super.onResume();
        // ambil username dan email
        populateHeader();
        initRecylerView();
        initViewModel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// get user uid, untuk mengenali siapa yang login saat ini
        user = FirebaseAuth.getInstance().getCurrentUser();


        /// klik menu icon / ikon menu
        binding.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout navDrawer = binding.drawerLayout;
                if (!navDrawer.isDrawerOpen(GravityCompat.START))
                    navDrawer.openDrawer(GravityCompat.START);
                else navDrawer.closeDrawer(GravityCompat.END);
            }
        });

        //getRole, untuk menentukan admin atau user biasa, jika admin, maka dapat melihat daftar user
        getRole();

        // klik ikon profil
        binding.account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomepageActivity.this, ProfileActivity.class));
            }
        });

        // logout dari aplikasi
        binding.exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitDialog();
            }
        });

        // tampilkan onboarding image
        showOnboardingImage();

        /// klik lokasi pembayaran
        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.app.goo.gl/1NjEZLyem51M4sx28"));
                startActivity(browserIntent);
            }
        });

        /// klik terlaris
        binding.textView17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomepageActivity.this, TerlarisActivity.class));
            }
        });

    }


    //getRole, untuk menentukan admin atau user biasa, jika admin, maka dapat melihat daftar user
    private void getRole() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        role = ""+documentSnapshot.get("role");

                        populateDrawerItem();

                    }
                });
    }

    /// fungsi untuk menampilkan drawer atau menu yang disamping pada halaman utama
    @SuppressLint("NonConstantResourceId")
    private void populateDrawerItem() {
        // Klik navigasi pada drawer
        NavigationView navView = binding.navView;
        if(role.equals("admin")) {
            navView.getMenu().findItem(R.id.nav_user).setVisible(true);
        }
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
                case R.id.nav_user: {
                    startActivity(new Intent(HomepageActivity.this, UserActivity.class));
                    break;
                }
            }
            return true;
        });
    }


    /// onboarding merupakan gambar gambar yang otomatis slide pada halaman utama
    private void showOnboardingImage() {
        final ArrayList<SlideModel> imageList = new ArrayList<>();// Create image list


        imageList.add(new SlideModel(R.drawable.logo, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.onboarding1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.onboarding2, ScaleTypes.FIT));

        binding.imageView2.setImageList(imageList);

    }

    /// fungsi konfirmasi logout
    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah anda yakin ingin keluar apliaksi ?")
                .setIcon(R.drawable.ic_baseline_exit_to_app_24)
                .setPositiveButton("YA", (dialogInterface, i) -> {
                    // sign out dari firebase autentikasi
                    FirebaseAuth.getInstance().signOut();

                    // go to login activity
                    Intent intent = new Intent(HomepageActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogInterface.dismiss();
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }


    /// memunculkan Foto profil, nama, dan email pada navigasi menu
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
                        nameHeader.setText("" + documentSnapshot.get("name"));

                        TextView emailHeader = hView.findViewById(R.id.email);
                        emailHeader.setText("" + documentSnapshot.get("email"));

                        ImageView userDp = hView.findViewById(R.id.userDp);
                        Glide.with(HomepageActivity.this)
                                .load("" + documentSnapshot.get("dp"))
                                .error(R.drawable.ic_baseline_face_24)
                                .into(userDp);
                    }
                });
    }

    /// menampilkan data terlaris
    private void initRecylerView() {
        binding.rvTerlaris.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new TerlarisAdapter();
        binding.rvTerlaris.setAdapter(adapter);
    }

    /// mengambil data terlaris dari firebase
    private void initViewModel() {
        // tampilkan daftar artikel di halaman artikel terkait pertanian
        CameraViewModel viewModel = new ViewModelProvider(this).get(CameraViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setCameraTerlaris();
        viewModel.getCameraList().observe(this, camera -> {
            if (camera.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                adapter.setData(camera);
            } else {
                binding.noData.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }


    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}