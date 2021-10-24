package com.masudin.omahkamerasragen.utils;

import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.masudin.omahkamerasragen.R;

import java.util.Calendar;

public class Background {
    /// background profil
    public static void setBackgroundImage(FragmentActivity activity, ImageView background) {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 6 && timeOfDay < 12) {
            Glide.with(activity)
                    .load(Constants.morning)
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_error)
                    .into(background);
        }
        else if(timeOfDay >= 12 && timeOfDay < 15) {
            Glide.with(activity)
                    .load(Constants.afternoon)
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_error)
                    .into(background);
        }
        else if(timeOfDay >= 15 && timeOfDay < 21) {
            Glide.with(activity)
                    .load(Constants.evening)
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_error)
                    .into(background);
        }
        else {
            Glide.with(activity)
                    .load(Constants.night)
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_error)
                    .into(background);
        }
    }
}
