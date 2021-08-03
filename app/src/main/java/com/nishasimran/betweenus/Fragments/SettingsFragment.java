package com.nishasimran.betweenus.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.Utils;

import org.jetbrains.annotations.NotNull;

public class SettingsFragment extends Fragment {

    private final MainFragment mainFragment;

    private SwitchMaterial blurSwitch;
    private EditText editText;
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
        editText = parent.findViewById(R.id.settings_wallpaper_et);
        saveBtn = parent.findViewById(R.id.settings_save_btn);

        setDefaultsForViews();
    }

    private void setDefaultsForViews() {
        boolean blur = Utils.getIsBackgroundBlur(mainFragment.activity.getApplication());
        blurSwitch.setChecked(blur);

        int wallpaperId = Utils.getBackgroundInt(mainFragment.activity.getApplication());
        editText.setText(String.valueOf(wallpaperId));

        blurSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            Utils.setIsBackgroundBlur(mainFragment.activity.getApplication(), b);
        });
        saveBtn.setOnClickListener(view -> {
            if (!editText.getText().toString().trim().isEmpty()) {
                try {
                    int value = Integer.parseInt(editText.getText().toString().trim());
                    if (value < 1 || value > 17) {
                        Toast.makeText(mainFragment.activity, "Enter a value between 1-17", Toast.LENGTH_SHORT).show();
                    } else {
                        Utils.setBackgroundInt(mainFragment.activity.getApplication(), value);
                        Toast.makeText(mainFragment.activity, "Restart to see change", Toast.LENGTH_SHORT).show();
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