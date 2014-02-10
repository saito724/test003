package org.example.droidgame;

import java.util.EnumMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.example.shootinggame.R;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;

import com.example.android.apis.graphics.spritetext.LabelMaker;
import com.example.android.apis.graphics.spritetext.NumericSprite;

/**
 * ゲーム固有の処理を記述したクラス
 */
public class DroidGame extends GameBase
{
    // 文字列を描画するためのクラス
    private Paint labelPaint;
    private LabelMaker labels;
    // 文字列ID
    private int labelFps, labelScore, labelStart, labelGameOver;
    // 数値を描画するためのクラス
    private NumericSprite numericSprite;

    // サウンド再生クラス
    private SoundPlayer soundPlayer;
    // 現在再生中のBGMID
    private int nowBGMID;

    // テクスチャID
   // private int playerTexID, enemyTexID, playerBulletTexID, enemyBulletTexID;
    private int  droidTexID, droidBulletTexID;
    private EnumMap<DroidType, Integer> droidEnumMap;

    // タッチ時に自機を触っているかどうか判定するためのサイズ
    private static final int FINGER_RADIUS = 50;
//    // 自機
//    private static final float PLAYER_SPEED = 6.0f;
//    private Player player;

//    // ショット用の配列
//    private static final int PLAYER_SHOT_NUM = 16;
//    private PlayerShot[] playerShot = new PlayerShot[PLAYER_SHOT_NUM];

    // 敵用の配列
    private static final int MUL_DROID_TIME = 10;//適正性時間ランダム分（MIN_DROID_TIMEに上乗せ）
    private static final int MIN_DROID_TIME = 10;//最小敵生成時間
    private static final int DROID_NUM = 20;//配列の数
    private Droid[] droids = new Droid[DROID_NUM];
    // 次に敵を生成するまでのカウンタ
    private int droidCounter;

    // 敵の弾用の配列
//    private static final int ENEMY_SHOT_NUM = 32;
//    private EnemyShot[] enemyShot = new EnemyShot[ENEMY_SHOT_NUM];

    // スコア
    private int score;

    // ゲームの状態
    private static final int GAMESTATE_TITLE = 0;
    private static final int GAMESTATE_PLAYING = 1;
    private static final int GAMESTATE_GAMEOVER = 2;
    private int gameState;

    // FPS
    private static final float FPS_NUM = 30.0f;

    /**
     * コンストラクタ
     */
    public DroidGame()
    {
        super(FPS_NUM, true);
    }

    /**
     * @Override アクティビティ生成時に呼び出される
     */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // サウンド再生クラスにthisを渡しているので、
        // コンストラクタではなくonCreateで初期化を行う
        soundPlayer = new SoundPlayer();
        soundPlayer.initializeSoundPlayer(this, 4);
        nowBGMID = -1;

        // テクスチャID
        //playerTexID = -1;
        droidTexID = -1;
        //playerBulletTexID = -1;
        droidBulletTexID = -1;

        // 文字列をセット
        labels = null;
        numericSprite = null;
        labelPaint = new Paint();
        labelPaint.setTextSize(32);
        labelPaint.setAntiAlias(true);
        labelPaint.setARGB(0xff, 0xff, 0xff, 0xff);

        // 自機を生成
      //  player = new Player(this, soundPlayer, 0.0f, Player.RADIUS);

        // ショット用の配列を生成
//        for (int i = 0; i < PLAYER_SHOT_NUM; i++)
//            playerShot[i] = new PlayerShot();
        // Enemy用の配列を生成
        
        for (int i = 0; i < DROID_NUM; i++){        	
        	 droids[i] = new Droid(this, soundPlayer);
        }
       //     enemy[i] = new Enemy(this, soundPlayer);
        	  droidCounter = 0;

          	  
        	  
        	  
        	  
        // 敵ショット用の配列を生成
//        for (int i = 0; i < ENEMY_SHOT_NUM; i++)
//            enemyShot[i] = new EnemyShot();

        // スコアの初期化
        score = 0;

