package com.helper.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.helper.app.R;
import com.helper.app.adapters.MessagesAdapter;
import com.helper.app.models.ChatId;
import com.helper.app.models.Message;
import com.helper.app.models.User;
import com.helper.app.presenters.FirebaseCallback;
import com.helper.app.presenters.FirebasePresenter;
import com.helper.app.presenters.MessagingPresenter;
import com.helper.app.presenters.OnMessagingViewCallback;
import com.helper.app.presenters.UsersCallback;
import com.helper.app.presenters.UsersPresenter;
import com.helper.app.utils.Constants;
import com.helper.app.utils.LocaleHelper;
import com.helper.app.utils.StorageHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class MessagingActivity extends BaseActivity implements OnMessagingViewCallback, UsersCallback, FirebaseCallback, TextView.OnEditorActionListener {
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    EditText messageEditText;
    RelativeLayout loadingContainer;
    TextView empty;

    private MessagesAdapter adapter;
    private Message message;
    private MessagingPresenter presenter;
    private FirebasePresenter firebasePresenter;
    private UsersPresenter usersPresenter;
    private ChatId id;
    private User currentUser;
    private boolean isWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.setLocale(this, getCurrentLanguage().getLanguage());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        id = getIntent().getParcelableExtra(Constants.ARG_OBJECT);

        init();
    }

    private void init() {
        findViewById(R.id.icon_send_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        refreshLayout = findViewById(R.id.refresh_layout);
        recyclerView = findViewById(R.id.recycler_view_chat);
        messageEditText = findViewById(R.id.edit_text_message);
        loadingContainer = findViewById(R.id.loading_container);
        empty = findViewById(R.id.empty);

        messageEditText.setOnEditorActionListener(this);

        currentUser = StorageHelper.getCurrentUser();

        adapter = new MessagesAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        presenter = new MessagingPresenter();
        firebasePresenter = new FirebasePresenter(this);
        usersPresenter = new UsersPresenter(this);
        usersPresenter.getUserById(id.getOtherUser().getId());
        load();

        refreshLayout.setColorSchemeResources(R.color.refreshColor1, R.color.refreshColor2, R.color.refreshColor3, R.color.refreshColor4);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });
    }

    private void load() {
        adapter.clear();
        adapter.notifyDataSetChanged();
        presenter.getMessages(id, this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendMessage();
            return true;
        }
        return false;
    }

    private void sendMessage() {
        if (!isWait) {
            String text = messageEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                message = new Message();
                message.setSenderId(currentUser.getId());
                message.setSenderName(currentUser.getFullName());
                message.setReceiveName(id.getOther(currentUser).getFullName());
                message.setMessage(text);

                isWait = true;
                presenter.sendMessage(id, message, this);
            } else {
                Toast.makeText(this, R.string.str_please_type_message_firstly, Toast.LENGTH_SHORT).show();
                messageEditText.requestFocus();
            }
        } else {
            Toast.makeText(this, R.string.str_please_wait, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSendMessageSuccess() {
        isWait = false;
        messageEditText.setText("");
        Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();

        firebasePresenter.getToken(id.getOther(currentUser));
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetMessageSuccess(Message message) {
        loadingContainer.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);
        adapter.add(message);
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onEmptyMessaging() {
        loadingContainer.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetMessageFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onHideProgress() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onShowProgress() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void onFailure(String message, View.OnClickListener listener) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onShowLoading() {
        onShowProgress();
    }

    @Override
    public void onHideLoading() {
        onHideProgress();
    }

    @Override
    public void onSaveTokenComplete() {
    }

    @Override
    public void onGetTokenComplete(String token) {
        firebasePresenter.send(message, Arrays.asList(token));
    }

    @Override
    public void onGetUserComplete(User user) {
        id.setOtherUser(user);
    }
}