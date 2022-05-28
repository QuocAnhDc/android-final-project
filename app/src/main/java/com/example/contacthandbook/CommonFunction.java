package com.example.contacthandbook;




import static android.provider.Settings.Global.getString;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.contacthandbook.model.User;

public class CommonFunction {
    public static void showCommonAlert(Context context, String title, String okButton) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(context)
                .setTitle(title)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .addButton(okButton, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    dialog.dismiss();
                });
        builder.show();
    }


}
