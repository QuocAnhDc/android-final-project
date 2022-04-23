package com.example.contacthandbook;


import android.content.Context;

import com.crowdfire.cfalertdialog.CFAlertDialog;

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
