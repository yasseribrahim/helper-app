package com.accident.warning.system.app.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.accident.warning.system.app.R;
import com.accident.warning.system.app.models.LocationModel;
import com.accident.warning.system.app.models.Message;
import com.accident.warning.system.app.models.Notification;
import com.accident.warning.system.app.models.User;
import com.accident.warning.system.app.presenters.FirebaseCallback;
import com.accident.warning.system.app.presenters.FirebasePresenter;
import com.accident.warning.system.app.presenters.NotificationsCallback;
import com.accident.warning.system.app.presenters.NotificationsPresenter;
import com.accident.warning.system.app.presenters.OnSpeedUpdatedCallback;
import com.accident.warning.system.app.utils.BitmapHelper;
import com.accident.warning.system.app.utils.Constants;
import com.accident.warning.system.app.utils.LocationManager;
import com.accident.warning.system.app.utils.LocationUtils;
import com.accident.warning.system.app.utils.StorageHelper;
import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CardFragment extends Fragment implements FirebaseCallback, View.OnClickListener, OnSpeedUpdatedCallback {
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private ValueEventListener valueEventListenerUser;
    private String userPath;
    private DigitSpeedView digitSpeedView;
    private ProgressBar progress;
    private ImageView qr;
    private TextView lblWelcome;
    private TextView btnStartTest;
    private TextView btnNeedHelp;
    private TextView lblAlertHint;
    private User user;

    private FirebasePresenter presenter;
    private LocationManager locationManager;
    private LocationModel locationModel;
    private Handler handler = new Handler();
    private int counter;
    private List<Float> speeds;

    private float previousSpeed = -1;
    private long previousTime = -1;

    public static CardFragment newInstance() {
        Bundle args = new Bundle();

        CardFragment fragment = new CardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);

        presenter = new FirebasePresenter(this);
        locationManager = new LocationManager();

        database = FirebaseDatabase.getInstance();
        userPath = Constants.NODE_NAME_USERS + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = database.getReference(userPath);
        lblWelcome = view.findViewById(R.id.lbl_welcome);
        btnStartTest = view.findViewById(R.id.btn_start_test);
        btnNeedHelp = view.findViewById(R.id.btn_need_help);
        lblAlertHint = view.findViewById(R.id.lbl_alert_hint);
        qr = view.findViewById(R.id.qr);
        progress = view.findViewById(R.id.progress);
        qr.setOnClickListener(this);

        lblAlertHint.setText(getString(R.string.str_alert_hint, LocationUtils.TEST_SPEED + ""));

        valueEventListenerUser = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                lblWelcome.setText(getString(R.string.str_header_welcome, user.getFullName()));
                progress.setVisibility(View.GONE);
                try {
                    qr.setImageBitmap(BitmapHelper.generateQRCode(user.getId()));
                } catch (Exception ex) {
                    Toast.makeText(CardFragment.this.getContext(), "General Error, " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userReference.addValueEventListener(valueEventListenerUser);

        digitSpeedView = view.findViewById(R.id.digit_speed_view);
        digitSpeedView.updateSpeed(0);

        speeds = LocationUtils.getInstance().getSpeedTest();

        btnNeedHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager.triggerLocation((AppCompatActivity) getContext(), new LocationManager.LocationListener() {
                    @Override
                    public void onLocationAvailable(LocationModel model) {
                        User currentUser = StorageHelper.getCurrentUser();
                        List<String> network = currentUser.getNetworks();

                        locationModel = model;
                        if (!network.isEmpty()) {
                            presenter.getTokens(network);
                        } else {
                            Toast.makeText(getContext(), R.string.str_setup_network, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFail(Status status) {
                        Toast.makeText(getContext(), "General Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter = 0;
                btnStartTest.setText(R.string.str_please_wait);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (counter < speeds.size()) {
                            updateLocation();
                        }
                    }
                }, 2000);
            }
        });

        return view;
    }

    @Override
    public void onGetTokensComplete(List<String> tokens) {
        Message message = new Message();

        User user = StorageHelper.getCurrentUser();
        message.setMessage(getString(R.string.str_notification, user.getFullName()));
        message.setSenderName(locationModel.toString());
        presenter.send(message, tokens);

        Notification notification = new Notification();
        notification.setMessage(message.getMessage());
        notification.setLocation(locationModel.toString());
        new NotificationsPresenter(new NotificationsCallback() {
            @Override
            public void onFailure(String message, View.OnClickListener listener) {

            }

            @Override
            public void onShowLoading() {

            }

            @Override
            public void onHideLoading() {

            }
        }).save(notification, user.getNetworks().toArray(new String[]{}));
    }

    @Override
    public void onSendNotificationComplete() {
        Toast.makeText(getContext(), "Message sent", Toast.LENGTH_LONG).show();
    }

    private void showAlertDialog() {
        AlertDialog.newInstance().show(getChildFragmentManager(), "");
    }

    private void updateLocation() {
        Float speed = speeds.get(counter);
        onSpeedUpdated(speed);

        if (previousSpeed < 0) {
            previousSpeed = speed;
            previousTime = System.currentTimeMillis(); // current time by milli seconds
        }

        float differenceSpeed = previousSpeed - speed;
        long differenceTime = System.currentTimeMillis() - previousTime;
        if (speed == 0 && differenceSpeed >= LocationUtils.TEST_SPEED && differenceTime < 1000) {
            try {
                showAlertDialog();

                btnStartTest.setText(R.string.str_start_test);
            } catch (Exception ex) {
            }
        } else {
            counter++;
            previousSpeed = speed;
            previousTime = System.currentTimeMillis();
            if (counter < speeds.size()) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateLocation();
                    }
                }, 10);
            }
        }
    }

    @Override
    public void onSpeedUpdated(float speed) {
        digitSpeedView.updateSpeed((int) speed);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userReference != null && valueEventListenerUser != null) {
            userReference.removeEventListener(valueEventListenerUser);
        }
        valueEventListenerUser = null;
        userReference = null;
        database = null;
        locationManager.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qr:
                break;
        }
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onShowLoading() {
        ProgressDialogFragment.show(getChildFragmentManager());
    }

    @Override
    public void onHideLoading() {
        ProgressDialogFragment.hide(getChildFragmentManager());
    }
}
