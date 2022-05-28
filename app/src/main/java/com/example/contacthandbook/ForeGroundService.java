package com.example.contacthandbook;

import static androidx.core.content.ContextCompat.getSystemService;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.text.NoCopySpan;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.fragment.feedback.FeedbackFragment;
import com.example.contacthandbook.model.Feedback;
import com.example.contacthandbook.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Provider;
import java.util.List;
import java.util.Map;

public class ForeGroundService extends Service {
    private static final String PREFS_NAME = "USER_INFO";
    public String userName;
    public String className;
    public Feedback fb =null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.oấyStartCommand(intent, flags, startId);
        Log.e("OnCammand", "start foreground");
        User user = getSavedInfo();
        if(user!= null){
            feedbackAdded(user);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startMyOwnForeground();
        }
        else{
            startForeground(1, new Notification());
        }

        return START_NOT_STICKY;
    }

    public void createChannel(String channelId, String channelName){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName,importance );
            channel.setDescription("show Notifications");
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }

    public User getSavedInfo() {
        User user = new User();
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        user.setUsername(sharedPref.getString("username", "contact"));
        user.setName(sharedPref.getString("name", "Contact Handbook"));
        user.setRole(sharedPref.getString("role", "student"));
        return  user;
    }

    public void feedbackAdded(User user){
        FirebaseManager firebaseManager = new FirebaseManager(this);
        firebaseManager.getClassName(user.getUsername(), user.getRole(), new FirebaseCallBack.ClassNameCallback() {
            @Override
            public void onCallback(String className) {
                if(className != null){
                    firebaseManager.listenFeedBackAdded(new FirebaseCallBack.FeedBackCallBack() {
                        @Override
                        public void onCallback(Feedback feedback) {
                            if(feedback.getReciver().equals(user.getUsername()) || feedback.getReciver().equals(className)){
                                sendFeedbackNotfication(feedback);
                            }
                        }
                    });
                }
            }
        });

    }

    public void sendFeedbackNotfication(Feedback fb){
        String channel_id = "com.example.contacthandbook.feedback_notify";
        String channel_name ="Feedback Notifications";
        createChannel(channel_id, channel_name);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(String.valueOf(R.string.feedback_intent), "feedbacks");
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT);

        Notification nf = new NotificationCompat.Builder(this, channel_id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(fb.getTitle())
                .setContentText(fb.getSender())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(fb.getContent()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat cp = NotificationManagerCompat.from(this);
        cp.notify(1, nf);
    }

    private void startMyOwnForeground(){
        String channel_id = "channelDefault";
        String channel_name = "My Background Service";
        createChannel(channel_id, channel_name);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channel_id);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        NotificationManagerCompat cp = NotificationManagerCompat.from(this);
        cp.notify(2, notification);
        startForeground(2, notification);
    }
}
