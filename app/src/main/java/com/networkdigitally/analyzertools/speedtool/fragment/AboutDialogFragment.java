package com.networkdigitally.analyzertools.speedtool.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.networkdigitally.analyzertools.speedtool.R;



public class AboutDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String VersionName;
        Context context = getContext();
        try {
            //noinspection ConstantConditions
            VersionName =  context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            VersionName = getString(R.string.about_unknown);
        }
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(getText(R.string.app_name))
                .setMessage(TextUtils.concat(VersionName, "\n\n",
                        getText(R.string.privacy_brief)))
                .setPositiveButton(R.string.dialog_ok, null)
                .create();
        alertDialog.show();
        TextView message = alertDialog.findViewById(android.R.id.message);
        if (message != null) {
            message.setMovementMethod(LinkMovementMethod.getInstance());
        }
        return alertDialog;
    }
}
