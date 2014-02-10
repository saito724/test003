package org.example.droidgame;

import javax.microedition.khronos.opengles.GL10;

public class EnemyShot extends ActorBase
{
    // �e�N�X�`���摜�̕��E����
    private static int TEXTURE_WIDTH = 64;
    private static int TEXTURE_HEIGHT = 64;
    // �V���b�g�̔��a
    private static final float RADIUS = 8.0f;
    // �V���b�g��Y���x
    private static final float MIN_SPEED = 4.0f;
    private static final float RAND_SPEED = 8.0f;
    public float speedX, speedY;

    /**
     * �R���X�g���N�^
     */
    public EnemyShot()
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
        // �ړ����x�������_����
        speedX = (((float) Math.random()) - 0.5f) * RAND_SPEED;
        speedX = speedX >= 0 ? speedX + MIN_SPEED : speedX - MIN_SPEED;
        speedY = (float) Math.random() * RAND_SPEED / 2 + MIN_SPEED;
        isAlive = true;
    }

    /**
     * ���t���[���̍X�V����
     */
    public void update()
    {
        // ���W���X�V
        x += speedX;
        y -= speedY;
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
