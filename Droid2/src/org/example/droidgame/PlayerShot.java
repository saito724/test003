package org.example.droidgame;

import javax.microedition.khronos.opengles.GL10;

public class PlayerShot extends ActorBase
{
    // �e�N�X�`���摜�̕��E����
    private static int TEXTURE_WIDTH = 64;
    private static int TEXTURE_HEIGHT = 64;
    // �V���b�g�̔��a
    private static final float RADIUS = 20.0f;
    // �V���b�g��Y���x
    private static final float SHOT_SPEED = 16.0f;

    /**
     * �R���X�g���N�^
     */
    public PlayerShot()
    {
        super(0.0f, 0.0f, RADIUS, false);
    }

    /**
     * �w����W����V���b�g���o��
     *
     * @param x �V���b�g�����������X���W
     * @param y �V���b�g�����������Y���W
     */
    public void execute(float x, float y)
    {
        this.x = x;
        this.y = y;
        isAlive = true;
    }

    /**
     * ���t���[���̍X�V����
     */
    public void update()
    {
        // ���W���X�V
        y += SHOT_SPEED;
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
