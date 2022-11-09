package com.helper.app.presenters;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.helper.app.CustomApplication;
import com.helper.app.models.Message;
import com.helper.app.models.User;
import com.helper.app.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebasePresenter {
    private final static String URL = "https://fcm.googleapis.com/fcm/send";
    private final static String KEY = "key=" + "AAAA9coHKWM:APA91bHa4iyWgj6diRHCt0UVVjXIflCDSv4nh8WfWEqDOvNiJ9nNIA2a6_hOlmB7FpCPRQwMAOWTP1C3UMVjxAYWwi88HhZm1YaJIsYIWHdxYJTW3TQxK5KgkqR7fFVtSY1yAqARBRB9";
    private final static String CONTENT_TYPE = "application/json";

    private DatabaseReference reference;
    private FirebaseCallback callback;
    private RequestQueue requestQueue;

    public FirebasePresenter(FirebaseCallback callback) {
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.NODE_NAME_TOKENS).getRef();
        this.callback = callback;
        this.requestQueue = Volley.newRequestQueue(CustomApplication.getApplication().getApplicationContext());
    }

    public void getTokens(List<String> users) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> tokens = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (users.contains(child.getKey())) {
                        tokens.add(child.getValue(String.class));
                    }
                }

                if (callback != null) {
                    callback.onGetTokensComplete(tokens);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getToken(User user) {
        callback.onShowLoading();
        reference.child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (callback != null) {
                    callback.onGetTokenComplete(snapshot.getValue(String.class));
                }
                callback.onHideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onHideLoading();
            }
        });
    }

    public void saveToken(User user, String token) {
        reference.child(user.getId()).setValue(token);
        if (callback != null) {
            callback.onSaveTokenComplete();
        }
    }

    public void saveToken(User user) {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                saveToken(user, token);
            }
        });
    }

    public void send(Message message, List<String> tokens) {
        JSONObject notification = new JSONObject();
        JSONObject notificationContent = new JSONObject();
        JSONObject data = new JSONObject();

        try {
            data.put("title", message.getSenderName());
            data.put("message", message.getMessage());
            notificationContent.put("title", message.getSenderName());
            notificationContent.put("body", message.getMessage());
            if (tokens != null && tokens.size() > 0) {
                JSONArray registrationIds = new JSONArray();
                for (int i = 0; i < tokens.size(); i++) {
                    registrationIds.put(tokens.get(i));
                }
                notification.put("registration_ids", registrationIds);
            }
            notification.put("data", data);
            notification.put("notification", notificationContent);
            Log.e("TAG", "try");
            Log.e("TAG", notification.toString());
        } catch (JSONException ex) {
            Log.e("TAG", "onCreate: " + ex.getMessage());
        }

        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        Log.e("TAG", "sendNotification");

        JsonObjectRequest request = new JsonObjectRequest(URL, notification, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("TAG", response.toString());

                if (callback != null) {
                    callback.onSendNotificationComplete();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CustomApplication.getApplication().getApplicationContext(), "Request error", Toast.LENGTH_LONG).show();

                if (callback != null) {
                    callback.onSendNotificationComplete();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", KEY);
                params.put("Content-Type", CONTENT_TYPE);
                return params;
            }
        };
        requestQueue.add(request);
    }
}
