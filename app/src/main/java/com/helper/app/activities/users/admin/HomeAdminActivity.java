package com.helper.app.activities.users.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.helper.app.R;
import com.helper.app.fragments.AboutFragment;
import com.helper.app.fragments.MoreFragment;
import com.helper.app.fragments.users.admin.CoursesGradesFragment;
import com.helper.app.fragments.users.admin.UsersTypesFragment;
import com.helper.app.models.User;
import com.helper.app.utils.StorageHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdminActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    protected Toolbar toolbar;
    private MenuItem previousItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
        setUpActionBar();
        setupNavDrawer();
    }

    @SuppressLint("WrongConstant")
    private void setupNavDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (previousItem != null && previousItem.getItemId() != item.getItemId()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switch (item.getItemId()) {
                                case R.id.nav_users:
                                    showFragment(UsersTypesFragment.newInstance(), R.id.container);
                                    toolbar.setTitle(R.string.str_users);
                                    break;
                                case R.id.nav_courses:
                                    showFragment(CoursesGradesFragment.newInstance(), R.id.container);
                                    toolbar.setTitle(R.string.str_courses);
                                    break;
                                case R.id.nav_about:
                                    showFragment(AboutFragment.newInstance(), R.id.container);
                                    toolbar.setTitle(R.string.str_about);
                                    break;
                                case R.id.nav_more:
                                    showFragment(MoreFragment.newInstance(), R.id.container);
                                    toolbar.setTitle(R.string.str_more);
                                    break;
                            }
                        }
                    }, 300);
                    if (previousItem != null) {
                        previousItem.setChecked(false);
                    }
                    item.setChecked(true);
                    previousItem = item;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        navigationView.setCheckedItem(R.id.nav_users);
        showFragment(UsersTypesFragment.newInstance(), R.id.container);
        toolbar.setTitle(R.string.str_users);

        CircleImageView profileImage = navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        TextView username = navigationView.getHeaderView(0).findViewById(R.id.username);
        User user = StorageHelper.getCurrentUser();
        if (user != null) {
            username.setText(user.getUsername());
            Glide.with(this).load(user.getImageProfile()).placeholder(R.drawable.ic_profile).into(profileImage);
        }
    }

    //Set up Action Bar
    @SuppressLint("WrongConstant")
    private void setUpActionBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(Gravity.START)) {
                    drawerLayout.closeDrawer(Gravity.START);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void showFragment(Fragment fragment, int fragmentContainer) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(fragmentContainer, fragment);
        transaction.commitAllowingStateLoss();
    }
}