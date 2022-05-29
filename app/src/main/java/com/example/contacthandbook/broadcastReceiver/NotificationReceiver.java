package com.example.contacthandbook.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.contacthandbook.MainActivity;
import com.example.contacthandbook.R;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = context.getString(R.string.notification_intent_name);
        String value = context.getString(R.string.feedback_intent_value);

        String stringValue = intent.getStringExtra(key);
        if(stringValue != null && stringValue.equals(value)){
            Intent loadMain = new Intent(context, MainActivity.class);
            context.startActivity(loadMain);
        }
    }
}
