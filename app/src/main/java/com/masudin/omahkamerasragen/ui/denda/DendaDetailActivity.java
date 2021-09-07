package com.masudin.omahkamerasragen.ui.denda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import com.masudin.omahkamerasragen.databinding.ActivityDendaDetailBinding;
import com.masudin.omahkamerasragen.ui.cart.CartAdapter;
import com.masudin.omahkamerasragen.ui.cart.CartModel;
import com.masudin.omahkamerasragen.ui.history_transaction.HistoryTransactionModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

public class DendaDetailActivity extends AppCompatActivity {

    public static final String EXTRA_DENDA = "denda";
    public static final String DENDA = "extraCash";
    private ActivityDendaDetailBinding binding;
    private HistoryTransactionModel model;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDendaDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_DENDA);
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("IDR"));

        binding.transactionId.setText("Kode Transaksi: " + model.getTransactionId());
        binding.finalPrice.setText("Biaya Denda: " + format.format(getIntent().getLongExtra(DENDA,0)));

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        initRecyclerView();


    }

    private void initRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CartAdapter adapter = new CartAdapter("transaction");
        binding.recyclerView.setAdapter(adapter);
        adapter.setData((ArrayList<CartModel>) model.data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}