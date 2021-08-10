package com.nishasimran.betweenus.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.nishasimran.betweenus.Activities.MainActivity;
import com.nishasimran.betweenus.Adapters.WallpaperAdapter;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.Utils;

import org.jetbrains.annotations.NotNull;

public class SettingsFragment extends Fragment {

    public final MainFragment mainFragment;

    private SwitchMaterial blurSwitch;
    private LinearLayout backgroundRoot, themeRoot;
    private ImageView backgroundExpansionImg, themeExpansionImg;
    private RecyclerView backgroundRecycler;
    private RadioGroup themeRadioGrp;
    private boolean isBackgroundExpanded = false, isThemesExpanded = false;
    private ConstraintLayout backgroundExpandedLayout;
    private Button saveButton;

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
        blurSwitch = parent.findViewById(R.id.settings_background_blur_switch);
        backgroundRoot = parent.findViewById(R.id.settings_background_root);
        themeRoot = parent.findViewById(R.id.settings_themes_root);
        backgroundExpansionImg = parent.findViewById(R.id.settings_background_expand);
        themeExpansionImg = parent.findViewById(R.id.settings_themes_expand);
        backgroundRecycler = parent.findViewById(R.id.settings_background_recycler);
        themeRadioGrp = parent.findViewById(R.id.settings_themes_radio_grp);
        backgroundExpandedLayout = parent.findViewById(R.id.settings_background_expanded_layout);
        saveButton = parent.findViewById(R.id.settings_save);

        setDefaultsForViews(parent);
    }

    private void setDefaultsForViews(View parent) {

        boolean backBlur = Utils.getIsBackgroundBlur(mainFragment.activity.getApplication());
        int defaultThemeInt = Utils.getThemeInt(mainFragment.activity.getApplication());
        blurSwitch.setChecked(backBlur);
        RadioButton defaultThemeRadio = themeRadioGrp.findViewWithTag(String.valueOf(defaultThemeInt));
        defaultThemeRadio.setChecked(true);

        backgroundRoot.setOnClickListener(view -> {
            if (isBackgroundExpanded) {
                backgroundExpansionImg.setImageResource(R.drawable.arrow_down);
                backgroundExpandedLayout.setVisibility(View.GONE);
                isBackgroundExpanded = false;
            } else {
                backgroundExpansionImg.setImageResource(R.drawable.arrow_up);
                backgroundExpandedLayout.setVisibility(View.VISIBLE);
                isBackgroundExpanded = true;
            }
        });
        themeRoot.setOnClickListener(view -> {
            if (isThemesExpanded) {
                themeExpansionImg.setImageResource(R.drawable.arrow_down);
                themeRadioGrp.setVisibility(View.GONE);
                isThemesExpanded = false;
            } else {
                themeExpansionImg.setImageResource(R.drawable.arrow_up);
                themeRadioGrp.setVisibility(View.VISIBLE);
                isThemesExpanded = true;
            }
        });

        WallpaperAdapter adapter = new WallpaperAdapter(mainFragment.activity, this);
        backgroundRecycler.setAdapter(adapter);

        saveButton.setOnClickListener(view -> {
            Utils.setIsBackgroundBlur(mainFragment.activity.getApplication(), blurSwitch.isChecked());
            RadioButton radioButton = parent.findViewById(themeRadioGrp.getCheckedRadioButtonId());
            try {
                int themeInt = Integer.parseInt(radioButton.getTag().toString());
                Utils.setThemeInt(mainFragment.activity.getApplication(), themeInt);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(getContext(), MainActivity.class);
            mainFragment.activity.finish();
            startActivity(intent);
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