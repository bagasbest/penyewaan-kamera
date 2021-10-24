package com.masudin.omahkamerasragen.ui.user;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.masudin.omahkamerasragen.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    /// INISIASI ARRAY LIST SEBAGAI PENAMPUNG LIST DATA user
    private final ArrayList<UserModel> listUser = new ArrayList<>();
    public void setData(ArrayList<UserModel> items) {
        listUser.clear();
        listUser.addAll(items);
        notifyDataSetChanged();
    }


    /// CASTING LAYOUT KE item_user SUPAYA LIST DENDA DAPAT DI TAMPILKAN BERBENTUK LIST
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listUser.get(position));
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }


    //// FUNGSI UNTUK MEMASUKKAN DATA DARI ARRAY LIST DIATAS KEDALAM ATRIBUT, SEHINGGA TERLIHAT nama, DLL PADA LIST
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cv;
        TextView name;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            name = itemView.findViewById(R.id.name);
        }

        public void bind(UserModel model) {
            name.setText(model.getName());

            /// klik item user, menuju detail user
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), UserDetailActivity.class);
                    intent.putExtra(UserDetailActivity.EXTRA_USER, model);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
