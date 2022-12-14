package com.helper.app.fragments;

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
import androidx.fragment.app.Fragment;

import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helper.app.R;
import com.helper.app.models.User;
import com.helper.app.presenters.OnSpeedUpdatedCallback;
import com.helper.app.utils.BitmapHelper;
import com.helper.app.utils.Constants;
import com.helper.app.utils.LocationUtils;

import java.util.List;

public class CardFragment extends Fragment implements View.OnClickListener, OnSpeedUpdatedCallback {
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private ValueEventListener valueEventListenerUser;
    private String userPath;
    private DigitSpeedView digitSpeedView;
    private ProgressBar progress;
    private ImageView qr;
    private TextView lblWelcome;
    private User user;

    private Handler handler = new Handler();
    private int counter;
    private List<Float> speeds;

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

        database = FirebaseDatabase.getInstance();
        userPath = Constants.NODE_NAME_USERS + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = database.getReference(userPath);
        lblWelcome = view.findViewById(R.id.lbl_welcome);
        qr = view.findViewById(R.id.qr);
        progress = view.findViewById(R.id.progress);
        qr.setOnClickListener(this);

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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (counter < speeds.size()) {
                    updateLocation();
                }
            }
        }, 2000);

        return view;
    }

    private void showAlertDialog() {
        AlertDialog.newInstance().show(getChildFragmentManager(), "");
    }

    private void updateLocation() {
        Float speed = speeds.get(counter);
        onSpeedUpdated(speed);

        counter++;

        if (counter < speeds.size()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateLocation();
                }
            }, 10);
        } else {
            try {
                showAlertDialog();
            }catch (Exception ex){}
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qr:
                break;
        }
    }
}
