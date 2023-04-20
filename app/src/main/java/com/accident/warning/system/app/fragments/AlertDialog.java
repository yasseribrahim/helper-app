package com.accident.warning.system.app.fragments;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.accident.warning.system.app.R;
import com.accident.warning.system.app.models.LocationModel;
import com.accident.warning.system.app.models.Message;
import com.accident.warning.system.app.models.Notification;
import com.accident.warning.system.app.models.User;
import com.accident.warning.system.app.presenters.FirebaseCallback;
import com.accident.warning.system.app.presenters.FirebasePresenter;
import com.accident.warning.system.app.presenters.NotificationsCallback;
import com.accident.warning.system.app.presenters.NotificationsPresenter;
import com.accident.warning.system.app.utils.LocationManager;
import com.accident.warning.system.app.utils.SimpleCountDownTimer;
import com.accident.warning.system.app.utils.StorageHelper;

import java.util.List;

public class AlertDialog extends DialogFragment implements FirebaseCallback, SimpleCountDownTimer.OnCountDownListener {
    private TextView btnFine, timer;
    private SimpleCountDownTimer downTimer;

    private FirebasePresenter presenter;
    private LocationManager locationManager;
    private LocationModel locationModel;

    public static AlertDialog newInstance() {
        Bundle args = new Bundle();
        AlertDialog fragment = new AlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_dialog_alrt, container, false);
        btnFine = view.findViewById(R.id.btn_fine);
        timer = view.findViewById(R.id.timer);

        presenter = new FirebasePresenter(this);
        downTimer = new SimpleCountDownTimer(0, 10, 1, this);

        btnFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        locationManager = new LocationManager();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        downTimer.start(true);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onPause() {
        super.onPause();
        downTimer.pause();
    }

    @Override
    public void onGetTokensComplete(List<String> tokens) {
        Message message = new Message();

        User user = StorageHelper.getCurrentUser();
        message.setMessage(getString(R.string.str_notification, user.getFullName()));
        message.setSenderName(locationModel.toString());
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

        presenter.send(message, tokens);
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        dismiss();

        Toast.makeText(getContext(), "General Error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onShowLoading() {
    }

    @Override
    public void onHideLoading() {
        dismiss();
    }

    @Override
    public void onCountDownActive(String time) {
        timer.setText(time);
        notification();
    }

    @Override
    public void onCountDownFinished() {
        User currentUser = StorageHelper.getCurrentUser();
        List<String> network = currentUser.getNetworks();

        btnFine.setVisibility(View.GONE);
        timer.setText(R.string.str_loading);

        locationManager.triggerLocation((AppCompatActivity) getContext(), new LocationManager.LocationListener() {
            @Override
            public void onLocationAvailable(LocationModel model) {
                locationModel = model;
                presenter.getTokens(network);
            }

            @Override
            public void onFail(Status status) {
                Toast.makeText(getContext(), "General Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationManager.stop();
    }

    @Override
    public void onSendNotificationComplete() {
        dismiss();
    }

    private void notification() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(500);
        }

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(getContext(), notification);
        ringtone.play();
    }
}