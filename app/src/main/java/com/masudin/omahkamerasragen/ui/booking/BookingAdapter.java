package com.masudin.omahkamerasragen.ui.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.masudin.omahkamerasragen.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private final ArrayList<BookingModel> listBooking = new ArrayList<>();
    public void setData(ArrayList<BookingModel> items) {
        listBooking.clear();
        listBooking.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listBooking.get(position));
    }

    @Override
    public int getItemCount() {
        return listBooking.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tId, dateStart, dateFinish;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tId = itemView.findViewById(R.id.transactionId);
            dateFinish = itemView.findViewById(R.id.dateFinish);
            dateStart = itemView.findViewById(R.id.dateStart);
        }

        public void bind(BookingModel model) {
            tId.setText(model.getTransactionId());
            dateStart.setText(model.getDateStart());
            dateFinish.setText(model.getDateFinish());
        }
    }
}
