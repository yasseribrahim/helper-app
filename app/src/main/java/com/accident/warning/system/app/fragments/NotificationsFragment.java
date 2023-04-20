package com.accident.warning.system.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.accident.warning.system.app.R;
import com.accident.warning.system.app.activities.MapsActivity;
import com.accident.warning.system.app.adapters.NotificationsAdapter;
import com.accident.warning.system.app.models.Notification;
import com.accident.warning.system.app.presenters.NotificationsCallback;
import com.accident.warning.system.app.presenters.NotificationsPresenter;
import com.accident.warning.system.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment implements NotificationsCallback, NotificationsAdapter.OnItemClickListener {
    private SwipeRefreshLayout refreshLayout;
    private TextView message;

    private NotificationsPresenter presenter;
    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private List<Notification> notifications;

    public static NotificationsFragment newInstance() {
        Bundle args = new Bundle();
        NotificationsFragment fragment = new NotificationsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        presenter = new NotificationsPresenter(this);
        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        message = rootView.findViewById(R.id.message);

        refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        notifications = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationsAdapter(notifications, this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private void load() {
        presenter.getNotifications();
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
    public void onGetNotificationsComplete(List<Notification> notifications) {
        this.notifications.clear();
        this.notifications.addAll(notifications);
        adapter.notifyDataSetChanged();

        message.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);
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

    @Override
    public void onItemViewListener(int position) {
        if (position >= 0 && position < notifications.size()) {
            Intent intent = new Intent(getContext(), MapsActivity.class);
            intent.putExtra(Constants.ARG_OBJECT, notifications.get(position));
            startActivity(intent);
        }
    }

    @Override
    public void onDeleteItemViewListener(int position) {
        if (position >= 0 && position < notifications.size()) {
            Notification notification = notifications.get(position);
            notifications.remove(position);
            presenter.delete(notification, position);
        }
    }

    @Override
    public void onDeleteNotificationComplete(int position) {
        Toast.makeText(getContext(), R.string.str_message_delete_successfully, Toast.LENGTH_LONG).show();
        adapter.notifyItemRemoved(position);
    }
}