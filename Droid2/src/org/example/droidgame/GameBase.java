package org.example.droidgame;

import android.app.Activity;
import android.os.Bundle;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.view.Window;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Activity, Renderer���p�������Q�[�����N���X
 */
public abstract class GameBase extends Activity implements
        GLSurfaceView.Renderer
{
    private GLSurfaceView gLSurfaceView;

    // �T�[�t�F�C�X�̕��E����
    protected int surfaceWidth;
    protected int surfaceHeight;

    // FPS�v��
    protected FPSManager fpsManager;
    // ���t���[�����~���b��
    private long frameTime;
    // Sleep����
    private long sleepTime;
    // �t���[���X�L�b�v���s�����ǂ���
    private boolean frameSkipEnable;
    // �t���[���X�L�b�v���������ǂ���
    private boolean frameSkipState;

    /**
     * �R���X�g���N�^
     *
     * @param fps FPS�l
     * @param frameskip_enable �t���[���X�L�b�v���L�����ǂ���
     */
    public GameBase(float fps, boolean frameskip_enable)
    {
        // FPS����̏�����
        frameSkipEnable = frameskip_enable;
        frameSkipState = false;
        fpsManager = new FPSManager(10);
        sleepTime = 0l;
        frameTime = (long) (1000.0f / fps);

    }

    /**
     * @Override �A�N�e�B�r�e�B�������ɌĂяo�����
     */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // �^�C�g���o�[������
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // GLSurfaceView �𐶐�
        gLSurfaceView = new GLSurfaceView(this);
        // �����_���[�𐶐����ăZ�b�g
        gLSurfaceView.setRenderer(this);

        // ���C�A�E�g�̃��\�[�X�Q�Ƃ͓n�����A����View�I�u�W�F�N�g��n��
        // setContentView(R.layout.main);
        setContentView(gLSurfaceView);
    }

    /**
     * ���t���[���ĂԍX�V����
     */
    abstract protected void update();

    /**
     * ���t���[���Ăԕ`�揈��
     */
    abstract protected void draw(GL10 gl);

    /**
     * @Override �`��̂��߂ɖ��t���[���Ăяo�����
     */
    public void onDrawFrame(GL10 gl)
    {
        fpsManager.calcFPS();

        // �t���[���X�L�b�v�L�����̂ݏ���
        if (frameSkipState)
        {
            // �O��Ăяo��������̌o�ߎ��Ԃ��擾
            long elapsedTime = fpsManager.getElapsedTime();
            // ���O��Sleep���Ԃ�����
            elapsedTime -= sleepTime;

            // �ݒ肳��Ă���P�ʎ��Ԃ�菬������΁A��������Sleep���A�o�ߎ��Ԃ�0��
            if (elapsedTime < frameTime && elapsedTime > 0l)
            {
                sleepTime = frameTime - elapsedTime;
                try
                {
                    Thread.sleep(sleepTime);
                }
                catch (InterruptedException e)
                {
                }
                elapsedTime = 0l;
            }
            else
            {
                // �X���[�v���Ԃ�0��
                sleepTime = 0;
                // �P�ʎ��Ԃ�����
                elapsedTime -= frameTime;
            }
            // ����ł��܂��A�P�ʎ��Ԃ��o�ߎ��Ԃ��傫�����
            if (elapsedTime >= frameTime)
            {
                // �t���[���X�L�b�v(�X�V������S��1�x�Ɏ��s���Ă��܂�)
                if (frameSkipEnable)
                {
                    for (; elapsedTime >= frameTime; elapsedTime -= frameTime)
                        update();
                }
            }
        }
        else
        {
            // ����̃t���[������L����
            frameSkipState = true;
        }
        update();
        draw(gl);
    }

    /**
     * @Override �T�[�t�F�C�X�̃T�C�Y�ύX���ɌĂяo�����
     * @param gl
     * @param width �ύX��̕�
     * @param height �ύX��̍���
     */
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        // �傫�Ȓx�����N����̂ŁA����t���[���������̃t���[���X�L�b�v�𖳌���
        frameSkipState = false;

        // �T�[�t�F�C�X�̕��E�������X�V
        surfaceWidth = width;
        surfaceHeight = height;

        // �r���[�|�[�g���T�C�Y�ɍ��킹�ăZ�b�g���Ȃ���
        gl.glViewport(0, 0, width, height);

        // �ˉe�s���I��
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // ���ݑI������Ă���s��(�ˉe�s��)�ɁA�P�ʍs����Z�b�g
        gl.glLoadIdentity();
        // ���s���e�p�̃p�����[�^���Z�b�g
        GLU.gluOrtho2D(gl, 0.0f, width, 0.0f, height);
    }

    /**
     * @Override �T�[�t�F�C�X�����������ہE�܂��͍Đ��������ۂɌĂяo�����
     */
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        // �傫�Ȓx�����N����̂ŁA����t���[���������̃t���[���X�L�b�v�𖳌���
        frameSkipState = false;

        // �A���t�@�u�����h�L��
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);

        // �f�B�U�𖳌���
        gl.glDisable(GL10.GL_DITHER);
        // �J���[�ƃe�N�X�`�����W�̕�Ԑ��x���A�ł������I�Ȃ��̂Ɏw��
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        // �o�b�t�@���������̃J���[�����Z�b�g
        gl.glClearColor(0, 0, 0, 1);

        // �Жʕ\����L����
        gl.glEnable(GL10.GL_CULL_FACE);
        // �J�����O�ݒ��CCW��
        gl.glFrontFace(GL10.GL_CCW);

        // �[�x�e�X�g�𖳌���
        gl.glDisable(GL10.GL_DEPTH_TEST);

        // �t���b�g�V�F�[�f�B���O�ɃZ�b�g
        gl.glShadeModel(GL10.GL_FLAT);
    }

    /**
     * @Override �|�[�Y��Ԃ���̕�������A�A�N�e�B�r�e�B�������ȂǂɌĂяo�����
     */
    protected void onResume()
    {
        // �傫�Ȓx�����N����̂ŁA����t���[���������̃t���[���X�L�b�v�𖳌���
        frameSkipState = false;

        super.onResume();
        gLSurfaceView.onResume();
    }

    /**
     * @Override �A�N�e�B�r�e�B�ꎞ��~����A�I�����ɌĂяo�����
     */
    protected void onPause()
    {
        // �傫�Ȓx�����N����̂ŁA����t���[���������̃t���[���X�L�b�v�𖳌���
        frameSkipState = false;

        super.onPause();
        gLSurfaceView.onPause();
    }
}
