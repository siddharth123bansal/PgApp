package com.patel.tanmay.assignment_login;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;


import org.json.JSONException;


public class Utils {

    private static Utils util = new Utils();

    public Utils() {

    }

    public static Utils getInstance() {
        return util;
    }

    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager manager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isAvailable() && manager.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }


    public static SharedPreferences getPrefs(Context c) {
        return c.getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getPrefsEditor(Context c) {
        return c.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
    }

    public static void saveUser(Context context , String u){
        SharedPreferences.Editor editor = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("USER",u);
        editor.commit();

    }

    public static void saveDate(Context context ,String timeObj){
        SharedPreferences.Editor editor = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("TIME",timeObj);
        editor.commit();

    }

    public static String getTime(Context c) throws JSONException {

        return getPrefs(c).getString("TIME","{}");

    }

    public static void deleteUser(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.remove("USER");
        editor.commit();

    }

    public static String getUser(Context c) throws JSONException {

       return getPrefs(c).getString("USER","{}");

    }

    public static boolean isUserPresent(Context c){
        if(getPrefs(c).contains("USER")) return true;
        else return false;
    }

}
