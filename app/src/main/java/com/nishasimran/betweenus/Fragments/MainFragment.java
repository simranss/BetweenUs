package com.nishasimran.betweenus.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.nishasimran.betweenus.Activities.MainActivity;
import com.nishasimran.betweenus.Adapters.MainFragmentAdapter;
import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.DataClasses.User;
import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.R;

import org.jetbrains.annotations.NotNull;

public class MainFragment extends Fragment {

    private final String TAG = "MainFrag";

    private final AppCompatActivity activity;

    // navigation UI
    DrawerLayout drawerLayout;
    NavigationView navView;

    // viewPager
    private ViewPager2 viewPager;

    // fragments
    Fragment chatFragment, dsFragment, settingsFragment;

    public MainFragment(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_main, container, false);

        // initialize the drawer layout
        drawerLayout = parent.findViewById(R.id.main_drawer);
        navView = parent.findViewById(R.id.main_nav_view);

        viewPager = parent.findViewById(R.id.main_view_pager);

        chatFragment = new ChatFragment(this);
        dsFragment = new DsFragment(this);
        settingsFragment = new SettingsFragment(this);

        MainFragmentAdapter adapter = new MainFragmentAdapter(activity);

        // add fragments to the adapter
        adapter.addFrag(chatFragment);
        adapter.addFrag(dsFragment);
        adapter.addFrag(settingsFragment);

        // set the viewPager with the adapter
        viewPager.setAdapter(adapter);

        // by default load the home fragment
        if (savedInstanceState == null) {
            loadFragment(0);
        }

        FirebaseDb.getInstance().goOnline();
        if (isInternetAvailable()) {
            restartConnectionChangeListener();
        }

        navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_chat:
                    Log.d(TAG, "Home clicked");
                    loadFragment(0);
                    break;
                case R.id.menu_ds:
                    Log.d(TAG, "Notifications clicked");
                    loadFragment(1);
                    break;
                case R.id.menu_settings:
                    Log.d(TAG, "My Stats clicked");
                    loadFragment(2);
                    break;
                case R.id.menu_logout:
                    Log.d(TAG, "Logout clicked");
                    break;
                default:
                    Log.d(TAG, "default selected");
                    loadFragment(0);
            }

            closeDrawer();

            return true;
        });

        return parent;
    }

    private boolean isInternetAvailable() {
        if (activity instanceof MainActivity) {
            return ((MainActivity) activity).isInternetAvail();
        }
        return false;
    }

    private void restartConnectionChangeListener() {
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).restartListenerForConnectionChange();
        }
    }

    private void updateState(String state) {
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).updateState(state);
        }
    }

    private void insertUser(User user) {
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).insertUser(user);
        }
    }

    private void insertKey(Key key) {
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).insertKey(key);
        }
    }

    // load a fragment
    public void loadFragment(int index) {
        viewPager.setCurrentItem(index, true);
    }

    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public boolean isDrawerOpen() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public boolean isHomeFragment() {
        return viewPager.getCurrentItem() == 0;
    }
}