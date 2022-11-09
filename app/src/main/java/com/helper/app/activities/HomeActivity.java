package com.helper.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.helper.app.R;
import com.helper.app.custom.CustomViewPager;
import com.helper.app.fragments.AboutFragment;
import com.helper.app.fragments.CardFragment;
import com.helper.app.fragments.MoreFragment;
import com.helper.app.fragments.NetworkFragment;
import com.helper.app.models.LocationModel;
import com.helper.app.presenters.OnSpeedUpdatedCallback;
import com.helper.app.utils.Constants;
import com.helper.app.utils.LocaleHelper;
import com.helper.app.utils.LocationManager;
import com.helper.app.utils.StorageHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends BaseActivity implements LocationManager.LocationListener, BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {
    private Toolbar toolbar;
    private CustomViewPager pager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private BottomNavigationView navigation;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setupSupportedActionBar(toolbar);

        navigation = findViewById(R.id.bottom_navigation);
        pager = findViewById(R.id.pager);

        pager.addOnPageChangeListener(this);
        navigation.setOnNavigationItemSelectedListener(this);
        loadFragments();

        locationManager = LocationManager.getInstance();
        locationManager.triggerLocation(this, this);

        if(getIntent().hasExtra(Constants.ARG_OBJECT)) {
            String location = getIntent().getStringExtra(Constants.ARG_OBJECT);
            String url =  "http://maps.google.com/maps?q=loc:" + location + " (" + location + ")";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            getIntent().removeExtra(Constants.ARG_OBJECT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.stop();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                pager.setCurrentItem(0);
                return true;
            case R.id.nav_network:
                pager.setCurrentItem(1);
                return true;
            case R.id.nav_about:
                pager.setCurrentItem(2);
                return true;
            case R.id.nav_more:
                pager.setCurrentItem(3);
                return true;
        }
        return false;
    }

    protected void setupSupportedActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolBarShadowStyle);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    protected void setActionBarTitle(int titleId) {
        getSupportActionBar().setTitle(titleId);
    }

    protected void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void loadFragments() {
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        sectionsPagerAdapter.titles.clear();
        sectionsPagerAdapter.fragments.clear();
        sectionsPagerAdapter.notifyDataSetChanged();

        sectionsPagerAdapter.addFrag(CardFragment.newInstance(), getString(R.string.str_home));
        sectionsPagerAdapter.addFrag(NetworkFragment.newInstance(), getString(R.string.str_network));
        sectionsPagerAdapter.addFrag(AboutFragment.newInstance(), getString(R.string.str_about));
        sectionsPagerAdapter.addFrag(MoreFragment.newInstance(), getString(R.string.str_more));

        setActionBarTitle(R.string.str_home);

        pager.setOffscreenPageLimit(4);
        pager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setActionBarTitle(sectionsPagerAdapter.titles.get(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments;
        private final List<String> titles;

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
            clearFragments(manager);
            fragments = new ArrayList<>();
            titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFrag(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        void clearFragments(FragmentManager manager) {
            List<Fragment> fragments = manager.getFragments();
            if (fragments != null) {
                FragmentTransaction transaction = manager.beginTransaction();
                for (Fragment fragment : fragments) {
                }
                transaction.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onLocationAvailable(LocationModel model) {
        try {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for(Fragment fragment : fragments) {
                if(fragment instanceof OnSpeedUpdatedCallback) {
                    ((OnSpeedUpdatedCallback) fragment).onSpeedUpdated(model.getSpeed());
                }
            }
        }catch (Exception ex) {

        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(Constants.NODE_NAME_LOCATIONS + "/" + StorageHelper.getCurrentUser().getId());
        reference.setValue(model);
    }

    @Override
    public void onFail(Status status) {
        Toast.makeText(this, "Error In Find Location", Toast.LENGTH_SHORT).show();
    }
}
