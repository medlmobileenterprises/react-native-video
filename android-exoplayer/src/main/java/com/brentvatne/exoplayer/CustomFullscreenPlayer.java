package com.brentvatne.exoplayer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.brentvatne.react.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class CustomFullscreenPlayer extends Activity {

    private SimpleExoPlayer player;
    private SimpleExoPlayerView simpleExoPlayerView;
    private long timestamp;
    private Uri videoUrl;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_fullscreen_player);

        Intent intent = getIntent();
        videoUrl = Uri.parse(intent.getStringExtra("URI"));
        timestamp = intent.getLongExtra("TIMESTAMP", 0);

        this.simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.simpleExoPlayerView);
        this.createExoPlayer();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("TIMESTAMP", player.getCurrentPosition());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void createExoPlayer() {
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        this.simpleExoPlayerView.setPlayer(player);
        this.prepareMediaSource();
    }

    private void prepareMediaSource() {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "android-exoplayer"), bandwidthMeter);

        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(this.videoUrl);
        player.seekTo(player.getCurrentWindowIndex(), timestamp);
        player.prepare(videoSource, false, true);
        player.setPlayWhenReady(true);
    }
}