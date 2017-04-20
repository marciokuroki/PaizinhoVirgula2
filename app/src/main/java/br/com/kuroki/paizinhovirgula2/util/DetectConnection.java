package br.com.kuroki.paizinhovirgula2.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by marciokuroki on 13/04/17.
 */

public class DetectConnection {

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getActiveNetworkInfo() != null
            && connectivityManager.getActiveNetworkInfo().isAvailable()
            && connectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }
}
