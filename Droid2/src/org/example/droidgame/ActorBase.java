package org.example.droidgame;

import javax.microedition.khronos.opengles.GL10;

/**
 * アクターの基底クラス
 */
public abstract class ActorBase
{
    // X, Y座標と、当たり判定用の円の半径
    public float x, y, r;
    // アクターが有効かどうかのフラグ。インスタンスを使いまわすために用いる
    public boolean isAlive;

    /**
     * コンストラクタ
     *
     * @param x X座標
     * @param y Y座標
     * @param w 当たり判定用の円の半径
     * @param alive アクターが有効かどうかのフラグ
     */
    public ActorBase(float x, float y, float r, boolean alive)
    {
        this.x = x;
        this.y = y;
        this.r = r;
        isAlive = alive;
    }

    /**
     * 他のアクターとの円同士の当たり判定を行う
     *
     * @param other 相手のアクター
     * @return 接していればtrueが変える
     */
    public boolean checkCollision(ActorBase other)
    {
        float x = this.x - other.x;
        float y = this.y - other.y;
        float r = this.r + other.r;

        return x * x + y * y <= r * r;
    }

    /**
     * 毎フレームの描画処理
     *
     * @param gl
     * @param tex_id テクスチャID
     */
    public abstract void draw(GL10 gl, int tex_id);
}