        // ゲームの状態を、タイトル画面に
        changeGameState(GAMESTATE_TITLE);
    }

    /**
     * ゲーム用のパラメータを初期化する
     */
    private void initGame()
    {
        for (int i = 0; i < DROID_NUM; i++)
            droids[i].isAlive = false;

        droidCounter = 0;
        score = 0;
    }

    /**
     * ゲームステートを変更する
     *
     * @param game_state
     */
    private void changeGameState(int game_state)
    {
        if (game_state == GAMESTATE_PLAYING)
        {
            initGame();
            nowBGMID = R.raw.bgm_stage;
        }
        else if (game_state == GAMESTATE_TITLE)
        {
            nowBGMID = R.raw.bgm_title;
        }
        else if (game_state == GAMESTATE_GAMEOVER)
        {
            nowBGMID = R.raw.bgm_gameover;
        }
        gameState = game_state;
        // サウンドを再生
        soundPlayer.stopBGM();
        soundPlayer.playBGM(nowBGMID, true);
    }

    /**
     * @Override 毎フレーム呼ぶ更新処理
     */
    protected void update()
    {
        if (gameState == GAMESTATE_PLAYING)
        {

            // 全敵を更新
            for (int i = 0; i < DROID_NUM; i++)
            {
                if (droids[i].isAlive)
                {
                	droids[i].update();

                    // 画面端と中央から上で跳ね返る
                    if (droids[i].x < 0 || droids[i].x > surfaceWidth)
                    	droids[i].speedX = -droids[i].speedX;
                    if (droids[i].y < surfaceHeight / 2
                            || droids[i].y > surfaceHeight)
                    	droids[i].speedY = -droids[i].speedY;
                }
            }

            // 敵を生成
            if (droidCounter < 0)
                createEnemy();
            droidCounter--;


            // ショットと敵の当たり判定
//            for (int i = 0; i < PLAYER_SHOT_NUM; i++)
//            {
//                if (playerShot[i].isAlive)
//                {
//                    for (int j = 0; j < DROID_NUM; j++)
//                    {
//                        if (droids[j].isAlive && !droids[j].isDeathState)
//                        {
//                            // 敵とショットが衝突していれば
//                            if (playerShot[i].checkCollision(droids[j]))
//                            {
//                                // ライフを削って、敵が死んだらスコア加算
//                                if (droids[j].decLife())
//                                    score++;
//                                // ショットを消す
//                                playerShot[i].isAlive = false;
//                            }
//                        }
//                    }
//                }
//            }

            // 画面外に出ていたら削除
            for (int i = 0; i < DROID_NUM; i++)
            {
                // 有効なショットについてのみ処理
                if (droids[i].isAlive)
                {
                    if (droids[i].y + droids[i].r > surfaceHeight)
                    	droids[i].isAlive = false;
                }
            }

        }
    }

    /**
     * フリーなインスタンスを見つけて、Enemyを生成する
     */
    private void createEnemy()
    {
        for (int i = 0; i < DROID_NUM; i++)
        {
            // 未使用のEnemyなら
            if (!droids[i].isAlive)
            {
            	DroidType type = DroidType.getRandomType();
                // 有効にする
            	droids[i].execute(type,droidEnumMap.get(type));

                // 効果音再生
                soundPlayer.playSE(R.raw.se_apeear_enemy);
                break;
            }
        }
        // 次にEnemyを生成する時間をセット
        droidCounter = (int) (Math.random() * MUL_DROID_TIME) + MIN_DROID_TIME;
    }





