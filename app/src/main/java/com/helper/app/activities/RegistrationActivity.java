package com.helper.app.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.helper.app.R;
import com.helper.app.models.User;
import com.helper.app.presenters.UsersCallback;
import com.helper.app.presenters.UsersPresenter;
import com.helper.app.utils.LocaleHelper;

public class RegistrationActivity extends BaseActivity implements UsersCallback {
    private ProgressBar progressBar;
    private EditText fullName;
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private EditText phone;
    private EditText address;
    private TextView btnSave;

    private UsersPresenter presenter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        presenter = new UsersPresenter(this);
        fullName = findViewById(R.id.full_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.re_password);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        progressBar = findViewById(R.id.progressBar2);
        btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = RegistrationActivity.this.password.getText().toString().trim();
                String confirmPassword = RegistrationActivity.this.confirmPassword.getText().toString().trim();
                String username = RegistrationActivity.this.username.getText().toString().trim();
                String phone = RegistrationActivity.this.phone.getText().toString().trim();
                String address = RegistrationActivity.this.address.getText().toString().trim();
                String fullName = RegistrationActivity.this.fullName.getText().toString().trim();

                if (username.isEmpty()) {
                    RegistrationActivity.this.username.setError(getString(R.string.str_username_invalid));
                    RegistrationActivity.this.username.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    RegistrationActivity.this.username.setError(getString(R.string.str_username_invalid));
                    RegistrationActivity.this.username.requestFocus();
                    return;
                }
                if (password.isEmpty() || password.length() < 6) {
                    RegistrationActivity.this.password.setError(getString(R.string.str_password_length_invalid));
                    RegistrationActivity.this.password.requestFocus();
                    return;
                }
                if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
                    RegistrationActivity.this.password.setError(getString(R.string.str_password_confirm_invalid));
                    RegistrationActivity.this.password.requestFocus();
                    return;
                }
                if (fullName.isEmpty()) {
                    RegistrationActivity.this.phone.setError(getString(R.string.str_full_name_invalid));
                    RegistrationActivity.this.phone.requestFocus();
                    return;
                }
                if (phone.isEmpty()) {
                    RegistrationActivity.this.fullName.setError(getString(R.string.str_phone_invalid));
                    RegistrationActivity.this.fullName.requestFocus();
                    return;
                }
                if (address.isEmpty()) {
                    RegistrationActivity.this.address.setError(getString(R.string.str_address_invalid));
                    RegistrationActivity.this.address.requestFocus();
                    return;
                }
                user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setFullName(fullName);
                user.setPhone(phone);
                user.setAddress(address);

                presenter.signup(user);
            }
        });
    }

    @Override
    public void onGetSignupUserComplete() {
        Toast.makeText(RegistrationActivity.this, R.string.str_message_added_successfully, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onGetSignupUserFail(String message) {
        Toast.makeText(RegistrationActivity.this, getString(R.string.str_signup_fail, message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(RegistrationActivity.this, getString(R.string.str_signup_fail, message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onHideLoading() {
        hideProgressBar();
    }

    @Override
    public void onShowLoading() {
        showProgressBar();
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
}