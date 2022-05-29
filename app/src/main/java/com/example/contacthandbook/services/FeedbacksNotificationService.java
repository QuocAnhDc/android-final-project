package com.example.contacthandbook.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.example.contacthandbook.R;
import com.example.contacthandbook.broadcastReceiver.FeedbacksReceiver;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.model.Feedback;
import com.example.contacthandbook.model.User;
import com.google.android.gms.common.server.response.FastParser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class FeedbacksNotificationService extends Service {

    private static final String PREFS_NAME = "USER_INFO";
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
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.oấyStartCommand(intent, flags, startId);
       // Log.e("OnCammand", "start foreground");
        User user = getSavedInfo();
        if(user != null && !user.getUsername().equals("noUser")){
            feedbackAdded(user);
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(flags);
            }
        }

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            startMyOwnForeground();
//        }
//        else{
//            startForeground(1, new Notification());
//        }

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
        user.setUsername(sharedPref.getString("username", "noUser"));
        user.setName(sharedPref.getString("name", "Contact Handbook"));
        user.setRole(sharedPref.getString("role", "student"));
        return  user;
    }

    public void feedbackAdded(User user){
        if(user != null){
            FirebaseManager firebaseManager = new FirebaseManager(this);
            firebaseManager.getClassName(user.getUsername(), user.getRole(), new FirebaseCallBack.ClassNameCallback() {
                @Override
                public void onCallback(String className) {
                    if(className != null){
                        firebaseManager.listenFeedBackAdded(new FirebaseCallBack.FeedBackCallBack() {
                            @Override
                            public void onCallback(Feedback feedback) {
                                if(feedback.getReciver().equals(user.getUsername()) || feedback.getReciver().equals(className)){
                                    Date current = new Date();
                                    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
                                    try{
                                        Date fbDate = formatter.parse(feedback.getDateStr());
                                        formatter.applyPattern("yyyy-MM-dd");
                                        String format_fbDate = formatter.format(fbDate);
                                        String format_currentDate = formatter.format(current);

                                        if(format_currentDate.equals(format_fbDate)){
                                            sendFeedbackNotfication(feedback);
                                        }
                                    }
                                    catch( java.text.ParseException e){
                                        Log.e("ERROR PARSE DATE", e.toString());
                                    }
                                }

                            }

                        });
                    }
                }
            });
        }
        else{

        }

    }

    public void sendFeedbackNotfication(Feedback fb){
        String channel_id = "com.example.contacthandbook.feedback_notify";
        String channel_name ="Feedback Notifications";
        createChannel(channel_id, channel_name);

        Intent intent_notify = new Intent(this, FeedbacksReceiver.class);
        intent_notify.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent_notify.putExtra(getString(R.string.feedback_intent), getString(R.string.feedback_intent_value));
        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,uniqueInt,intent_notify,0);


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