//    /**
//     * タッチ中のプレイヤー座標移動処理 touchDown, touchMoveで呼ばれる
//     *
//     * @param x タッチイベントで渡されたX座標
//     * @param y タッチイベントで渡されたY座標
//     */
//    private void updateTouchPlayer(float x, float y)
//    {
//        // スクリーン座標からOpenGLのワールド座標に変換する
//        float tmp_y = this.surfaceHeight - y;
//
//        if (tmp_y < player.y + FINGER_RADIUS
//                && tmp_y > player.y - FINGER_RADIUS)
//        {
//            // 自機をタッチしていればショットを出す
//            if ((x - player.x) * (x - player.x) + (tmp_y - player.y)
//                    * (tmp_y - player.y) <= FINGER_RADIUS * FINGER_RADIUS)
//            {
//                player.setShotState(true);
//                player.setSpeed(0.0f);
//            }
//            // 自機より左をタッチしていれば左へ
//            else if (x < player.x - FINGER_RADIUS)
//            {
//                player.setSpeed(-PLAYER_SPEED);
//            }
//            // 自機より右をタッチしていれば右へ
//            else if (x > player.x + FINGER_RADIUS)
//            {
//                player.setSpeed(PLAYER_SPEED);
//            }
//        }
//    }

    /**
     * タッチされた瞬間に呼ばれるメソッド
     *
     * @param x タッチイベントで渡されたX座標
     * @param y タッチイベントで渡されたY座標
     */
    private void touchDown(float x, float y)
    {
        if (gameState == GAMESTATE_PLAYING)
        {
           //TODO 敵と衝突判定する
        }
        else if (gameState == GAMESTATE_TITLE)
        {
            changeGameState(GAMESTATE_PLAYING);
        }
        else if (gameState == GAMESTATE_GAMEOVER)
        {
            changeGameState(GAMESTATE_TITLE);
        }
    }

    /**
     * タッチ状態から離された瞬間に呼ばれるメソッド
     *
     * @param x タッチイベントで渡されたX座標
     * @param y タッチイベントで渡されたY座標
     */
    private void touchUp(float x, float y)
    {
    //とりあえず何もしない
//        if (gameState == GAMESTATE_PLAYING)
//        {
//            // 移動をやめる
//            player.setSpeed(0.0f);
//            // ショットを出すのをやめる
//            player.setShotState(false);
//        }
    }

    /**
     * タッチ状態の指がスライドした際
     *
     * @param x タッチイベントで渡されたX座標
     * @param y タッチイベントで渡されたY座標
     */
    private void touchMove(float x, float y)
    {
        //とりあえず何もしない    	
        if (gameState == GAMESTATE_PLAYING)
        {
          //  updateTouchPlayer(x, y);
        }
    }

    /**
     * @Override タッチイベント
     */
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
        // タッチされた瞬間
        case MotionEvent.ACTION_DOWN:
            touchDown(event.getX(), event.getY());
            break;
        // 離された瞬間
        case MotionEvent.ACTION_UP:
            touchUp(event.getX(), event.getY());
            break;
        // タッチ状態の指がスライドした際
        case MotionEvent.ACTION_MOVE:
            touchMove(event.getX(), event.getY());
            break;
        }
        return true;
    }

    /**
     * @Override 毎フレーム呼ぶ描画処理
     */
    protected void draw(GL10 gl)
    {
        // 描画用バッファをクリア
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        if (gameState == GAMESTATE_PLAYING)
        {
            // 自機を描画
//            player.draw(gl, playerTexID);

            // Enemyを描画
            for (int i = 0; i < DROID_NUM; i++)
            {
                if (droids[i].isAlive)
                	droids[i].draw(gl, droidTexID);
            }


            // テキストを描画
            // 文字列描画
            labels.beginDrawing(gl, surfaceWidth, surfaceHeight);
            labels.draw(gl, 0, surfaceHeight - labels.getHeight(labelFps),
                    labelFps);
            labels.draw(gl, 0, surfaceHeight - labels.getHeight(labelFps) * 2,
                    labelScore);
            labels.endDrawing(gl);
            // 数値描画
            numericSprite.setValue((int) fpsManager.getFPS());
            numericSprite.draw(gl, labels.getWidth(labelFps), surfaceHeight
                    - labels.getHeight(labelFps), surfaceWidth, surfaceHeight);
            numericSprite.setValue(score);
            numericSprite.draw(gl, labels.getWidth(labelScore), surfaceHeight
                    - labels.getHeight(labelFps) * 2, surfaceWidth,
                    surfaceHeight);
        }
        else if (gameState == GAMESTATE_TITLE)
        {
            // 中央に文字列を描画
            labels.beginDrawing(gl, surfaceWidth, surfaceHeight);
            labels.draw(gl, surfaceWidth / 2 - labels.getWidth(labelStart) / 2,
                    surfaceHeight / 2 - labels.getHeight(labelStart) / 2,
                    labelStart);
            labels.endDrawing(gl);
        }
        else if (gameState == GAMESTATE_GAMEOVER)
        {
            // 中央に文字列を描画
            float left = surfaceWidth / 2 - labels.getWidth(labelGameOver) / 2;
            labels.beginDrawing(gl, surfaceWidth, surfaceHeight);
            labels.draw(gl, left,
                    surfaceHeight / 2 + labels.getHeight(labelGameOver),
                    labelGameOver);
            labels.draw(gl, left,
                    surfaceHeight / 2 - labels.getHeight(labelGameOver),
                    labelScore);
            labels.endDrawing(gl);
            // 数値描画
            numericSprite.setValue(score);
            numericSprite.draw(gl, left + labels.getWidth(labelScore),
                    surfaceHeight / 2 - labels.getHeight(labelGameOver),
                    surfaceWidth, surfaceHeight);
        }
    }

    /**
     * @Override サーフェイスのサイズ変更時に呼ばれる
     * @param gl
     * @param width 変更後の幅
     * @param height 変更後の高さ
     */
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        super.onSurfaceChanged(gl, width, height);
    }

    /**
     * @Override サーフェイスが生成される際・または再生成される際に呼ばれる
     */
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        super.onSurfaceCreated(gl, config);

        // 文字列を生成
        if (labels != null)
        {
            labels.shutdown(gl);
        }
        else
        {
            labels = new LabelMaker(true, 256, 128);
        }
        labels.initialize(gl);
        labels.beginAdding(gl);
   //     labelFps = labels.add(gl, "FPS:", labelPaint);
        labelScore = labels.add(gl, "Score:", labelPaint);
        labelStart = labels.add(gl, "Touch Start!!", labelPaint);
        labelGameOver = labels.add(gl, "GAME OVER", labelPaint);
        labels.endAdding(gl);

        // 数値文字列を生成
        if (numericSprite != null)
        {
            numericSprite.shutdown(gl);
        }
        else
        {
            numericSprite = new NumericSprite();
        }
        numericSprite.initialize(gl, labelPaint);
  
        
  		// ドロイド君の種類（type）ごとにテクスチャを生成してEnumMapにtypeとtexture_idを登録しておく
  		droidEnumMap = new EnumMap<DroidType, Integer>(DroidType.class);

  		DroidType[] doidTypeValue = DroidType.values();
  		for (DroidType type : doidTypeValue) {
  			Integer TextureID = TextureLoader.loadTexture(gl, this,
  					type.getImageId());// テクスチャ作って、アクセスできるtexture_id を返す
  			droidEnumMap.put(type, TextureID);
  		}      


    }

    /**
     * @Override 一時停止からの再開
     */
    protected void onResume()
    {
        super.onResume();
        // サウンドを再生
        soundPlayer.playBGM(nowBGMID, true);
    }

    /**
     * @Override 停止状態からの再開
     */
    protected void onRestart()
    {
        super.onRestart();
        // サウンドを再生
        soundPlayer.playBGM(nowBGMID, true);
    }

    /**
     * @Override アクティビティ一時停止時に呼び出される
     */
    protected void onPause()
    {
        super.onPause();
        // 一時停止時には音を止める
        soundPlayer.stopAllSound();
    }

    /**
     * @Override アクティビティ停止時に呼び出される
     */
    protected void onStop()
    {
        super.onStop();
        // 停止時には音を止める
        soundPlayer.stopAllSound();
    }

    /**
     * @Override アクティビティ終了時に呼び出される
     */
    protected void onDestroy()
    {
        super.onDestroy();
        // MediaPlayerを開放する
        soundPlayer.finalizeSoundPlayer();
    }
}
