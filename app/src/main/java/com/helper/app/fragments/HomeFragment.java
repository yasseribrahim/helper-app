package com.helper.app.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helper.app.R;
import com.helper.app.models.User;
import com.helper.app.utils.BitmapHelper;
import com.helper.app.utils.Constants;
import com.helper.app.utils.UIHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements View.OnClickListener, Listener {
    private static final int LOCATION_SETTING_REQUEST_CODE = 1;
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private ValueEventListener valueEventListenerUser;
    private String userPath;
    private CircleImageView profileImage;
    private DigitSpeedView digitSpeedView;
    private ProgressBar progress;
    private ImageView qr;
    private TextView lblWelcome;
    private TextView username;
    private TextView grade;
    private TextView userType;
    private User user;
    private EasyWayLocation easyWayLocation;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        database = FirebaseDatabase.getInstance();
        userPath = Constants.NODE_NAME_USERS + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = database.getReference(userPath);
        profileImage = view.findViewById(R.id.profile_image);
        lblWelcome = view.findViewById(R.id.lbl_welcome);
        qr = view.findViewById(R.id.qr);
        username = view.findViewById(R.id.username);
        grade = view.findViewById(R.id.grade);
        userType = view.findViewById(R.id.user_type);
        progress = view.findViewById(R.id.progress);
        qr.setOnClickListener(this);

        valueEventListenerUser = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                lblWelcome.setText(getString(R.string.str_header_welcome, user.getFullName()));
                username.setText(user.getUsername());
                progress.setVisibility(View.GONE);
                try {
                    qr.setImageBitmap(BitmapHelper.generateQRCode(user.getId()));
                } catch (Exception ex) {
                    Toast.makeText(HomeFragment.this.getContext(), "General Error, " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                Glide.with(HomeFragment.this.getContext()).load(user.getImageProfile()).placeholder(R.drawable.ic_profile).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userReference.addValueEventListener(valueEventListenerUser);

        digitSpeedView = view.findViewById(R.id.digit_speed_view);
        easyWayLocation = new EasyWayLocation(getContext(), false, false, this);

        return view;
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

    @Override
    public void locationOn() {
        Toast.makeText(getContext(), "Location ON", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void currentLocation(Location location) {
        StringBuilder data = new StringBuilder();
        data.append(location.getLatitude());
        data.append(" , ");
        data.append(location.getLongitude());

        digitSpeedView.updateSpeed((int) location.getSpeed());
    }

    @Override
    public void locationCancelled() {
        Toast.makeText(getContext(), "Location Cancelled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOCATION_SETTING_REQUEST_CODE:
                easyWayLocation.onActivityResult(resultCode);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        easyWayLocation.startLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        easyWayLocation.endUpdates();

    }
}
