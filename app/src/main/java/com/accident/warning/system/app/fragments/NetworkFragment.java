package com.accident.warning.system.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.accident.warning.system.app.R;
import com.accident.warning.system.app.activities.AddUserActivity;
import com.accident.warning.system.app.activities.QRCodeScannerActivity;
import com.accident.warning.system.app.adapters.UsersAdapter;
import com.accident.warning.system.app.models.User;
import com.accident.warning.system.app.presenters.UsersCallback;
import com.accident.warning.system.app.presenters.UsersPresenter;
import com.accident.warning.system.app.utils.Constants;
import com.accident.warning.system.app.utils.StorageHelper;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

public class NetworkFragment extends Fragment implements UsersCallback, UsersAdapter.OnItemClickListener {
    private TextView btnNewUser;
    private SwipeRefreshLayout refreshLayout;
    private AutoCompleteTextView textSearch;
    private TextView message;

    private UsersPresenter presenter;
    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private List<User> users, searchedUsers;
    private boolean isGetCurrentUser;
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(NetworkFragment.this.getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    addChild(result.getContents());
                }
            });

    public static NetworkFragment newInstance() {
        Bundle args = new Bundle();
        NetworkFragment fragment = new NetworkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_network, container, false);

        presenter = new UsersPresenter(this);
        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        btnNewUser = rootView.findViewById(R.id.new_user);
        message = rootView.findViewById(R.id.message);
        textSearch = rootView.findViewById(R.id.text_search);
        isGetCurrentUser = false;

        refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanOptions options = new ScanOptions();
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                options.setPrompt("Scan");
                options.setOrientationLocked(true);
                options.setCameraId(0);  // Use a specific camera of the device
                options.setBeepEnabled(false);
                options.setCaptureActivity(QRCodeScannerActivity.class);
                options.setBarcodeImageEnabled(true);
                barcodeLauncher.launch(options);
            }
        });

        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(textSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        users = new ArrayList<>();
        searchedUsers = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersAdapter = new UsersAdapter(searchedUsers, this);
        recyclerView.setAdapter(usersAdapter);

        return rootView;
    }

    private void addChild(String id) {
        presenter.getUserById(id);
    }

    @Override
    public void onGetUserComplete(User user) {
        if (!isGetCurrentUser) {
            User currentUser = StorageHelper.getCurrentUser();
            if (user != null && !user.equals(currentUser) && !currentUser.getNetworks().contains(user.getId())) {
                Intent intent = new Intent(getContext(), AddUserActivity.class);
                intent.putExtra(Constants.ARG_OBJECT, user);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), getString(R.string.str_fail_to_add, user.getFullName()), Toast.LENGTH_LONG).show();
            }
        } else {
            isGetCurrentUser = false;
            StorageHelper.setCurrentUser(user);
            presenter.getUsers();
        }
    }

    private void load() {
        isGetCurrentUser = true;
        presenter.getUserById(StorageHelper.getCurrentUser().getId());
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    @Override
    public void onGetUsersComplete(List<User> users) {
        this.users.clear();
        this.users.addAll(users);
        search(textSearch.getText().toString());
    }

    @Override
    public void onShowLoading() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void onHideLoading() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void search(String searchedText) {
        searchedUsers.clear();
        if (!searchedText.isEmpty()) {
            for (User user : users) {
                if (isMatched(user, searchedText)) {
                    searchedUsers.add(user);
                }
            }
        } else {
            searchedUsers.addAll(users);
        }

        refresh();
    }

    private boolean isMatched(User user, String text) {
        String searchedText = text.toLowerCase();
        boolean result = user.getFullName().toLowerCase().contains(searchedText) ||
                (user.getAddress() != null && user.getAddress().toLowerCase().contains(searchedText)) ||
                (user.getPhone() != null && user.getPhone().toLowerCase().contains(searchedText)) ||
                (user.getUsername() != null && user.getUsername().toLowerCase().contains(searchedText));
        return result;
    }

    private void refresh() {
        message.setVisibility(View.GONE);
        if (searchedUsers.isEmpty()) {
            message.setVisibility(View.VISIBLE);
        }

        usersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemViewListener(int position) {
        User user = searchedUsers.get(position);
    }

    @Override
    public void onDeleteItemViewListener(int position) {
        if (position >= 0 && position < searchedUsers.size()) {
            User user = searchedUsers.get(position);
            int index = users.indexOf(user);
            searchedUsers.remove(position);
            if (index >= 0 && index < users.size()) {
                users.remove(position);
            }
            presenter.deleteNetwork(user, position);
        }
    }

    @Override
    public void onGetDeleteUserComplete(int position) {
        Toast.makeText(getContext(), R.string.str_message_delete_successfully, Toast.LENGTH_LONG).show();
        usersAdapter.notifyItemRemoved(position);
    }
}