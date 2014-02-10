package org.example.droidgame;

import javax.microedition.khronos.opengles.GL10;

import org.example.shootinggame.R;

import android.R.integer;

/**
 * �G�̃N���X
 */
public class Droid extends ActorBase
{
    // �e�N�X�`���摜�̕��E����
    private static int TEXTURE_WIDTH = 128;
    private static int TEXTURE_HEIGHT = 128;
    private float size;
    
    
//    // Enemy�̔��a
//    private static final float RADIUS = 35.0f;

    // Enemy�̑��x
    private static final float MIN_SPEED = 2.0f;
    private static final float RAND_SPEED = 8.0f;
    public float speedX, speedY;

    
    //�|�C���g�i���_�j
    private int point;

    private int textureId;

    /* �V���b�g */
    // �V���b�g���˂̂��߂̃J�E���^
    private int shotCounter;
    // ���t���[���Ԋu�ŃV���b�g���o����
    private int SHOT_EXEC_TIME = 10;

    /* ���S���o */
    // ���S���������ǂ���
    public boolean isDeathState;
    // ���S���o�p�̊g��k�������ƍ��W���炵
    private float scaleX, scaleY, addY;
    private static final float ADD_SCALE_X = -0.15f;
    private static final float ADD_SCALE_Y = 0.1f;
    private static final float ADD_Y = 10.0f;

    // ShootingGame�ւ̎Q��
    private DroidGame	droidGame;
    // �T�E���h�v���[���ւ̎Q��
    private SoundPlayer soundPlayer;

    /**
     * �R���X�g���N�^
     *
     * @param sg ShootingGame�ւ̎Q��
     * @param sp SoundPlayer�ւ̎Q��
     */
    public Droid(DroidGame dg, SoundPlayer sp)
    {
        super(0.0f, 0.0f,0.0f,false);

        droidGame = dg;
        soundPlayer = sp;
        initialize();
    }

    /**
     * ������
     */
    private void initialize()
    {
        speedX = 0.0f;
        speedY = 0.0f;
        shotCounter = 0;
        isDeathState = false;
        scaleX = 1.0f;
        scaleY = 1.0f;
        addY = 0.0f;
    }

    /**
     * �w����W����G�𐶐�
     *
     * @param x �G�����������X���W
     * @param y �G�����������Y���W
     */
    public void execute(DroidType type,int textureId)
    {
        initialize();
        this.x = 0;
        this.y = 0;
        this.size = type.getSize();
        this.textureId = textureId;
        this.point = type.getPoint();

        speedY =  type.getSpeed();
        isAlive = true;
    }

    /**
     * ���C�t���P���
     *
     * @return ��������ʁA����Enemy�����S��Ԃɓ˓�������true��Ԃ�
     */
    public boolean decLife()
    {
        if (!isDeathState)
        {

                isDeathState = true;

                // ���ʉ��Đ�
                soundPlayer.playSE(R.raw.se_death_enemy);

        }
        return isDeathState;
    }

    /**
     * ���t���[���̍X�V����
     */
    public void update()
    {
        // �ʏ폈��
        if (!isDeathState)
        {
            // ���W���X�V

            y += speedY;

//            // �V���b�g���ˏ���
//            shotCounter++;
//            if (shotCounter >= SHOT_EXEC_TIME)
//            {
//                shotCounter = 0;
//                droidGame.createEnemyShot(x, y - 40);
//            }
        }
        else
        {
            scaleX += ADD_SCALE_X;
            scaleY += ADD_SCALE_Y;
            addY += ADD_Y;

            // X�g�嗦��0����������玀�S�����I��
            if (scaleX < 0.0f)
            {
                scaleX = 0.0f;
                isAlive = false;
            }
        }
    }

    /**
     *  ���t���[���̕`�揈��
     */
    public void draw(GL10 gl)
    {
        draw(gl, textureId);
    }

	@Override
	public void draw(GL10 gl, int tex_id) {
        TextureDrawer.drawTexture(gl, textureId,(int) x, (int) (y + addY),
                TEXTURE_WIDTH, TEXTURE_HEIGHT, 0.0f, scaleX, scaleY);
		
	}

	public int getPoint() {
		return point;
	}

	public float getSize() {
		return size;
	}

}
