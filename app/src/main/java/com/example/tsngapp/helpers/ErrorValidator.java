package com.example.tsngapp.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.tsngapp.R;

//Singleton
public class ErrorValidator {

    private static final ErrorValidator ourInstance = new ErrorValidator();

    public static ErrorValidator getInstance() {
        return ourInstance;
    }

    private ErrorValidator() {
    }

    //Valida conexão à internet
    public boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    //Mostra mensagem erro generica
    public void showErrorMessage(Context context, String message) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);

        builder.setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, null).show();
    }


}
