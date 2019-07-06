package ir.alizeyn.neshanmock.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import ir.alizeyn.neshanmock.auth.User;
import ir.alizeyn.neshanmock.ui.activity.AuthAcitivity;

public class LoginHelper {

    public static void signUp(Context context,
                              String username,
                              String password,
                              String token) {

        SharedPreferences sp = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        sp.edit().putString("username", username)
                .putString("password", password)
                .putString("token", token)
                .apply();

    }

    public static boolean isUserSignIn(Context context) {
        SharedPreferences sp = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        return sp.getString("token", null) != null;
    }

    public static User getUser(Context context) {
        if (!isUserSignIn(context)) {
            return null;
        } else {
            SharedPreferences sp = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
            String token = sp.getString("token", null);
            String username = sp.getString("username", null);
            String password = sp.getString("password", null);
            return new User(username, password, token);
        }
    }

    public static void showLoginRequiredDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Login Required")
                .setMessage("You should login to your account to use online services.")
                .setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Login / Sign Up", (dialogInterface, i) -> {
                    Intent intent = new Intent(context, AuthAcitivity.class);
                    dialogInterface.dismiss();
                    context.startActivity(intent);
                })
                .show();
    }

}
