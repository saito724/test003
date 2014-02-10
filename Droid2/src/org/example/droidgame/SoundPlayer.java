package org.example.droidgame;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.example.shootinggame.R;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

/**
 * �����Đ������l�����Ȃ���T�E���h�Đ����s���N���X
 */
public class SoundPlayer implements OnCompletionListener
{
    // ���\�[�XID��MediaPlayer��HashMap
    HashMap<Integer, MediaPlayer> mpHashMap = null;

    // �`�����l�����̃��\�[�XID
    private int resID[] = null;
    // �`�����l�����Đ������ǂ���
    private boolean isPlaying[] = null;
    // ���݃`�����l���ɃZ�b�g����Ă���MediaPlayer�ւ̎Q��
    private MediaPlayer mediaPlayer[] = null;

    /**
     * ����������
     *
     * @param context
     * @param channel �`�����l����
     */
    public void initializeSoundPlayer(Context context, int channel)
    {
        mpHashMap = new HashMap<Integer, MediaPlayer>();

        // �`�����l�����ɉ����ď�����
        resID = new int[channel];
        isPlaying = new boolean[channel];
        mediaPlayer = new MediaPlayer[channel];
        for (int i = 0; i < channel; i++)
        {
            resID[i] = -1;
            isPlaying[i] = false;
            mediaPlayer[i] = null;
        }

        // ���\�[�X�̐�����MediaPlayer�𐶐����AHashMap�ɒǉ�
        createMediaPlayer(context, R.raw.bgm_title);
        createMediaPlayer(context, R.raw.bgm_stage);
        createMediaPlayer(context, R.raw.bgm_gameover);
        createMediaPlayer(context, R.raw.se_apeear_enemy);
        createMediaPlayer(context, R.raw.se_death_enemy);
        createMediaPlayer(context, R.raw.se_player_shot);
    }

    /**
     * �I������
     */
    public void finalizeSoundPlayer()
    {
        // MediaPlayer�����ׂĊJ��
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
     * MediaPlayer�𐶐����AHashMap�ɒǉ�����
     *
     * @param context
     * @param id
     */
    private void createMediaPlayer(Context context, int id)
    {
        // MediaPlayer�𐶐�
        MediaPlayer mp = MediaPlayer.create(context, id);

        // OnCompletionListener�̎w��
        mp.setOnCompletionListener(this);

        // HashMap�ɃZ�b�g
        mpHashMap.put(Integer.valueOf(id), mp);
    }

    /**
     * HashMap����MediaPlayer���擾���A�Z�b�g���� playBGM, playSE����Ă΂�Ă���
     *
     * @param channel_idx
     * @param id
     * @return id�ɑΉ�����MediaPlayer���������true
     */
    private boolean setMediaPlayer(int channel_idx, int id)
    {
        // HashMap����Y���̃T�E���h�����o��
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
     * �w��`�����l���̃T�E���h��擪����Đ����� playBGM, playSE����Ă΂�Ă���
     *
     * @param channel_idx
     * @param loop_enable ���[�v�Đ����s�����ǂ���
     */
    private void playMediaPlayer(int channel_idx, boolean loop_enable)
    {
        // �Đ����s��
        mediaPlayer[channel_idx].setLooping(loop_enable);
        mediaPlayer[channel_idx].seekTo(0);
        mediaPlayer[channel_idx].start();
        isPlaying[channel_idx] = true;
    }

    /**
     * BGM���Đ�����
     *
     * @param id
     * @param loop_enable ���[�v�Đ����s�����ǂ���
     * @return �Đ��ł�����true��Ԃ�
     */
    public boolean playBGM(int id, boolean loop_enable)
    {
        // �`�����l��0�Ԃ�BGM�Ƃ���
        // �Đ��������擾���A�Đ����ł���Ή������Ȃ�
        if (isPlaying[0])
        {
            return false;
        }
        else
        {
            // ���݃Z�b�g���Ă�����̂Ɠ���łȂ����HashMap����擾
            if (resID[0] != id)
            {
                if (!setMediaPlayer(0, id))
                    return false;
            }

            // �Đ����s��
            playMediaPlayer(0, loop_enable);

            return true;
        }
    }

    /**
     * SE���Đ�����
     *
     * @param id
     * @return �Đ��ł�����true��Ԃ�
     */
    public boolean playSE(int id)
    {
        // �T�E���h�`�����l��1�Ԉȍ~��SE�Ƃ���
        for (int i = 1; i < mediaPlayer.length; i++)
        {
            // �܂��A���݃`�����l���ɃZ�b�g���̃T�E���h���ǂ���
            if (resID[i] == id)
            {
                // �v���C���Ȃ��~����
                stopChannel(i);

                // �Đ�����
                playMediaPlayer(i, false);

                return true;
            }
        }

        // �Z�b�g���̃T�E���h�łȂ���΍ēx���[�v
        for (int i = 1; i < mediaPlayer.length; i++)
        {
            // �Đ����łȂ��`�����l����T��
            if (!isPlaying[i])
            {
                // HashMap����Y���̃T�E���h�����o��
                if (setMediaPlayer(i, id))
                {
                    // �Đ�����
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
     * �����Ɏw�肵���`�����l���̍Đ���~�������s�� stopBGM, stopSE, stopAllSound���ŌĂ΂��
     *
     * @param idx
     * @return ��~����������ɏI�������true��Ԃ�
     */
    private boolean stopChannel(int idx)
    {
        if (idx < 0 || idx >= mediaPlayer.length)
            return false;
        if (mediaPlayer[idx] == null)
            return false;
        if (!isPlaying[idx])
            return false;

        // stop����MediaPlayer�́A��xprepare���\�b�h���Ăяo���Ȃ���
        // �Ă�start���\�b�h���Ăяo�����Ƃ��ł��Ȃ�
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

        // stop����prepare�͕��ׂ�������̂ŁA
        // pause��Ԃɂ��Đ擪�ɃV�[�N���Ă�������l������
        /*
         * mediaPlayer[idx].pause(); mediaPlayer[idx].seekTo(0);
         */
        isPlaying[idx] = false;

        return true;
    }

    /**
     * �Đ�����BGM���~����
     *
     * @return ��~�ɐ��������true��Ԃ�
     */
    public boolean stopBGM()
    {
        return stopChannel(0);
    }

    /**
     * �Đ�����SE���~����
     *
     * @param id
     * @return ��~�ɐ��������true��Ԃ�
     */
    public boolean stopSE(int id)
    {
        // �T�E���h�`�����l��1�Ԉȍ~��SE�Ƃ���
        for (int i = 1; i < mediaPlayer.length; i++)
        {
            if (resID[i] == id)
                return stopChannel(i);
        }

        return false;
    }

    /**
     * �S�ẴT�E���h���~����
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
     * @Override �T�E���h�Đ��I�����ɌĂяo�����
     */
    public void onCompletion(MediaPlayer mp)
    {
        for (int i = 0; i < mediaPlayer.length; i++)
        {
            if (mp == mediaPlayer[i])
            {
                // �T�E���h�Đ��I������MediaPlayer�́uPlayback Completed�v�Ƃ�����ԂȂ̂ŁA
                // start���\�b�h�ōēx�Đ����s�����Ƃ��ł���
                // �O�̂��ߐ擪�ɃV�[�N���Ă��邪�A���̕K�v���Ȃ�����
                mediaPlayer[i].seekTo(0);
                isPlaying[i] = false;
            }
        }
    }
}
