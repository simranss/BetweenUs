package com.nishasimran.betweenus.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.Utils;

import org.jetbrains.annotations.NotNull;

public class SettingsFragment extends Fragment {

    private final MainFragment mainFragment;

    private SwitchMaterial blurSwitch;
    private EditText wallpaperEditText, themeEditText;
    private Button saveBtn;

    public SettingsFragment(MainFragment fragment) {
        mainFragment = fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_settings, container, false);

        initViews(parent);

        return parent;
    }

    private void initViews(View parent) {
        blurSwitch = parent.findViewById(R.id.settings_blur_switch);
        wallpaperEditText = parent.findViewById(R.id.settings_wallpaper_et);
        themeEditText = parent.findViewById(R.id.settings_theme_et);
        saveBtn = parent.findViewById(R.id.settings_save_btn);

        setDefaultsForViews();
    }

    private void setDefaultsForViews() {
        boolean blur = Utils.getIsBackgroundBlur(mainFragment.activity.getApplication());
        blurSwitch.setChecked(blur);

        int wallpaperId = Utils.getBackgroundInt(mainFragment.activity.getApplication());
        wallpaperEditText.setText(String.valueOf((wallpaperId == -1?14:wallpaperId)));

        int themeId = Utils.getThemeInt(mainFragment.activity.getApplication());
        themeEditText.setText(String.valueOf((themeId == -1?14:themeId)));

        blurSwitch.setOnCheckedChangeListener((compoundButton, b) -> Utils.setIsBackgroundBlur(mainFragment.activity.getApplication(), b));
        saveBtn.setOnClickListener(view -> {
            if (!wallpaperEditText.getText().toString().trim().isEmpty() && !themeEditText.getText().toString().trim().isEmpty()) {
                try {
                    int wallpaperValue = Integer.parseInt(wallpaperEditText.getText().toString().trim());
                    if (wallpaperValue < 1 || wallpaperValue > 17) {
                        Toast.makeText(mainFragment.activity, "Enter a wallpaperValue between 1-17", Toast.LENGTH_SHORT).show();
                    } else {
                        Utils.setBackgroundInt(mainFragment.activity.getApplication(), wallpaperValue);
                    }
                    int themeValue = Integer.parseInt(themeEditText.getText().toString().trim());
                    if (themeValue != 1 && themeValue != 0) {
                        Toast.makeText(mainFragment.activity, "Enter a themeValue as either 0 or 1", Toast.LENGTH_SHORT).show();
                    } else {
                        Utils.setThemeInt(mainFragment.activity.getApplication(), themeValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mainFragment.activity, "Values are empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        if (mainFragment.isDocsExpanded()) {
            mainFragment.setDocsExpanded(false);
        }
        mainFragment.checkMenuItem(R.id.menu_settings);
        super.onResume();
    }
}