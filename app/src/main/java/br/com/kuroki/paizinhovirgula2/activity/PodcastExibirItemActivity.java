package br.com.kuroki.paizinhovirgula2.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import br.com.kuroki.paizinhovirgula2.R;
import br.com.kuroki.paizinhovirgula2.entity.Item;
import br.com.kuroki.paizinhovirgula2.persistence.PaizinhoDataBaseHelper;
import br.com.kuroki.paizinhovirgula2.task.DownloadTask;
import br.com.kuroki.paizinhovirgula2.task.interfaces.ITarefaDownload;
import br.com.kuroki.paizinhovirgula2.util.DateUtil;
import br.com.kuroki.paizinhovirgula2.util.UlTagHandler;

public class PodcastExibirItemActivity extends AppCompatActivity implements ITarefaDownload, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {

    private ImageView image;
    private TextView current, content;
    private ImageButton play, replay, foward, download;
    private ProgressBar progressBar;

    private Long idItem;

    private PaizinhoDataBaseHelper dataBaseHelper;
    private Item itemSelecionado;

    private String nomeArquivo;

    private MediaPlayer player;
    private long currentTime;
    private long duration;
    private boolean isPlaying;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_exibir_item);

        image = (ImageView) findViewById(R.id.apei_imagem);

        current = (TextView) findViewById(R.id.apei_current);
        progressBar = (ProgressBar) findViewById(R.id.apei_progress);

        content = (TextView) findViewById(R.id.apei_content);

        //play = (ImageButton) findViewById(R.id.apei_play);
        //replay = (ImageButton) findViewById(R.id.apei_replay);
        //foward = (ImageButton) findViewById(R.id.apei_foward);

        download = (ImageButton) findViewById(R.id.apei_download);

        idItem = getIntent().getLongExtra("itemID", -1l);

        if (idItem > 0) {
            itemSelecionado = buscarItemByID(idItem);
            if (itemSelecionado.getDuration() == null) {
                progressBar.setMax(100);
            }else {
                progressBar.setMax(itemSelecionado.getDuration().intValue());
            }
            progressBar.setProgress(itemSelecionado.getResumePosition());
            current.setText(DateUtil.converterLongToString(itemSelecionado.getResumePosition().longValue()));

            int icone = R.mipmap.ic_trico;
            if (itemSelecionado.getTipo() == Item.PODCAST_TIPO_SINUCA_DE_BICOS)
                icone = R.mipmap.ic_sinuca;

            Picasso.with(this)
                    .load(itemSelecionado.getImage())
                    .placeholder(icone)
                    .error(icone)
                    .into(image);

            if (Build.VERSION.SDK_INT >= 24) {
                content.setText(Html.fromHtml(itemSelecionado.getContent(), Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM, null, new UlTagHandler()));
            } else {
                String aux = itemSelecionado.getContent();
                if (aux == null)
                    aux = itemSelecionado.getDescription();
                aux = aux.replace("<li>", "-");
                aux = aux.replace("</li>", "<br>");
                content.setText(Html.fromHtml(aux));
                content.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemSelecionado != null) {
                    DownloadTask downloadTask = new DownloadTask(PodcastExibirItemActivity.this, PodcastExibirItemActivity.this);
                    nomeArquivo = itemSelecionado.getNomePodcast() + "_" + itemSelecionado.getNumeroEpisodio() + ".mp3";
                    downloadTask.execute(itemSelecionado.getUrl(), nomeArquivo, itemSelecionado.getId().toString());
                }
            }
        });

        if(isPlaying){
            playMusic(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(player != null){
            duration = player.getDuration();
            currentTime = player.getCurrentPosition();
            atualizarCurrentTimeNoBanco();
        }
    }

    private void atualizarCurrentTimeNoBanco() {
        if (itemSelecionado != null) {
            if (currentTime > 0) {
                try{
                    itemSelecionado.setResumePosition((int) currentTime);
                    getHelper().getItemDao().update(itemSelecionado);
                }catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(player != null){
            player.stop();
            player.release();
            player = null;
        }
    }

    public void replay15SecMusic(View view) {
        if (currentTime <= 15000) {
            currentTime = 0;
        }else {
            currentTime -= 15000;
        }

        player.seekTo((int) currentTime);
        progressBar.setProgress((int) currentTime);

        player.start();
        isPlaying = true;
        updateTimeMusicThread(player, current);
    }

    public void foward15SecMusic(View view) {
        if (duration >= currentTime + 15000) {
            currentTime = duration;
        }else {
            currentTime += 15000;
        }

        progressBar.setProgress((int) currentTime);
        player.seekTo((int) currentTime);

        player.start();
        isPlaying = true;
        updateTimeMusicThread(player, current);
    }

    public void playMusic(View view) {
        if (player == null) {
            try {
                if (itemSelecionado != null) {
                    currentTime = itemSelecionado.getResumePosition();

                    if (itemSelecionado.getLocalDownload() != null) {

                        File arquivo = new File(itemSelecionado.getLocalDownload());
                        if (arquivo.length() == itemSelecionado.getSizeMedia() ) {
                            //TODO carregar o audio do app se o tamanho do arquivo for igual ao da descrição.
                            Uri uri = Uri.fromFile(arquivo);
                            player = new MediaPlayer();
                            player.setDataSource(PodcastExibirItemActivity.this, uri);
                            player.prepareAsync();
                        }else {
                            //TODO stream de audio
                            player = new MediaPlayer();
                            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            player.setDataSource(itemSelecionado.getUrl());
                            player.prepareAsync();
                        }
                    }else {
                        //TODO stream de audio
                        player = new MediaPlayer();
                        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        player.setDataSource(itemSelecionado.getUrl());
                        player.prepareAsync();
                    }
                }
                player.seekTo((int) currentTime);
                player.setOnBufferingUpdateListener(this);
                player.setOnCompletionListener(this);
                player.setOnErrorListener(this);
                player.setOnPreparedListener(this);
                player.setOnSeekCompleteListener(this);
            }
            catch (IllegalArgumentException e) { e.printStackTrace(); }
            catch (SecurityException e) { e.printStackTrace(); }
            catch (IllegalStateException e) { e.printStackTrace(); }
            catch (IOException e) {e.printStackTrace();}
        }else {
            player.seekTo((int) currentTime);
            player.start();
            isPlaying = true;
            updateTimeMusicThread(player, current);
        }



    }

    public void pauseMusic(View view){
        isPlaying = false;
        if(player != null){
            player.pause();
            currentTime = player.getCurrentPosition();
            atualizarCurrentTimeNoBanco();
        }
    }


    public void stopMusic(View view){
        isPlaying = false;
        if(player != null){
            atualizarCurrentTimeNoBanco();

            progressBar.setProgress(0);
            player.stop();
            player.release();
            player = null;
            currentTime = 0;
            current.setText("");
        }
    }

    public void updateTimeMusic(final long duration, final long currentTime, final TextView view){
        runOnUiThread(new Runnable(){
            public void run(){
                long aux;
                int minute, second;

                // DURATION
                aux = duration / 1000;
                minute = (int) (aux / 60);
                second = (int) (aux % 60);
                String sDuration = minute < 10 ? "0"+minute : minute+"";
                sDuration += ":"+(second < 10 ? "0"+second : second);

                // CURRENTTIME
                aux = currentTime / 1000;
                minute = (int) (aux / 60);
                second = (int) (aux % 60);
                String sCurrentTime = minute < 10 ? "0"+minute : minute+"";
                sCurrentTime += ":"+(second < 10 ? "0"+second : second);

                view.setText(sDuration +" / "+sCurrentTime);

                progressBar.setProgress((int) currentTime);
            }
        });
    }

    public void updateTimeMusicThread(final MediaPlayer mp, final TextView view){
        new Thread(){
            public void run(){
                while(isPlaying){
                    try{
                        updateTimeMusic(mp.getDuration(), mp.getCurrentPosition(), view);
                        Thread.sleep(1000);
                    }
                    catch(IllegalStateException e){ e.printStackTrace(); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                }
            }
        }.start();
    }

    private PaizinhoDataBaseHelper getHelper() {
        if (dataBaseHelper == null)
            dataBaseHelper = new PaizinhoDataBaseHelper(this);
        return dataBaseHelper;
    }

    private Item buscarItemByID(long idItem) {
        try {
            Item resultado = getHelper().getItemDao().queryForId(idItem);
            return resultado;
        }catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void depoisDownload(String audio) {
        try {
            File arquivoAudio = getFileStreamPath(nomeArquivo);
            if (arquivoAudio.exists()) {
                Toast.makeText(this, arquivoAudio.getName(), Toast.LENGTH_LONG);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPlaying = true;

        mp.start();
        mp.setLooping(false);
        mp.setVolume(1, 1);
        mp.seekTo((int)currentTime);

        updateTimeMusicThread(mp, current);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    public void onClickSeekProgressBar(View view) {
        //TODO pesquisar sobre o seek progressbar
    }
}
