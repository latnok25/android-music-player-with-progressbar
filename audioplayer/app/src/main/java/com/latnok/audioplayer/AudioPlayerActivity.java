package com.latnok.audioplayer;

/**
 * Created by k on 11/30/2017.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.latnok.audioplayer.controls.Controls;
import com.latnok.audioplayer.service.SongService;
import com.latnok.audioplayer.util.PlayerConstants;
import com.latnok.audioplayer.util.UtilFunctions;

import static com.latnok.audioplayer.service.SongService.mp;

public class AudioPlayerActivity extends Activity {

    Button btnBack;
    static Button btnPause;
    Button btnNext;
    static Button btnPlay;
    static TextView textNowPlaying;
    static TextView textAlbumArtist;
    static TextView textComposer;
    static LinearLayout linearLayoutPlayer;
    SeekBar progressBar;
    static Context context;
    TextView textBufferDuration, textDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getActionBar().show();
        setContentView(R.layout.audio_player);
        context = this;
        init();

        progressBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mp.seekTo(progress);
                            progressBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );
    }

    private void init() {
        getViews();
        setListeners();
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), Mode.SRC_IN);
        PlayerConstants.PROGRESSBAR_HANDLER = new Handler(){
            @Override
            public void handleMessage(Message msg){
                Integer i[] = (Integer[])msg.obj;
                textBufferDuration.setText(UtilFunctions.getDuration(i[0]));
                textDuration.setText(UtilFunctions.getDuration(i[1]));
                progressBar.setProgress(i[0]);
                progressBar.setMax(i[1]);
            }
        };
    }

    private void setListeners() {
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.previousControl(getApplicationContext());
            }
        });

        btnPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.pauseControl(getApplicationContext());
            }
        });

        btnPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Controls.playControl(getApplicationContext());
            }
        });

        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.nextControl(getApplicationContext());
            }
        });
    }

    public static void changeUI(){
        updateUI();
        changeButton();
    }

    private void getViews() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        textNowPlaying = (TextView) findViewById(R.id.textNowPlaying);
        linearLayoutPlayer = (LinearLayout) findViewById(R.id.linearLayoutPlayer);
        textAlbumArtist = (TextView) findViewById(R.id.textAlbumArtist);
        textComposer = (TextView) findViewById(R.id.textComposer);
        progressBar = (SeekBar) findViewById(R.id.progressBar);
        textBufferDuration = (TextView) findViewById(R.id.textBufferDuration);
        textDuration = (TextView) findViewById(R.id.textDuration);
        textNowPlaying.setSelected(true);
        textAlbumArtist.setSelected(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), getApplicationContext());
        if (isServiceRunning) {
            updateUI();
        }
        changeButton();
    }

    public static void changeButton() {
        if(PlayerConstants.SONG_PAUSED){
            btnPause.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
        }else{
            btnPause.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
        }
    }

    private static void updateUI() {
        try{
            String songName = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getTitle();
            String artist = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getArtist();
            String album = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbum();
            String composer = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getComposer();
            textNowPlaying.setText(songName);
            textAlbumArtist.setText(artist + " - " + album);
            if(composer != null && composer.length() > 0){
                textComposer.setVisibility(View.VISIBLE);
                textComposer.setText(composer);
            }else{
                textComposer.setVisibility(View.GONE);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            long albumId = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getAlbumId();
            Bitmap albumArt = UtilFunctions.getAlbumart(context, albumId);
            if(albumArt != null){
                linearLayoutPlayer.setBackgroundDrawable(new BitmapDrawable(albumArt));
            }else{
                linearLayoutPlayer.setBackgroundDrawable(new BitmapDrawable(UtilFunctions.getDefaultAlbumArt(context)));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
