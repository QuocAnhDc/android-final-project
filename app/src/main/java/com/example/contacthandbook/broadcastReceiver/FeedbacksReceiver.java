package com.example.contacthandbook.broadcastReceiver;






import static android.provider.Settings.System.getString;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.contacthandbook.MainActivity;
import com.example.contacthandbook.R;

public class FeedbacksReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String intent_value = intent.getStringExtra(context.getString(R.string.feedback_intent));
        if(intent_value != null && intent_value.equals(context.getString(R.string.feedback_intent_value))){
            Intent mainActivity = new Intent(context, MainActivity.class);
            mainActivity.putExtra(context.getString(R.string.feedback_intent), intent_value);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivity);
        }
    }
}
