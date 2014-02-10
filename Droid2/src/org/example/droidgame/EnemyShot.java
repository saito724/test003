package org.example.droidgame;

import javax.microedition.khronos.opengles.GL10;

public class EnemyShot extends ActorBase
{
    // テクスチャ画像の幅・高さ
    private static int TEXTURE_WIDTH = 64;
    private static int TEXTURE_HEIGHT = 64;
    // ショットの半径
    private static final float RADIUS = 8.0f;
    // ショットのY速度
    private static final float MIN_SPEED = 4.0f;
    private static final float RAND_SPEED = 8.0f;
    public float speedX, speedY;

    /**
     * コンストラクタ
     */
    public EnemyShot()
    {
        super(0.0f, 0.0f, RADIUS, false);
    }

    /**
     * 指定座標からショットを出す
     *
     * @param x ショットが生成されるX座標
     * @param y ショットが生成されるY座標
     */
    public void execute(float x, float y)
    {
        this.x = x;
        this.y = y;
        // 移動速度をランダムで
        speedX = (((float) Math.random()) - 0.5f) * RAND_SPEED;
        speedX = speedX >= 0 ? speedX + MIN_SPEED : speedX - MIN_SPEED;
        speedY = (float) Math.random() * RAND_SPEED / 2 + MIN_SPEED;
        isAlive = true;
    }

    /**
     * 毎フレームの更新処理
     */
    public void update()
    {
        // 座標を更新
        x += speedX;
        y -= speedY;
    }

    /**
     * @Override 毎フレームの描画処理
     */
    public void draw(GL10 gl, int tex_id)
    {
        TextureDrawer.drawTexture(gl, tex_id, (int) x, (int) y, TEXTURE_WIDTH,
                TEXTURE_HEIGHT, 0.0f, 1.0f, 1.0f);
    }

}
