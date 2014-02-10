package org.example.droidgame;

import javax.microedition.khronos.opengles.GL10;

import org.example.shootinggame.R;

/**
 * 自機のクラス
 */
public class Player extends ActorBase
{
    // テクスチャ画像の幅・高さ
    private static int TEXTURE_WIDTH = 128;
    private static int TEXTURE_HEIGHT = 128;
    // 自機の半径
    public static final float RADIUS = 35.0f;
    // 自機のX速度
    private float speed;

    /* ショット関連 */
    // ショット発射中かどうか
    private boolean shotState;
    // 何フレームに一回発射するか
    private static final int PLAYER_SHOT_INTERVAL = 3;
    // ショット用のカウンタ
    private int playerShotCounter;

    // ShootingGameへの参照
    private ShootingGame shootingGame;
    // サウンドプレーヤへの参照
    private SoundPlayer soundPlayer;

    /**
     * コンストラクタ
     *
     * @param sg ShootingGameへの参照
     * @param sp SoundPlayerへの参照
     * @param x X座標値
     * @param y Y座標値
     */
    public Player(ShootingGame sg, SoundPlayer sp, float x, float y)
    {
        super(x, y, RADIUS, true);
        shootingGame = sg;
        soundPlayer = sp;
    }

    /**
     * 初期化処理
     */
    public void initialize()
    {
        speed = 0.0f;
        shotState = false;
        playerShotCounter = 0;
    }

    /**
     * ショット発射中かどうかを書き換え
     *
     * @param flag
     */
    public void setShotState(boolean flag)
    {
        shotState = flag;
    }

    /**
     * スピードを書き換え
     *
     * @param speed
     */
    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

    /**
     * 毎フレームの更新処理
     */
    public void update()
    {
        // 座標を更新
        x += speed;

        // ショット発射中ならショットを出す
        if (shotState)
        {
            if (playerShotCounter == 0)
            {
                shootingGame.createShot(x, y + TEXTURE_HEIGHT / 2);

                // 効果音再生
                soundPlayer.playSE(R.raw.se_player_shot);
            }

            playerShotCounter++;

            if (playerShotCounter > PLAYER_SHOT_INTERVAL)
                playerShotCounter = 0;
        }
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
