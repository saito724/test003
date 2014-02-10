package org.example.droidgame;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.example.shootinggame.R;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

/**
 * 同時再生数を考慮しながらサウンド再生を行うクラス
 */
public class SoundPlayer implements OnCompletionListener
{
    // リソースIDとMediaPlayerのHashMap
    HashMap<Integer, MediaPlayer> mpHashMap = null;

    // チャンネル毎のリソースID
    private int resID[] = null;
    // チャンネルが再生中かどうか
    private boolean isPlaying[] = null;
    // 現在チャンネルにセットされているMediaPlayerへの参照
    private MediaPlayer mediaPlayer[] = null;

    /**
     * 初期化処理
     *
     * @param context
     * @param channel チャンネル数
     */
    public void initializeSoundPlayer(Context context, int channel)
    {
        mpHashMap = new HashMap<Integer, MediaPlayer>();

        // チャンネル数に応じて初期化
        resID = new int[channel];
        isPlaying = new boolean[channel];
        mediaPlayer = new MediaPlayer[channel];
        for (int i = 0; i < channel; i++)
        {
            resID[i] = -1;
            isPlaying[i] = false;
            mediaPlayer[i] = null;
        }

        // リソースの数だけMediaPlayerを生成し、HashMapに追加
        createMediaPlayer(context, R.raw.bgm_title);
        createMediaPlayer(context, R.raw.bgm_stage);
        createMediaPlayer(context, R.raw.bgm_gameover);
        createMediaPlayer(context, R.raw.se_apeear_enemy);
        createMediaPlayer(context, R.raw.se_death_enemy);
        createMediaPlayer(context, R.raw.se_player_shot);
    }

    /**
     * 終了処理
     */
    public void finalizeSoundPlayer()
    {
        // MediaPlayerをすべて開放
        for (Iterator<MediaPlayer> it = mpHashMap.values().iterator(); it
                .hasNext();)
        {
            MediaPlayer mp = it.next();
            mp.stop();
            mp.setOnCompletionListener(null);
            mp.release();
        }

        resID = null;
        isPlaying = null;
        mediaPlayer = null;
        mpHashMap.clear();
    }

    /**
     * MediaPlayerを生成し、HashMapに追加する
     *
     * @param context
     * @param id
     */
    private void createMediaPlayer(Context context, int id)
    {
        // MediaPlayerを生成
        MediaPlayer mp = MediaPlayer.create(context, id);

        // OnCompletionListenerの指定
        mp.setOnCompletionListener(this);

        // HashMapにセット
        mpHashMap.put(Integer.valueOf(id), mp);
    }

    /**
     * HashMapからMediaPlayerを取得し、セットする playBGM, playSEから呼ばれている
     *
     * @param channel_idx
     * @param id
     * @return idに対応したMediaPlayerが見つかればtrue
     */
    private boolean setMediaPlayer(int channel_idx, int id)
    {
        // HashMapから該当のサウンドを取り出す
        MediaPlayer mp = mpHashMap.get(Integer.valueOf(id));
        if (mp != null)
        {
            mediaPlayer[channel_idx] = mp;
            resID[channel_idx] = id;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 指定チャンネルのサウンドを先頭から再生する playBGM, playSEから呼ばれている
     *
     * @param channel_idx
     * @param loop_enable ループ再生を行うかどうか
     */
    private void playMediaPlayer(int channel_idx, boolean loop_enable)
    {
        // 再生を行う
        mediaPlayer[channel_idx].setLooping(loop_enable);
        mediaPlayer[channel_idx].seekTo(0);
        mediaPlayer[channel_idx].start();
        isPlaying[channel_idx] = true;
    }

    /**
     * BGMを再生する
     *
     * @param id
     * @param loop_enable ループ再生を行うかどうか
     * @return 再生できたらtrueを返す
     */
    public boolean playBGM(int id, boolean loop_enable)
    {
        // チャンネル0番をBGMとする
        // 再生中かを取得し、再生中であれば何もしない
        if (isPlaying[0])
        {
            return false;
        }
        else
        {
            // 現在セットしているものと同一でなければHashMapから取得
            if (resID[0] != id)
            {
                if (!setMediaPlayer(0, id))
                    return false;
            }

            // 再生を行う
            playMediaPlayer(0, loop_enable);

            return true;
        }
    }

    /**
     * SEを再生する
     *
     * @param id
     * @return 再生できたらtrueを返す
     */
    public boolean playSE(int id)
    {
        // サウンドチャンネル1番以降をSEとする
        for (int i = 1; i < mediaPlayer.length; i++)
        {
            // まず、現在チャンネルにセット中のサウンドかどうか
            if (resID[i] == id)
            {
                // プレイ中なら停止して
                stopChannel(i);

                // 再生する
                playMediaPlayer(i, false);

                return true;
            }
        }

        // セット中のサウンドでなければ再度ループ
        for (int i = 1; i < mediaPlayer.length; i++)
        {
            // 再生中でないチャンネルを探す
            if (!isPlaying[i])
            {
                // HashMapから該当のサウンドを取り出し
                if (setMediaPlayer(i, id))
                {
                    // 再生する
                    playMediaPlayer(i, false);

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * 引数に指定したチャンネルの再生停止処理を行う stopBGM, stopSE, stopAllSound内で呼ばれる
     *
     * @param idx
     * @return 停止処理が正常に終了すればtrueを返す
     */
    private boolean stopChannel(int idx)
    {
        if (idx < 0 || idx >= mediaPlayer.length)
            return false;
        if (mediaPlayer[idx] == null)
            return false;
        if (!isPlaying[idx])
            return false;

        // stopしたMediaPlayerは、一度prepareメソッドを呼び出さないと
        // 再びstartメソッドを呼び出すことができない
        mediaPlayer[idx].stop();
        try
        {
            mediaPlayer[idx].prepare();
        }
        catch (IllegalStateException e)
        {
            return false;
        }
        catch (IOException e)
        {
            return false;
        }

        // stopしてprepareは負荷がかかるので、
        // pause状態にして先頭にシークしておく手も考えられる
        /*
         * mediaPlayer[idx].pause(); mediaPlayer[idx].seekTo(0);
         */
        isPlaying[idx] = false;

        return true;
    }

    /**
     * 再生中のBGMを停止する
     *
     * @return 停止に成功すればtrueを返す
     */
    public boolean stopBGM()
    {
        return stopChannel(0);
    }

    /**
     * 再生中のSEを停止する
     *
     * @param id
     * @return 停止に成功すればtrueを返す
     */
    public boolean stopSE(int id)
    {
        // サウンドチャンネル1番以降をSEとする
        for (int i = 1; i < mediaPlayer.length; i++)
        {
            if (resID[i] == id)
                return stopChannel(i);
        }

        return false;
    }

    /**
     * 全てのサウンドを停止する
     */
    public void stopAllSound()
    {
        for (int i = 0; i < mediaPlayer.length; i++)
        {
            if (mediaPlayer[i] != null)
                stopChannel(i);
        }
    }

    /**
     * @Override サウンド再生終了時に呼び出される
     */
    public void onCompletion(MediaPlayer mp)
    {
        for (int i = 0; i < mediaPlayer.length; i++)
        {
            if (mp == mediaPlayer[i])
            {
                // サウンド再生終了時のMediaPlayerは「Playback Completed」という状態なので、
                // startメソッドで再度再生を行うことができる
                // 念のため先頭にシークしているが、その必要もないかも
                mediaPlayer[i].seekTo(0);
                isPlaying[i] = false;
            }
        }
    }
}
