package com.masudin.omahkamerasragen.ui.history_transaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.masudin.omahkamerasragen.R;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

public class HistoryTransactionAdapter extends RecyclerView.Adapter<HistoryTransactionAdapter.ViewHolder> {

    private final ArrayList<HistoryTransactionModel> historyTransactionModelArrayList = new ArrayList<>();
    public void setData(ArrayList<HistoryTransactionModel> items) {
        historyTransactionModelArrayList.clear();
        historyTransactionModelArrayList.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(historyTransactionModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return historyTransactionModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cv;
        View view;
        TextView transactionId, dateStart, status, finalPrice;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.cv);
            transactionId = itemView.findViewById(R.id.transactionId);
            dateStart = itemView.findViewById(R.id.dateStart);
            status = itemView.findViewById(R.id.status);
            finalPrice = itemView.findViewById(R.id.finalPrice);
            view = itemView.findViewById(R.id.view8);
        }

        @SuppressLint("SetTextI18n")
        public void bind(HistoryTransactionModel model) {
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setMaximumFractionDigits(0);
            format.setCurrency(Currency.getInstance("IDR"));

            transactionId.setText(model.getTransactionId());
            dateStart.setText(model.getDateStart());
            status.setText(model.getStatus());
            finalPrice.setText(format.format(Integer.parseInt(model.getFinalPrice())));

            if(model.getStatus().equals("Belum Bayar")) {
                view.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.rounded_bg));
            } else {
                view.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.rounded_bg2));
            }


            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), HistoryTransactionDetailActivity.class);
                    intent.putExtra(HistoryTransactionDetailActivity.EXTRA_TRANSACTION, model);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
