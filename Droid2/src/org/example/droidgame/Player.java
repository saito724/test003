package org.example.droidgame;

import javax.microedition.khronos.opengles.GL10;

import org.example.shootinggame.R;

/**
 * ���@�̃N���X
 */
public class Player extends ActorBase
{
    // �e�N�X�`���摜�̕��E����
    private static int TEXTURE_WIDTH = 128;
    private static int TEXTURE_HEIGHT = 128;
    // ���@�̔��a
    public static final float RADIUS = 35.0f;
    // ���@��X���x
    private float speed;

    /* �V���b�g�֘A */
    // �V���b�g���˒����ǂ���
    private boolean shotState;
    // ���t���[���Ɉ�񔭎˂��邩
    private static final int PLAYER_SHOT_INTERVAL = 3;
    // �V���b�g�p�̃J�E���^
    private int playerShotCounter;

    // ShootingGame�ւ̎Q��
    private ShootingGame shootingGame;
    // �T�E���h�v���[���ւ̎Q��
    private SoundPlayer soundPlayer;

    /**
     * �R���X�g���N�^
     *
     * @param sg ShootingGame�ւ̎Q��
     * @param sp SoundPlayer�ւ̎Q��
     * @param x X���W�l
     * @param y Y���W�l
     */
    public Player(ShootingGame sg, SoundPlayer sp, float x, float y)
    {
        super(x, y, RADIUS, true);
        shootingGame = sg;
        soundPlayer = sp;
    }

    /**
     * ����������
     */
    public void initialize()
    {
        speed = 0.0f;
        shotState = false;
        playerShotCounter = 0;
    }

    /**
     * �V���b�g���˒����ǂ�������������
     *
     * @param flag
     */
    public void setShotState(boolean flag)
    {
        shotState = flag;
    }

    /**
     * �X�s�[�h����������
     *
     * @param speed
     */
    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

    /**
     * ���t���[���̍X�V����
     */
    public void update()
    {
        // ���W���X�V
        x += speed;

        // �V���b�g���˒��Ȃ�V���b�g���o��
        if (shotState)
        {
            if (playerShotCounter == 0)
            {
                shootingGame.createShot(x, y + TEXTURE_HEIGHT / 2);

                // ���ʉ��Đ�
                soundPlayer.playSE(R.raw.se_player_shot);
            }

            playerShotCounter++;

            if (playerShotCounter > PLAYER_SHOT_INTERVAL)
                playerShotCounter = 0;
        }
    }

    /**
     * @Override ���t���[���̕`�揈��
     */
    public void draw(GL10 gl, int tex_id)
    {
        TextureDrawer.drawTexture(gl, tex_id, (int) x, (int) y, TEXTURE_WIDTH,
                TEXTURE_HEIGHT, 0.0f, 1.0f, 1.0f);
    }
}
