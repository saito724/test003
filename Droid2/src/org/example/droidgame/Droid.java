package org.example.droidgame;

import javax.microedition.khronos.opengles.GL10;

import org.example.shootinggame.R;

import android.R.integer;

/**
 * 敵のクラス
 */
public class Droid extends ActorBase
{
    // テクスチャ画像の幅・高さ
    private static int TEXTURE_WIDTH = 128;
    private static int TEXTURE_HEIGHT = 128;
    private float size;
    
    
//    // Enemyの半径
//    private static final float RADIUS = 35.0f;

    // Enemyの速度
    private static final float MIN_SPEED = 2.0f;
    private static final float RAND_SPEED = 8.0f;
    public float speedX, speedY;
    // Enemyのライフ
    private static final int MAX_LIFE = 12;
    public int life;
    
    //ポイント（得点）
    private int point;
    private DroidType type;
    

    /* ショット */
    // ショット発射のためのカウンタ
    private int shotCounter;
    // 何フレーム間隔でショットを出すか
    private int SHOT_EXEC_TIME = 10;

    /* 死亡演出 */
    // 死亡処理中かどうか
    public boolean isDeathState;
    // 死亡演出用の拡大縮小処理と座標ずらし
    private float scaleX, scaleY, addY;
    private static final float ADD_SCALE_X = -0.15f;
    private static final float ADD_SCALE_Y = 0.1f;
    private static final float ADD_Y = 10.0f;

    // ShootingGameへの参照
    private DroidGame	droidGame;
    // サウンドプレーヤへの参照
    private SoundPlayer soundPlayer;

    /**
     * コンストラクタ
     *
     * @param sg ShootingGameへの参照
     * @param sp SoundPlayerへの参照
     */
    public Droid(DroidGame dg, SoundPlayer sp)
    {
        super(0.0f, 0.0f,0.0f,false);

        droidGame = dg;
        soundPlayer = sp;
        initialize();
    }

    public void setType(DroidType type){
    	
    	this.size = type.getSize();
    	this.point = type.getPoint();
    	
    }
    
    /**
     * 初期化
     */
    private void initialize()
    {
        speedX = 0.0f;
        speedY = 0.0f;
        life = MAX_LIFE;
        shotCounter = 0;
        isDeathState = false;
        scaleX = 1.0f;
        scaleY = 1.0f;
        addY = 0.0f;
    }

    /**
     * 指定座標から敵を生成
     *
     * @param x 敵が生成されるX座標
     * @param y 敵が生成されるY座標
     */
    public void execute(DroidType type,int textureId)
    {
        initialize();
        this.x = type.getPoint();
        this.y = 0;

        // 移動速度をランダムで
        speedX = (((float) Math.random()) - 0.5f) * RAND_SPEED;
        speedX = speedX >= 0 ? speedX + MIN_SPEED : speedX - MIN_SPEED;
        speedY = (float) Math.random() * RAND_SPEED / 4 + MIN_SPEED;
        isAlive = true;
    }

    /**
     * ライフを１つ削る
     *
     * @return 削った結果、このEnemyが死亡状態に突入したらtrueを返す
     */
    public boolean decLife()
    {
        if (!isDeathState)
        {
            life--;
            if (life <= 0)
            {
                isDeathState = true;

                // 効果音再生
                soundPlayer.playSE(R.raw.se_death_enemy);
            }
        }
        return isDeathState;
    }

    /**
     * 毎フレームの更新処理
     */
    public void update()
    {
        // 通常処理
        if (!isDeathState)
        {
            // 座標を更新
            x += speedX;
            y += speedY;

//            // ショット発射処理
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

            // X拡大率が0を下回ったら死亡処理終了
            if (scaleX < 0.0f)
            {
                scaleX = 0.0f;
                isAlive = false;
            }
        }
    }

    /**
     * @Override 毎フレームの描画処理
     */
    public void draw(GL10 gl, int tex_id)
    {
        TextureDrawer.drawTexture(gl, tex_id, (int) x, (int) (y + addY),
                TEXTURE_WIDTH, TEXTURE_HEIGHT, 0.0f, scaleX, scaleY);
    }

}
