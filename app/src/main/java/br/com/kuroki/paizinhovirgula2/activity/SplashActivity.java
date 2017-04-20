package br.com.kuroki.paizinhovirgula2.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.design.widget.Snackbar;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import br.com.kuroki.paizinhovirgula2.R;
import br.com.kuroki.paizinhovirgula2.util.DetectConnection;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (int i=0; i < 2000; i++) {

        }

        //if (checkWifiOnAndConnected()) {
        if(DetectConnection.checkInternetConnection(getApplicationContext())) {
            startActivity(new Intent(this, PrincipalActivity.class));
            finish();
        }else {
            Toast.makeText(this, "Sem conexão wifi. Favor tentar mais tarde.", Toast.LENGTH_LONG).show();

            for (int i=0; i < 10000; i++) {

            }
            finish();
        }
    }

    private boolean checkWifiOnAndConnected() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            if (wifiInfo.getNetworkId() == -1) {
                return false;
            }
            return true;
        }else {
            return false;
        }
    }
}