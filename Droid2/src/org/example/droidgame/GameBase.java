package org.example.droidgame;

import android.app.Activity;
import android.os.Bundle;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.view.Window;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Activity, Rendererを継承したゲーム基底クラス
 */
public abstract class GameBase extends Activity implements
        GLSurfaceView.Renderer
{
    private GLSurfaceView gLSurfaceView;

    // サーフェイスの幅・高さ
    protected int surfaceWidth;
    protected int surfaceHeight;

    // FPS計測
    protected FPSManager fpsManager;
    // 毎フレーム何ミリ秒か
    private long frameTime;
    // Sleep時間
    private long sleepTime;
    // フレームスキップを行うかどうか
    private boolean frameSkipEnable;
    // フレームスキップが無効かどうか
    private boolean frameSkipState;

    /**
     * コンストラクタ
     *
     * @param fps FPS値
     * @param frameskip_enable フレームスキップが有効かどうか
     */
    public GameBase(float fps, boolean frameskip_enable)
    {
        // FPS周りの初期化
        frameSkipEnable = frameskip_enable;
        frameSkipState = false;
        fpsManager = new FPSManager(10);
        sleepTime = 0l;
        frameTime = (long) (1000.0f / fps);

    }

    /**
     * @Override アクティビティ生成時に呼び出される
     */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // タイトルバーを消す
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // GLSurfaceView を生成
        gLSurfaceView = new GLSurfaceView(this);
        // レンダラーを生成してセット
        gLSurfaceView.setRenderer(this);

        // レイアウトのリソース参照は渡さず、直接Viewオブジェクトを渡す
        // setContentView(R.layout.main);
        setContentView(gLSurfaceView);
    }

    /**
     * 毎フレーム呼ぶ更新処理
     */
    abstract protected void update();

    /**
     * 毎フレーム呼ぶ描画処理
     */
    abstract protected void draw(GL10 gl);

    /**
     * @Override 描画のために毎フレーム呼び出される
     */
    public void onDrawFrame(GL10 gl)
    {
        fpsManager.calcFPS();

        // フレームスキップ有効時のみ処理
        if (frameSkipState)
        {
            // 前回呼び出し時からの経過時間を取得
            long elapsedTime = fpsManager.getElapsedTime();
            // 直前のSleep時間を引く
            elapsedTime -= sleepTime;

            // 設定されている単位時間より小さければ、差分だけSleepし、経過時間を0に
            if (elapsedTime < frameTime && elapsedTime > 0l)
            {
                sleepTime = frameTime - elapsedTime;
                try
                {
                    Thread.sleep(sleepTime);
                }
                catch (InterruptedException e)
                {
                }
                elapsedTime = 0l;
            }
            else
            {
                // スリープ時間を0に
                sleepTime = 0;
                // 単位時間を引く
                elapsedTime -= frameTime;
            }
            // それでもまだ、単位時間より経過時間が大きければ
            if (elapsedTime >= frameTime)
            {
                // フレームスキップ(更新処理を全部1度に実行してしまう)
                if (frameSkipEnable)
                {
                    for (; elapsedTime >= frameTime; elapsedTime -= frameTime)
                        update();
                }
            }
        }
        else
        {
            // 次回のフレームから有効に
            frameSkipState = true;
        }
        update();
        draw(gl);
    }

    /**
     * @Override サーフェイスのサイズ変更時に呼び出される
     * @param gl
     * @param width 変更後の幅
     * @param height 変更後の高さ
     */
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        // 大きな遅延が起こるので、次回フレーム処理時のフレームスキップを無効に
        frameSkipState = false;

        // サーフェイスの幅・高さを更新
        surfaceWidth = width;
        surfaceHeight = height;

        // ビューポートをサイズに合わせてセットしなおす
        gl.glViewport(0, 0, width, height);

        // 射影行列を選択
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // 現在選択されている行列(射影行列)に、単位行列をセット
        gl.glLoadIdentity();
        // 平行投影用のパラメータをセット
        GLU.gluOrtho2D(gl, 0.0f, width, 0.0f, height);
    }

    /**
     * @Override サーフェイスが生成される際・または再生成される際に呼び出される
     */
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        // 大きな遅延が起こるので、次回フレーム処理時のフレームスキップを無効に
        frameSkipState = false;

        // アルファブレンド有効
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);

        // ディザを無効化
        gl.glDisable(GL10.GL_DITHER);
        // カラーとテクスチャ座標の補間精度を、最も効率的なものに指定
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        // バッファ初期化時のカラー情報をセット
        gl.glClearColor(0, 0, 0, 1);

        // 片面表示を有効に
        gl.glEnable(GL10.GL_CULL_FACE);
        // カリング設定をCCWに
        gl.glFrontFace(GL10.GL_CCW);

        // 深度テストを無効に
        gl.glDisable(GL10.GL_DEPTH_TEST);

        // フラットシェーディングにセット
        gl.glShadeModel(GL10.GL_FLAT);
    }

    /**
     * @Override ポーズ状態からの復旧時や、アクティビティ生成時などに呼び出される
     */
    protected void onResume()
    {
        // 大きな遅延が起こるので、次回フレーム処理時のフレームスキップを無効に
        frameSkipState = false;

        super.onResume();
        gLSurfaceView.onResume();
    }

    /**
     * @Override アクティビティ一時停止時や、終了時に呼び出される
     */
    protected void onPause()
    {
        // 大きな遅延が起こるので、次回フレーム処理時のフレームスキップを無効に
        frameSkipState = false;

        super.onPause();
        gLSurfaceView.onPause();
    }
}
