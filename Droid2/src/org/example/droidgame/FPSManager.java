package org.example.droidgame;

import java.util.LinkedList;
import android.os.SystemClock;

/**
 * FPS�Z�o�N���X
 */
public class FPSManager
{
    // �O��calcFPS() ���Ăяo�����ۂ̒l
    private long prevTime;
    // �O��calcFPS() ���Ăяo�����ۂ̍����l
    private long elapsedTime;
    // FPS�l
    private float fps;
    // �o�ߎ��Ԃ̘a
    private long times;
    // ���t���[���̌o�ߎ��Ԃ��A�T���v���������ێ�����Queue
    private LinkedList<Long> elapsedTimeList;
    // �T���v����
    private int sampleNum;

    /**
     * �R���X�g���N�^
     *
     * @param sample_num �T���v���l
     */
    public FPSManager(int sample_num)
    {
        prevTime = 0l;
        elapsedTime = 0l;
        fps = 0.0f;
        times = 0l;
        elapsedTimeList = new LinkedList<Long>();
        for (int i = 0; i < sample_num; i++)
            elapsedTimeList.add(0l);
        sampleNum = sample_num;
    }

    /**
     * �O���calcFPS()�Ăяo��������̍������Ƃ���FPS�l���v��
     */
    public void calcFPS()
    {
        // �u�[�g��̃~���b���擾
        long now_time = SystemClock.uptimeMillis();

        elapsedTime = now_time - prevTime;
        prevTime = now_time;

        // �o�ߎ��Ԃ����Z
        times += elapsedTime;
        // �o�ߎ��ԃ��X�g�ɒǉ���
        elapsedTimeList.add(elapsedTime);
        // ���X�g���̍ł��Â����̂��폜
        times -= elapsedTimeList.poll();

        // ���ώ��Ԃ��v��
        long tmp = times / sampleNum;

        // FPS�l���Z�o
        if (tmp != 0l)
            fps = 1000.0f / tmp;
        else
            fps = 0.0f;
    }

    /**
     * ���݂�FPS�l��Ԃ�
     *
     * @return fps�l��\��float�l
     */
    public float getFPS()
    {
        return fps;
    }

    /**
     * �O�񂩂�̌o�ߎ��Ԃ�Ԃ�
     *
     * @return �o�ߎ���(�~���b)��\��long�l
     */
    public long getElapsedTime()
    {
        return elapsedTime;
    }
}
