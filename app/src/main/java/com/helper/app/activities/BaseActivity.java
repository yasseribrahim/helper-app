package com.helper.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.helper.app.CustomApplication;
import com.helper.app.presenters.FirebaseCallback;
import com.helper.app.presenters.FirebasePresenter;
import com.helper.app.utils.Constants;
import com.helper.app.utils.LocaleHelper;
import com.helper.app.utils.StorageHelper;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    protected SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null && StorageHelper.getCurrentUser() != null) {
                FirebasePresenter presenter = new FirebasePresenter(new FirebaseCallback() {
                    @Override
                    public void onFailure(String message, View.OnClickListener listener) {

                    }

                    @Override
                    public void onShowLoading() {

                    }

                    @Override
                    public void onHideLoading() {

                    }
                });
                presenter.saveToken(StorageHelper.getCurrentUser());
            }
        } catch (Exception ex) {
        }
    }

    protected Locale getCurrentLanguage() {
        try {
            if (preferences == null) {
                preferences = PreferenceManager.getDefaultSharedPreferences(CustomApplication.getApplication());
            }
            return new Locale(preferences.getString("language", Locale.getDefault().getLanguage()));
        } catch (Exception ex) {
        }
        return Locale.getDefault();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LocaleHelper.onAttach(context, getCurrentLanguage().getLanguage()));
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void openHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        if(getIntent().hasExtra("title")) {
            String location = getIntent().getStringExtra("title");
            intent.putExtra(Constants.ARG_OBJECT, location);
        }
        startActivity(intent);
        finishAffinity();
    }
}
