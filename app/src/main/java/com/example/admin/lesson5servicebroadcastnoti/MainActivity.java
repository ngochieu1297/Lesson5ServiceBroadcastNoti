package com.example.admin.lesson5servicebroadcastnoti;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        OnItemClickListener, MyService.OnSyncActivityListerner {
    private static final String TAG = "MainActivity" ;
    private RecyclerView mRecyclerMusic;
    private ImageButton mButtonPlay;
    private ImageButton mButtonPause;
    private ImageButton mButtonNext;
    private ImageButton mButtonPrev;
    private SeekBar mSeekBar;
    private TextView mTextTitle;
    private TextView mCurrentSongTime;
    private TextView mDurationSongTime;
    private MyService mService;
    public static List<Music> mMusics = new ArrayList<>();
    public static final int MESSAGE_DELAY = 500;
    public static final int NEXT_SONG = 1;
    public static final int PREVIOUS_SONG = -1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int currentIndex = mService.getCurrentIndex();
            int currentPosition = mService.getCurrentPosition();
            mSeekBar.setProgress(currentPosition);
            mTextTitle.setText(mMusics.get(currentIndex).getName());
            mCurrentSongTime.setText(TimeConvert.convertMilisecondToFormatTime(currentPosition));
            mHandler.sendMessageDelayed(new Message(), MESSAGE_DELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addData();
        initViews();
        Intent intent = new Intent(this, MyService.class);
        if (mService == null) {
            startService(intent);
        }
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private void addData() {
        mMusics.clear();
        mMusics.add(new Music("Chuyện anh vẫn chưa kể", "Chi Dân", R.raw.chuyen_anh_van_chua_ke));
        mMusics.add(new Music("Chuyến đi của năm", "Soobin Hoàng Sơn", R.raw.chuyen_di_cua_nam));
        mMusics.add(new Music("Đừng xin lỗi nữa", "Erik x Min", R.raw.dung_xin_loi_nua));
        mMusics.add(new Music("Kém duyên", "Rum x Nit x Masew", R.raw.kem_duyen));
        mMusics.add(new Music("Người lạ ơi", "Karik x Orange", R.raw.nguoi_la_oi));
    }

    private void initViews() {
        mRecyclerMusic = findViewById(R.id.recycler_music);
        mRecyclerMusic.setHasFixedSize(true);
        MusicAdapter musicAdapter = new MusicAdapter(mMusics, this);
        mRecyclerMusic.setAdapter(musicAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerMusic.setLayoutManager(layoutManager);
        mButtonNext = findViewById(R.id.image_button_next);
        mButtonPrev = findViewById(R.id.image_button_prev);
        mButtonPlay = findViewById(R.id.image_button_play);
        mButtonPause = findViewById(R.id.image_button_pause);
        mButtonPlay.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
        mButtonPrev.setOnClickListener(this);
        mButtonPause.setOnClickListener(this);
        mTextTitle = findViewById(R.id.text_title);
        mCurrentSongTime = findViewById(R.id.text_time_start);
        mDurationSongTime = findViewById(R.id.text_time_end);
        mSeekBar = findViewById(R.id.seek_bar_song);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser) {
                    mService.seek(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.LocalBinder binder = (MyService.LocalBinder) iBinder;
            mService = binder.getService();
            mService.setSyncSeekbarListerner(MainActivity.this);
            syncSeekbar(mService.getDuration());
            syncNotification(mService.isPlaying());
            mHandler.sendMessageDelayed(new Message(), MESSAGE_DELAY);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            unbindService(mConnection);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_button_next:
                mService.changeSong(NEXT_SONG);
                mService.updateNotification();
                mButtonPause.setVisibility(View.VISIBLE);
                mButtonPlay.setVisibility(View.GONE);
                break;
            case R.id.image_button_prev:
                mService.changeSong(PREVIOUS_SONG);
                mService.updateNotification();
                mButtonPause.setVisibility(View.VISIBLE);
                mButtonPlay.setVisibility(View.GONE);
                break;
            case R.id.image_button_play:
                if (mService.getMediaPlayer() == null) {
                    mService.create(0);
                    mService.start();
                } else {
                    mService.start();
                }
                mService.updateNotification();
                mButtonPlay.setVisibility(View.GONE);
                mButtonPause.setVisibility(View.VISIBLE);
                break;
            case R.id.image_button_pause:
                mService.pause();
                mService.updateNotification();
                mButtonPlay.setVisibility(View.VISIBLE);
                mButtonPause.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        mService.create(position);
        mService.start();
        mService.updateNotification();
        mButtonPause.setVisibility(View.VISIBLE);
        mButtonPlay.setVisibility(View.GONE);
    }

    @Override
    public void syncSeekbar(int max) {
        mSeekBar.setMax(max);
        mDurationSongTime.setText(TimeConvert.convertMilisecondToFormatTime(max));
    }

    @Override
    public void syncNotification(boolean isPlaying) {
        if (isPlaying) {
            mButtonPause.setVisibility(View.VISIBLE);
            mButtonPlay.setVisibility(View.GONE);
        } else {
            mButtonPause.setVisibility(View.GONE);
            mButtonPlay.setVisibility(View.VISIBLE);
        }
    }
}
