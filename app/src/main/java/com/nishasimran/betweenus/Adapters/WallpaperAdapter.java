package com.nishasimran.betweenus.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nishasimran.betweenus.Fragments.SettingsFragment;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.Utils;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder> {

    Context context;
    SettingsFragment fragment;

    public WallpaperAdapter(Context context, SettingsFragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.background_list_item, parent, false);
        return new WallpaperViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, int position) {
        if (position == 0) {
            holder.noWallpaper.setVisibility(View.VISIBLE);
            holder.noWallpaper.setText(R.string.no_wallpaper);
        } else {
            holder.noWallpaper.setVisibility(View.GONE);
        }
        switch (position) {
            case 0:
                holder.wallpaperImg.setImageResource(R.color.colorBackgroundBB);
                break;
            case 1:
                holder.wallpaperImg.setImageResource(R.drawable.background_01_img);
                break;
            case 2:
                holder.wallpaperImg.setImageResource(R.drawable.background_02_img);
                break;
            case 3:
                holder.wallpaperImg.setImageResource(R.drawable.background_03_img);
                break;
            case 4:
                holder.wallpaperImg.setImageResource(R.drawable.background_04_img);
                break;
            case 5:
                holder.wallpaperImg.setImageResource(R.drawable.background_05_img);
                break;
            case 6:
                holder.wallpaperImg.setImageResource(R.drawable.background_06_img);
                break;
            case 7:
                holder.wallpaperImg.setImageResource(R.drawable.background_07_img);
                break;
            case 8:
                holder.wallpaperImg.setImageResource(R.drawable.background_08_img);
                break;
            case 9:
                holder.wallpaperImg.setImageResource(R.drawable.background_09_img);
                break;
            case 10:
                holder.wallpaperImg.setImageResource(R.drawable.background_10_img);
                break;
            case 11:
                holder.wallpaperImg.setImageResource(R.drawable.background_11_img);
                break;
            case 12:
                holder.wallpaperImg.setImageResource(R.drawable.background_12_img);
                break;
            case 13:
                holder.wallpaperImg.setImageResource(R.drawable.background_13_img);
                break;
            case 14:
                holder.wallpaperImg.setImageResource(R.drawable.background_14_img);
                break;
            case 15:
                holder.wallpaperImg.setImageResource(R.drawable.background_15_img);
                break;
            case 16:
                holder.wallpaperImg.setImageResource(R.drawable.background_16_img);
                break;
            case 17:
                holder.wallpaperImg.setImageResource(R.drawable.background_17_img);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 18;
    }

    static class WallpaperViewHolder extends RecyclerView.ViewHolder {

        TextView noWallpaper;
        ImageView wallpaperImg;

        public WallpaperViewHolder(@NonNull View itemView, final WallpaperAdapter adapter) {
            super(itemView);

            wallpaperImg = itemView.findViewById(R.id.settings_background_img);
            noWallpaper = itemView.findViewById(R.id.settings_no_wallpaper);

            wallpaperImg.setOnClickListener(view -> {
                Utils.setBackgroundInt(adapter.fragment.mainFragment.activity.getApplication(), getAdapterPosition());
                Toast.makeText(adapter.context, R.string.wallpaper_selected, Toast.LENGTH_SHORT).show();
            });
        }
    }
}
