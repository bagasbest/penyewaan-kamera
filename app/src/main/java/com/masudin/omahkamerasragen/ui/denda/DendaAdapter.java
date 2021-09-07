package com.masudin.omahkamerasragen.ui.denda;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.ui.history_transaction.HistoryTransactionModel;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;

public class DendaAdapter extends RecyclerView.Adapter<DendaAdapter.ViewHolder> {

    private final ArrayList<HistoryTransactionModel> listDenda = new ArrayList<>();
    public void setData(ArrayList<HistoryTransactionModel> items) {
        listDenda.clear();
        listDenda.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_denda, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listDenda.get(position));
    }

    @Override
    public int getItemCount() {
        return listDenda.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cl;
        TextView denda, transactionId, dateFinish, dateBackProduct;
        long extraCash = 0;
        long finalDateFinishInMillis = 0;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cl = itemView.findViewById(R.id.cv);
            denda = itemView.findViewById(R.id.denda);
            transactionId = itemView.findViewById(R.id.transactionId);
            dateBackProduct = itemView.findViewById(R.id.dateBackProduct);
            dateFinish = itemView.findViewById(R.id.dateFinish);

        }

        @SuppressLint("SetTextI18n")
        public void bind(HistoryTransactionModel historyTransactionModel) {

            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setMaximumFractionDigits(0);
            format.setCurrency(Currency.getInstance("IDR"));

            transactionId.setText(historyTransactionModel.getTransactionId());
            dateFinish.setText(historyTransactionModel.getDateFinish());
            dateBackProduct.setText(historyTransactionModel.getDateFinish());

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String duration = historyTransactionModel.data.get(0).getDuration();


            try {
                Date date = sdf.parse(historyTransactionModel.getData().get(0).getDateFinish());
                long dateFinishInMillis = date.getTime();
                long dateNowInMillis = System.currentTimeMillis();

                if(duration.equals("6 Jam")) {
                    finalDateFinishInMillis = dateFinishInMillis + (1000*60*60*13);
                } else if (duration.equals("12 Jam")) {
                    finalDateFinishInMillis = dateFinishInMillis + (1000*60*60*19);
                }


                if(dateNowInMillis > finalDateFinishInMillis) {
                    long diff = dateNowInMillis - finalDateFinishInMillis;
                    long extendCash = diff / (1000*60*60);
                    extraCash = extendCash * 5000;
                    denda.setText(format.format(extendCash));
                }

            } catch (ParseException e){
                e.printStackTrace();
            }


            cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), DendaDetailActivity.class);
                    intent.putExtra(DendaDetailActivity.EXTRA_DENDA, historyTransactionModel);
                    intent.putExtra(DendaDetailActivity.DENDA, extraCash);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
