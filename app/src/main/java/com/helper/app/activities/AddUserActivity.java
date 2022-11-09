package com.helper.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.helper.app.R;
import com.helper.app.fragments.ProgressDialogFragment;
import com.helper.app.models.User;
import com.helper.app.presenters.UsersCallback;
import com.helper.app.presenters.UsersPresenter;
import com.helper.app.utils.Constants;
import com.helper.app.utils.LocaleHelper;
import com.helper.app.utils.StorageHelper;

public class AddUserActivity extends BaseActivity implements UsersCallback {
    private TextView name;
    private TextView btnAddUser;
    private ImageView image;

    private UsersPresenter presenter;
    private User user, currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        presenter = new UsersPresenter(this);

        name = findViewById(R.id.lbl_name);
        image = findViewById(R.id.image);
        btnAddUser = findViewById(R.id.btn_add_user);

        currentUser = StorageHelper.getCurrentUser();
        user = getIntent().getParcelableExtra(Constants.ARG_OBJECT);

        name.setText(user.getFullName());
        Glide.with(this).load(user.getImageProfile()).placeholder(R.drawable.ic_profile).into(image);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                if (!currentUser.getNetworks().contains(user.getId())) {
                    currentUser.getNetworks().add(user.getId());
                    user.getNetworks().add(currentUser.getId());
                    save();
                } else {
                    Toast.makeText(AddUserActivity.this, getString(R.string.str_fail_to_add, user.getFullName()), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    private void save() {
        presenter.save(currentUser, user);
    }

    @Override
    public void onSaveUserComplete() {
        Toast.makeText(this, getString(R.string.str_message_added_successfully), Toast.LENGTH_LONG).show();
        hideProgressBar();
        finish();
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
}