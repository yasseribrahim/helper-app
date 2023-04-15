package com.accident.warning.system.app.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.accident.warning.system.app.presenters.UsersCallback;
import com.accident.warning.system.app.presenters.UsersPresenter;
import com.bumptech.glide.Glide;
import com.accident.warning.system.app.R;
import com.accident.warning.system.app.fragments.ProgressDialogFragment;
import com.accident.warning.system.app.models.ChatId;
import com.accident.warning.system.app.models.User;
import com.accident.warning.system.app.utils.Constants;
import com.accident.warning.system.app.utils.LocaleHelper;
import com.accident.warning.system.app.utils.StorageHelper;

public class NetworkUserProfileActivity extends BaseActivity implements UsersCallback {
    private Toolbar toolbar;
    private TextView name;
    private TextView btnChat, btnCall, btnTrack;
    private ImageView image;

    private UsersPresenter presenter;
    private User otherUser, currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_user_profile);

        presenter = new UsersPresenter(this);

        currentUser = StorageHelper.getCurrentUser();
        otherUser = getIntent().getParcelableExtra(Constants.ARG_OBJECT);

        toolbar = findViewById(R.id.toolbar);
        name = findViewById(R.id.lbl_name);
        image = findViewById(R.id.image);
        btnChat = findViewById(R.id.btn_chat);
        btnCall = findViewById(R.id.btn_call);
        btnTrack = findViewById(R.id.btn_track);

        setupSupportedActionBar(toolbar);
        setActionBarTitle(otherUser.getFullName());

        name.setText(otherUser.getFullName());
        Glide.with(this).load(otherUser.getImageProfile()).placeholder(R.drawable.ic_profile).into(image);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NetworkUserProfileActivity.this, MessagingActivity.class);
                ChatId id = new ChatId(currentUser, otherUser);
                intent.putExtra(Constants.ARG_OBJECT, id);
                startActivity(intent);
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHaveCallPermission()) {
                    openCall();
                } else {
                    requestCallPermission();
                }
            }
        });
        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermission();
            }
        });
    }

    private void openCall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + otherUser.getPhone()));
        startActivity(intent);
    }

    private void openMaps() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    public void hideProgressBar() {
        ProgressDialogFragment.hide(getSupportFragmentManager());
    }

    public void showProgressBar() {
        ProgressDialogFragment.show(getSupportFragmentManager());
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onShowLoading() {
        showProgressBar();
    }

    @Override
    public void onHideLoading() {
        hideProgressBar();
    }

    protected void setupSupportedActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolBarShadowStyle);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    protected void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestLocationPermission();
                            }
                        }).create().show();
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission();
            }
        }
        if (isHaveBackgroundLocation()) {
            openMaps();
        }
    }

    private boolean isHaveCallPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestCallPermission();
            return false;
        }
        return true;
    }

    private boolean isHaveBackgroundLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestBackgroundLocationPermission();
                return false;
            }
        }
        return true;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
    }

    private void requestCallPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL);
    }

    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            );
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        isHaveBackgroundLocation();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null)));
                    }
                }
                break;
            case MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        openMaps();
                        Toast.makeText(
                                this,
                                "Granted Background Location Permission",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            case MY_PERMISSIONS_REQUEST_CALL:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        //
                        openCall();
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66;
    private static final int MY_PERMISSIONS_REQUEST_CALL = 55;
}