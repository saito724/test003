package org.example.droidgame;

import javax.microedition.khronos.opengles.GL10;

public class PlayerShot extends ActorBase
{
    // テクスチャ画像の幅・高さ
    private static int TEXTURE_WIDTH = 64;
    private static int TEXTURE_HEIGHT = 64;
    // ショットの半径
    private static final float RADIUS = 20.0f;
    // ショットのY速度
    private static final float SHOT_SPEED = 16.0f;

    /**
     * コンストラクタ
     */
    public PlayerShot()
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
        isAlive = true;
    }

    /**
     * 毎フレームの更新処理
     */
    public void update()
    {
        // 座標を更新
        y += SHOT_SPEED;
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
