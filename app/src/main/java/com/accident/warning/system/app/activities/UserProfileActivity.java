package com.accident.warning.system.app.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.accident.warning.system.app.presenters.UsersCallback;
import com.accident.warning.system.app.presenters.UsersPresenter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.accident.warning.system.app.R;
import com.accident.warning.system.app.models.User;
import com.accident.warning.system.app.utils.Constants;
import com.accident.warning.system.app.utils.LocaleHelper;

public class UserProfileActivity extends BaseActivity implements UsersCallback {
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private EditText fullName;
    private TextView lblPassword, lblConfirmPassword;
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private EditText phone;
    private EditText address;
    private TextView btnSave;
    private TextView header;
    private FrameLayout passwordContainer, passwordConfirmContainer;

    private UsersPresenter presenter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        toolbar = findViewById(R.id.toolbar);
        setupSupportedActionBar(toolbar);

        presenter = new UsersPresenter(this);
        fullName = findViewById(R.id.full_name);
        passwordContainer = findViewById(R.id.container_password);
        passwordConfirmContainer = findViewById(R.id.container_confirm_password);
        lblPassword = findViewById(R.id.lbl_password);
        lblConfirmPassword = findViewById(R.id.lbl_confirm_password);
        header = findViewById(R.id.header);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.re_password);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        progressBar = findViewById(R.id.progressBar2);
        btnSave = findViewById(R.id.btn_save);

        user = getIntent().getParcelableExtra("object");
        bindUser();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = UserProfileActivity.this.password.getText().toString().trim();
                String confirmPassword = UserProfileActivity.this.confirmPassword.getText().toString().trim();
                String username = UserProfileActivity.this.username.getText().toString().trim();
                String phone = UserProfileActivity.this.phone.getText().toString().trim();
                String address = UserProfileActivity.this.address.getText().toString().trim();
                String fullName = UserProfileActivity.this.fullName.getText().toString().trim();

                if (username.isEmpty()) {
                    UserProfileActivity.this.username.setError(getString(R.string.str_username_invalid));
                    UserProfileActivity.this.username.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    UserProfileActivity.this.username.setError(getString(R.string.str_username_invalid));
                    UserProfileActivity.this.username.requestFocus();
                    return;
                }
                if(user.getId() == null) {
                    if (password.isEmpty() || password.length() < 6) {
                        UserProfileActivity.this.password.setError(getString(R.string.str_password_length_invalid));
                        UserProfileActivity.this.password.requestFocus();
                        return;
                    }
                    if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
                        UserProfileActivity.this.password.setError(getString(R.string.str_password_confirm_invalid));
                        UserProfileActivity.this.password.requestFocus();
                        return;
                    }
                }
                if (fullName.isEmpty()) {
                    UserProfileActivity.this.phone.setError(getString(R.string.str_full_name_invalid));
                    UserProfileActivity.this.phone.requestFocus();
                    return;
                }
                if (phone.isEmpty()) {
                    UserProfileActivity.this.fullName.setError(getString(R.string.str_phone_invalid));
                    UserProfileActivity.this.fullName.requestFocus();
                    return;
                }
                if (address.isEmpty()) {
                    UserProfileActivity.this.address.setError(getString(R.string.str_address_invalid));
                    UserProfileActivity.this.address.requestFocus();
                    return;
                }

                user.setUsername(username);
                user.setPassword(password);
                user.setFullName(fullName);
                user.setPhone(phone);
                user.setAddress(address);

                if (user.getId() == null || user.getId().isEmpty()) {
                    presenter.signup(user);
                } else {
                    presenter.save(user);
                }
            }
        });
    }

    @Override
    public void onGetSignupUserComplete() {
        Toast.makeText(UserProfileActivity.this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onSaveUserComplete() {
        Toast.makeText(UserProfileActivity.this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onGetSignupUserFail(String message) {
        Toast.makeText(UserProfileActivity.this, getString(R.string.str_signup_fail, message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(UserProfileActivity.this, getString(R.string.str_signup_fail, message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onHideLoading() {
        hideProgressBar();
    }

    @Override
    public void onShowLoading() {
        showProgressBar();
    }

    private void save() {
        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        DatabaseReference node = dp.getReference(Constants.NODE_NAME_USERS);
        node.child(user.getId()).setValue(user);
        Toast.makeText(UserProfileActivity.this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
        finish();
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

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void bindUser() {
        username.setEnabled(false);
        passwordContainer.setVisibility(View.GONE);
        passwordConfirmContainer.setVisibility(View.GONE);
        lblPassword.setVisibility(View.GONE);
        lblConfirmPassword.setVisibility(View.GONE);
        if (user == null) {
            user = new User();
            username.setEnabled(true);

            passwordContainer.setVisibility(View.VISIBLE);
            passwordConfirmContainer.setVisibility(View.VISIBLE);
            lblPassword.setVisibility(View.VISIBLE);
            lblConfirmPassword.setVisibility(View.VISIBLE);
        }

        fullName.setText(user.getFullName());
        username.setText(user.getUsername());
        password.setText(user.getPassword());
        confirmPassword.setText(user.getPassword());
        phone.setText(user.getPhone());
        address.setText(user.getAddress());

        header.setText(user.getId() != null ? R.string.str_user_profile_title : R.string.str_registration);
        setActionBarTitle(getString(user.getId() != null ? R.string.str_user_profile_title : R.string.str_registration));
    }
}