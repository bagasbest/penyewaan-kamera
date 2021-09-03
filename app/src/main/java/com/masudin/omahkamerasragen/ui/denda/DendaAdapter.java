package com.masudin.omahkamerasragen.ui.denda;

import android.content.Intent;
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

import java.util.ArrayList;

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

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cl = itemView.findViewById(R.id.cv);
            denda = itemView.findViewById(R.id.delete);
            transactionId = itemView.findViewById(R.id.transactionId);
            dateBackProduct = itemView.findViewById(R.id.dateBackProduct);
            dateFinish = itemView.findViewById(R.id.dateFinish);

        }

        public void bind(HistoryTransactionModel historyTransactionModel) {
            transactionId.setText(historyTransactionModel.getTransactionId());
            dateFinish.setText(historyTransactionModel.getData().get(0).getDateFinish());
            dateBackProduct.setText(historyTransactionModel.getData().get(0).getDateFinish());
            denda.setText("0");


            cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), DendaDetailActivity.class);
                    intent.putExtra(DendaDetailActivity.EXTRA_DENDA, historyTransactionModel);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
