package org.example.droidgame;

import javax.microedition.khronos.opengles.GL10;

/**
 * �A�N�^�[�̊��N���X
 */
public abstract class ActorBase
{
    // X, Y���W�ƁA�����蔻��p�̉~�̔��a
    public float x, y, r;
    // �A�N�^�[���L�����ǂ����̃t���O�B�C���X�^���X���g���܂킷���߂ɗp����
    public boolean isAlive;

    /**
     * �R���X�g���N�^
     *
     * @param x X���W
     * @param y Y���W
     * @param w �����蔻��p�̉~�̔��a
     * @param alive �A�N�^�[���L�����ǂ����̃t���O
     */
    public ActorBase(float x, float y, float r, boolean alive)
    {
        this.x = x;
        this.y = y;
        this.r = r;
        isAlive = alive;
    }

    /**
     * ���̃A�N�^�[�Ƃ̉~���m�̓����蔻����s��
     *
     * @param other ����̃A�N�^�[
     * @return �ڂ��Ă����true���ς���
     */
    public boolean checkCollision(ActorBase other)
    {
        float x = this.x - other.x;
        float y = this.y - other.y;
        float r = this.r + other.r;

        return x * x + y * y <= r * r;
    }

    /**
     * ���t���[���̕`�揈��
     *
     * @param gl
     * @param tex_id �e�N�X�`��ID
     */
    public abstract void draw(GL10 gl, int tex_id);
}
