package com.nishasimran.betweenus.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.navigation.NavigationView;
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
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    // viewPager
    private ViewPager2 viewPager;
    private MainFragmentAdapter adapter;

    // fragments
    private Fragment chatFragment, memoriesFragment, calendarFragment, tasksFragment, dsFragment, docsForusFragment, docsPoemsFragment, docsDreamsFragment, settingsFragment;

    private boolean isDocsExpanded = false;

    public MainFragment(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_main, container, false);

        // initialize the drawer layout
        drawerLayout = parent.findViewById(R.id.main_drawer);
        navView = parent.findViewById(R.id.main_nav_view);

        initViewPager(parent);

        // add fragments to the adapter
        addFragForViewPager();

        setDocsExpanded(false);

        // by default load the home fragment
        if (savedInstanceState == null) {
            loadFragment(0);
        }

        FirebaseDb.getInstance().goOnline();
        if (isInternetAvailable()) {
            restartConnectionChangeListener();
        }

        setDrawerListener();

        setNavigationListener();

        return parent;
    }

    private void setDrawerListener() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull @NotNull View drawerView, float slideOffset) { }
            @Override
            public void onDrawerOpened(@NonNull @NotNull View drawerView) { }
            @Override
            public void onDrawerClosed(@NonNull @NotNull View drawerView) {
                if (isDocsExpanded) {
                    if (viewPager.getCurrentItem() != 5 && viewPager.getCurrentItem() != 6 && viewPager.getCurrentItem() != 7) {
                        setDocsExpanded(false);
                    }
                }
            }
            @Override
            public void onDrawerStateChanged(int newState) { }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void setNavigationListener() {
        navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_chat:
                    Log.d(TAG, "Chat clicked");
                    loadFragment(0);

                    closeDrawer();
                    if (isDocsExpanded) {
                        setDocsExpanded(false);
                    }
                    break;
                case R.id.menu_memories:
                    Log.d(TAG, "Memories clicked");
                    loadFragment(1);

                    closeDrawer();
                    if (isDocsExpanded) {
                        setDocsExpanded(false);
                    }
                    break;
                case R.id.menu_calendar:
                    Log.d(TAG, "Calendar clicked");
                    loadFragment(2);

                    closeDrawer();
                    if (isDocsExpanded) {
                        setDocsExpanded(false);
                    }
                    break;
                case R.id.menu_tasks:
                    Log.d(TAG, "Tasks clicked");
                    loadFragment(3);

                    closeDrawer();
                    if (isDocsExpanded) {
                        setDocsExpanded(false);
                    }
                    break;
                case R.id.menu_ds:
                    Log.d(TAG, "DS clicked");
                    loadFragment(4);

                    closeDrawer();
                    if (isDocsExpanded) {
                        setDocsExpanded(false);
                    }
                    break;
                case R.id.menu_docs:
                    Log.d(TAG, "Docs clicked");
                    setDocsExpanded(true);
                    break;
                case R.id.menu_docs_forus:
                    Log.d(TAG, "For Us clicked");
                    loadFragment(5);

                    closeDrawer();
                    break;
                case R.id.menu_docs_poems:
                    Log.d(TAG, "Poems clicked");
                    loadFragment(6);

                    closeDrawer();
                    break;
                case R.id.menu_docs_dreams:
                    Log.d(TAG, "Dreams clicked");
                    loadFragment(7);

                    closeDrawer();
                    break;
                case R.id.menu_settings:
                    Log.d(TAG, "Settings clicked");
                    loadFragment(8);

                    closeDrawer();
                    if (isDocsExpanded) {
                        setDocsExpanded(false);
                    }
                    break;
                case R.id.menu_logout:
                    Log.d(TAG, "Logout clicked");

                    closeDrawer();
                    break;
                default:
                    Log.d(TAG, "default selected");
                    loadFragment(0);

                    closeDrawer();
                    if (isDocsExpanded) {
                        setDocsExpanded(false);
                    }
            }

            return true;
        });
    }

    private void addFragForViewPager() {
        adapter.addFrag(chatFragment);
        adapter.addFrag(memoriesFragment);
        adapter.addFrag(calendarFragment);
        adapter.addFrag(tasksFragment);
        adapter.addFrag(dsFragment);
        adapter.addFrag(docsForusFragment);
        adapter.addFrag(docsPoemsFragment);
        adapter.addFrag(docsDreamsFragment);
        adapter.addFrag(settingsFragment);
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

    public void checkMenuItem(@IdRes int id) {
        navView.setCheckedItem(id);
    }

    public boolean isDocsExpanded() {
        return isDocsExpanded;
    }

    public void setDocsExpanded(boolean expanded) {
        isDocsExpanded = expanded;
        navView.getMenu().findItem(R.id.menu_docs).setVisible(!isDocsExpanded);
        navView.getMenu().findItem(R.id.menu_docs_expand).setVisible(isDocsExpanded);
    }

    private void initViewPager(View parent) {
        viewPager = parent.findViewById(R.id.main_view_pager);

        chatFragment = new ChatFragment(this);
        memoriesFragment = new MemoriesFragment(this);
        calendarFragment = new CalendarFragment(this);
        tasksFragment = new TasksFragment(this);
        dsFragment = new DsFragment(this);
        docsForusFragment = new DocsForusFragment(this);
        docsPoemsFragment = new DocsPoemsFragment(this);
        docsDreamsFragment = new DocsDreamsFragment(this);
        settingsFragment = new SettingsFragment(this);

        adapter = new MainFragmentAdapter(activity);

        // set the viewPager with the adapter
        viewPager.setAdapter(adapter);
    }
}