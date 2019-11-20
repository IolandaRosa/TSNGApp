package com.example.tsngapp.helpers;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;

import com.example.tsngapp.R;

public class DialogUtil {
    private static @StyleRes int getBaseDialogTheme() {
        return android.R.style.Theme_Material_Light_Dialog_NoActionBar;
    }

    public static AlertDialog createPositiveNegativeDialog(Context context,
                                                           @StringRes int positiveBtnText,
                                                           @StringRes int negativeBtnText,
                                                           @StringRes Integer title,
                                                           @StringRes Integer message,
                                                           final DialogInterface.OnClickListener callback) {
        return createPositiveNegativeDialog(context, context.getString(positiveBtnText),
                context.getString(negativeBtnText), title != null ? context.getString(title) : null,
                message != null ? context.getString(message) : null, callback);
    }

    public static AlertDialog createPositiveNegativeDialog(Context context,
                                                           String positiveBtnText,
                                                           String negativeBtnText,
                                                           String title,
                                                           String message,
                                                           final DialogInterface.OnClickListener callback) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(context, getBaseDialogTheme()))
                .setPositiveButton(positiveBtnText, callback)
                .setNegativeButton(negativeBtnText, callback);
        if (title != null) alertDialog.setTitle(title);
        if (message != null) alertDialog.setMessage(message);
        return alertDialog.create();
    }

    public static AlertDialog createOkCancelDialog(Context context,
                                                   String title,
                                                   String message,
                                                   final DialogInterface.OnClickListener callback) {
        return createPositiveNegativeDialog(context, context.getString(R.string.label_ok),
                context.getString(R.string.label_cancel), title, message, callback);
    }

    public static AlertDialog createOkCancelDialog(Context context,
                                                   @StringRes Integer title,
                                                   @StringRes Integer message,
                                                   final DialogInterface.OnClickListener callback) {
        return createPositiveNegativeDialog(context, context.getString(R.string.label_ok),
                context.getString(R.string.label_cancel), title != null ? context.getString(title) : null,
                message != null ? context.getString(message) : null, callback);
    }
}
