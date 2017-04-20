package br.com.kuroki.paizinhovirgula2.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.kuroki.paizinhovirgula2.R;
import br.com.kuroki.paizinhovirgula2.activity.PrincipalActivity;
import br.com.kuroki.paizinhovirgula2.activity.SplashActivity;
import br.com.kuroki.paizinhovirgula2.entity.Item;
import br.com.kuroki.paizinhovirgula2.persistence.PaizinhoDataBaseHelper;
import br.com.kuroki.paizinhovirgula2.task.interfaces.ITarefaDownload;

/**
 * Created by marciokuroki on 20/02/17.
 */

// usually, subclasses of AsyncTask are declared inside the activity class.
// that way, you can easily modify the UI thread from here
public class DownloadTask extends AsyncTask<String, Integer, String> {

    private String userAgent;

    private Context context;
    private ITarefaDownload iTarefaDownload;
    private PowerManager.WakeLock mWakeLock;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private String arquivo = "";

    private int notify_id = 1;

    private PaizinhoDataBaseHelper dataBaseHelper;

    public DownloadTask(Context context, ITarefaDownload tarefaDownload) {
        this.context = context;
        this.iTarefaDownload = tarefaDownload;

        String versionName = "";
        int versionCode = 0;
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        userAgent = " AppPaizinho V." + versionName + "." + versionCode;
    }

    private PaizinhoDataBaseHelper getHelper() {
        if (dataBaseHelper == null) {
            dataBaseHelper = new PaizinhoDataBaseHelper(context);
        }
        return dataBaseHelper;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();

        notificationBuilder = new NotificationCompat.Builder(context);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder.setSmallIcon(R.drawable.ic_action_paizinho);
        notificationBuilder.setContentTitle("Preparando o Download");
        notificationBuilder.setContentText("Iniciando o Download...");

        notificationManager.notify(notify_id, notificationBuilder.build());
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        arquivo = sUrl[1];
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", userAgent);
            Log.i("#### DOWNLOAD_TASK", connection.getURL().toString());
            connection.connect();

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            Log.i("#TEST", ""+fileLength);

            // download the file
            input = connection.getInputStream();

            output = context.openFileOutput(arquivo, Context.MODE_PRIVATE);

            notificationBuilder.setContentTitle(arquivo);
            notificationBuilder.setContentText("Download do " + arquivo);
            notificationManager.notify(notify_id, notificationBuilder.build());

            byte data[] = new byte[1024];
            long total = 0;
            int previousProgress = (int) (total * 100 / fileLength);
            int count;
            while ((count = input.read(data)) != -1) {
                //Log.i("###TEST", "Entrei no laço");
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    Log.i("TESTE", "Cancelado!");
                    notificationBuilder.setContentText("Download do "+ arquivo +" cancelado!");
                    notificationBuilder.setProgress(0,0,false);
                    notificationManager.notify(notify_id, notificationBuilder.build());
                    return null;
                }
                total += count;
                //Log.i("Atualizando... ", ""+total);
                // publishing the progress....
                if (fileLength > 0) {// only if total length is known
                    if ((int) (total * 100/ fileLength) > previousProgress ) {
                        Log.i("Atualizando... ", ""+total);
                        previousProgress = (int) (total * 100 / fileLength);
                        publishProgress((int) (total * 100 / fileLength));
                    }
                }
                output.write(data, 0, count);
            }

            Long idSelecionado = Long.parseLong(sUrl[2]);
            Item itemSelecionado;
            if (idSelecionado != null) {
                itemSelecionado = getHelper().getItemDao().queryForId(idSelecionado);
                itemSelecionado.setDownloaded(true);
                Uri uri = Uri.fromFile(context.getFileStreamPath(arquivo));
                itemSelecionado.setLocalDownload(uri.getEncodedPath());

                getHelper().getItemDao().update(itemSelecionado);
            }

            return arquivo;
        } catch (NumberFormatException e) {
          e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            //return e.toString();
            e.printStackTrace();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        //super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        notificationBuilder.setContentText("Baixando..." + progress[0] + "% concluído.");
        notificationBuilder.setProgress(100,progress[0],false);
        notificationManager.notify(notify_id, notificationBuilder.build());
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        iTarefaDownload.depoisDownload(result);
        if (result != null) {
            Intent notificationIntent = new Intent(context, SplashActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            notificationBuilder.setContentText("Download do " + arquivo + " concluído!");
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setContentIntent(intent);
            notificationBuilder.setAutoCancel(true);
            notificationManager.notify(notify_id, notificationBuilder.build());
        }
    }
}