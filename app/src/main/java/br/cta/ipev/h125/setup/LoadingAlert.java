package br.cta.ipev.h125.setup;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import br.cta.ipev.h125.R;

public class LoadingAlert {

    private Activity activity;
    private AlertDialog dialog;

    public LoadingAlert(Activity activity) {
        this.activity = activity;
    }

    public LoadingAlert(Context context) {
        this.activity = (Activity) context;
    }

    public void startAlertDialogO() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_progress, null));
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();

    }

    public void closeAlertDialog() {
        dialog.dismiss();
    }

}
