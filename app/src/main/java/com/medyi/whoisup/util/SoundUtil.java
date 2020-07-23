package com.medyi.whoisup.util;

import android.content.Context;
import android.media.MediaPlayer;

public final class SoundUtil {

    private SoundUtil() {
    }

    public static void playSound(Context context, int res) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, res);
        mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.release());
        mediaPlayer.start();
    }
}
