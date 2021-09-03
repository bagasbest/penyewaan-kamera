package com.masudin.omahkamerasragen.ui.denda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityDendaDetailBinding;
import com.masudin.omahkamerasragen.ui.cart.CartAdapter;
import com.masudin.omahkamerasragen.ui.cart.CartModel;
import com.masudin.omahkamerasragen.ui.history_transaction.HistoryTransactionModel;

import java.util.ArrayList;

public class DendaDetailActivity extends AppCompatActivity {

    public static final String EXTRA_DENDA = "denda";
    private ActivityDendaDetailBinding binding;
    private HistoryTransactionModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDendaDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_DENDA);
        binding.transactionId.setText(model.getTransactionId());

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