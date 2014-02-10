package org.example.droidgame;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.example.shootinggame.R;

import com.example.android.apis.graphics.spritetext.*;

/**
 * �Q�[���ŗL�̏������L�q�����N���X
 */
public class ShootingGame extends GameBase
{
    // �������`�悷�邽�߂̃N���X
    private Paint labelPaint;
    private LabelMaker labels;
    // ������ID
    private int labelFps, labelScore, labelStart, labelGameOver;
    // ���l��`�悷�邽�߂̃N���X
    private NumericSprite numericSprite;

    // �T�E���h�Đ��N���X
    private SoundPlayer soundPlayer;
    // ���ݍĐ�����BGMID
    private int nowBGMID;

    // �e�N�X�`��ID
    private int playerTexID, enemyTexID, playerBulletTexID, enemyBulletTexID;

    // �^�b�`���Ɏ��@��G���Ă��邩�ǂ������肷�邽�߂̃T�C�Y
    private static final int FINGER_RADIUS = 50;
    // ���@
    private static final float PLAYER_SPEED = 6.0f;
    private Player player;

    // �V���b�g�p�̔z��
    private static final int PLAYER_SHOT_NUM = 16;
    private PlayerShot[] playerShot = new PlayerShot[PLAYER_SHOT_NUM];

    // �G�p�̔z��
    private static final int MUL_ENEMY_TIME = 120;
    private static final int MIN_ENEMY_TIME = 120;
    private static final int ENEMY_NUM = 8;
    private Enemy[] enemy = new Enemy[ENEMY_NUM];
    // ���ɓG�𐶐�����܂ł̃J�E���^
    private int enemyCounter;

    // �G�̒e�p�̔z��
    private static final int ENEMY_SHOT_NUM = 32;
    private EnemyShot[] enemyShot = new EnemyShot[ENEMY_SHOT_NUM];

    // �X�R�A
    private int score;

    // �Q�[���̏��
    private static final int GAMESTATE_TITLE = 0;
    private static final int GAMESTATE_PLAYING = 1;
    private static final int GAMESTATE_GAMEOVER = 2;
    private int gameState;

    // FPS
    private static final float FPS_NUM = 30.0f;

    /**
     * �R���X�g���N�^
     */
    public ShootingGame()
    {
        super(FPS_NUM, true);
    }

    /**
     * @Override �A�N�e�B�r�e�B�������ɌĂяo�����
     */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // �T�E���h�Đ��N���X��this��n���Ă���̂ŁA
        // �R���X�g���N�^�ł͂Ȃ�onCreate�ŏ��������s��
        soundPlayer = new SoundPlayer();
        soundPlayer.initializeSoundPlayer(this, 4);
        nowBGMID = -1;

        // �e�N�X�`��ID
        playerTexID = -1;
        enemyTexID = -1;
        playerBulletTexID = -1;
        enemyBulletTexID = -1;

        // ��������Z�b�g
        labels = null;
        numericSprite = null;
        labelPaint = new Paint();
        labelPaint.setTextSize(32);
        labelPaint.setAntiAlias(true);
        labelPaint.setARGB(0xff, 0xff, 0xff, 0xff);

        // ���@�𐶐�
        player = new Player(this, soundPlayer, 0.0f, Player.RADIUS);

        // �V���b�g�p�̔z��𐶐�
        for (int i = 0; i < PLAYER_SHOT_NUM; i++)
            playerShot[i] = new PlayerShot();
        // Enemy�p�̔z��𐶐�
        for (int i = 0; i < ENEMY_NUM; i++)
            enemy[i] = new Enemy(this, soundPlayer);
        enemyCounter = 0;
        // �G�V���b�g�p�̔z��𐶐�
        for (int i = 0; i < ENEMY_SHOT_NUM; i++)
            enemyShot[i] = new EnemyShot();

        // �X�R�A�̏�����
        score = 0;

        // �Q�[���̏�Ԃ��A�^�C�g����ʂ�
        changeGameState(GAMESTATE_TITLE);
    }

    /**
     * �Q�[���p�̃p�����[�^������������
     */
    private void initGame()
    {
        player.x = this.surfaceWidth / 2.0f;
        player.initialize();

        for (int i = 0; i < PLAYER_SHOT_NUM; i++)
            playerShot[i].isAlive = false;
        for (int i = 0; i < ENEMY_NUM; i++)
            enemy[i].isAlive = false;
        for (int i = 0; i < ENEMY_SHOT_NUM; i++)
            enemyShot[i].isAlive = false;

        enemyCounter = 0;
        score = 0;
    }

    /**
     * �Q�[���X�e�[�g��ύX����
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
        // �T�E���h���Đ�
        soundPlayer.stopBGM();
        soundPlayer.playBGM(nowBGMID, true);
    }

    /**
     * @Override ���t���[���ĂԍX�V����
     */
    protected void update()
    {
        if (gameState == GAMESTATE_PLAYING)
        {
            // ���@���X�V
            player.update();
            // ��ʒ[���z���Ȃ��悤��
            if (player.x < player.r)
                this.player.x = player.r;
            if (player.x + player.r > this.surfaceWidth)
                this.player.x = this.surfaceWidth - player.r;

            // �S�G���X�V
            for (int i = 0; i < ENEMY_NUM; i++)
            {
                if (enemy[i].isAlive)
                {
                    enemy[i].update();

                    // ��ʒ[�ƒ��������Œ��˕Ԃ�
                    if (enemy[i].x < 0 || enemy[i].x > surfaceWidth)
                        enemy[i].speedX = -enemy[i].speedX;
                    if (enemy[i].y < surfaceHeight / 2
                            || enemy[i].y > surfaceHeight)
                        enemy[i].speedY = -enemy[i].speedY;
                }
            }

            // �G�𐶐�
            if (enemyCounter < 0)
                createEnemy();
            enemyCounter--;

            // �S�V���b�g���X�V
            for (int i = 0; i < PLAYER_SHOT_NUM; i++)
            {
                if (playerShot[i].isAlive)
                    playerShot[i].update();
            }

            // �S�G�V���b�g���X�V
            for (int i = 0; i < ENEMY_SHOT_NUM; i++)
            {
                if (enemyShot[i].isAlive)
                    enemyShot[i].update();
            }

            // ���@�ƓG�̓����蔻��
            for (int i = 0; i < ENEMY_NUM; i++)
            {
                if (enemy[i].isAlive)
                {
                    // �G�Ǝ��@���Փ˂��Ă����
                    if (player.checkCollision(enemy[i]))
                    {
                        // �Q�[���I�[�o�[
                        changeGameState(GAMESTATE_GAMEOVER);
                        return;
                    }
                }
            }

            // ���@�ƓG�̒e�̓����蔻��
            for (int i = 0; i < ENEMY_SHOT_NUM; i++)
            {
                if (enemyShot[i].isAlive)
                {
                    // �G�Ǝ��@���Փ˂��Ă����
                    if (player.checkCollision(enemyShot[i]))
                    {
                        // �Q�[���I�[�o�[
                        changeGameState(GAMESTATE_GAMEOVER);
                        return;
                    }
                }
            }

            // �V���b�g�ƓG�̓����蔻��
            for (int i = 0; i < PLAYER_SHOT_NUM; i++)
            {
                if (playerShot[i].isAlive)
                {
                    for (int j = 0; j < ENEMY_NUM; j++)
                    {
                        if (enemy[j].isAlive && !enemy[j].isDeathState)
                        {
                            // �G�ƃV���b�g���Փ˂��Ă����
                            if (playerShot[i].checkCollision(enemy[j]))
                            {
                                // ���C�t������āA�G�����񂾂�X�R�A���Z
                                if (enemy[j].decLife())
                                    score++;
                                // �V���b�g������
                                playerShot[i].isAlive = false;
                            }
                        }
                    }
                }
            }

            // ��ʊO�ɏo�Ă�����폜
            for (int i = 0; i < PLAYER_SHOT_NUM; i++)
            {
                // �L���ȃV���b�g�ɂ��Ă̂ݏ���
                if (playerShot[i].isAlive)
                {
                    if (playerShot[i].y + playerShot[i].r > surfaceHeight)
                        playerShot[i].isAlive = false;
                }
            }
            // �G�̒e��
            for (int i = 0; i < ENEMY_SHOT_NUM; i++)
            {
                // �L���ȃV���b�g�ɂ��Ă̂ݏ���
                if (enemyShot[i].isAlive)
                {
                    if (enemyShot[i].y - enemyShot[i].r < 0)
                        enemyShot[i].isAlive = false;
                }
            }
        }
    }

    /**
     * �t���[�ȃC���X�^���X�������āAEnemy�𐶐�����
     */
    private void createEnemy()
    {
        for (int i = 0; i < ENEMY_NUM; i++)
        {
            // ���g�p��Enemy�Ȃ�
            if (!enemy[i].isAlive)
            {
                // �L���ɂ���
                enemy[i].execute((float) Math.random() * surfaceWidth,
                        surfaceHeight - 50.0f);

                // ���ʉ��Đ�
                soundPlayer.playSE(R.raw.se_apeear_enemy);
                break;
            }
        }
        // ����Enemy�𐶐����鎞�Ԃ��Z�b�g
        enemyCounter = (int) (Math.random() * MUL_ENEMY_TIME) + MIN_ENEMY_TIME;
    }

    /**
     * �t���[�ȃC���X�^���X�������āA�V���b�g�𐶐�����
     */
    public void createShot(float x, float y)
    {
        for (int i = 0; i < PLAYER_SHOT_NUM; i++)
        {
            // ���g�p�̃V���b�g�Ȃ�
            if (!playerShot[i].isAlive)
            {
                // �L���ɂ���
                playerShot[i].execute(x, y);
                break;
            }
        }
    }

    /**
     * �t���[�ȃC���X�^���X�������āA�G�̃V���b�g�𐶐�����
     */
    public void createEnemyShot(float x, float y)
    {
        for (int i = 0; i < ENEMY_SHOT_NUM; i++)
        {
            // ���g�p�̃V���b�g�Ȃ�
            if (!enemyShot[i].isAlive)
            {
                // �L���ɂ���
                enemyShot[i].execute(x, y);
                break;
            }
        }
    }

    /**
     * �^�b�`���̃v���C���[���W�ړ����� touchDown, touchMove�ŌĂ΂��
     *
     * @param x �^�b�`�C�x���g�œn���ꂽX���W
     * @param y �^�b�`�C�x���g�œn���ꂽY���W
     */
    private void updateTouchPlayer(float x, float y)
    {
        // �X�N���[�����W����OpenGL�̃��[���h���W�ɕϊ�����
        float tmp_y = this.surfaceHeight - y;

        if (tmp_y < player.y + FINGER_RADIUS
                && tmp_y > player.y - FINGER_RADIUS)
        {
            // ���@���^�b�`���Ă���΃V���b�g���o��
            if ((x - player.x) * (x - player.x) + (tmp_y - player.y)
                    * (tmp_y - player.y) <= FINGER_RADIUS * FINGER_RADIUS)
            {
                player.setShotState(true);
                player.setSpeed(0.0f);
            }
            // ���@��荶���^�b�`���Ă���΍���
            else if (x < player.x - FINGER_RADIUS)
            {
                player.setSpeed(-PLAYER_SPEED);
            }
            // ���@���E���^�b�`���Ă���ΉE��
            else if (x > player.x + FINGER_RADIUS)
            {
                player.setSpeed(PLAYER_SPEED);
            }
        }
    }

    /**
     * �^�b�`���ꂽ�u�ԂɌĂ΂�郁�\�b�h
     *
     * @param x �^�b�`�C�x���g�œn���ꂽX���W
     * @param y �^�b�`�C�x���g�œn���ꂽY���W
     */
    private void touchDown(float x, float y)
    {
        if (gameState == GAMESTATE_PLAYING)
        {
            updateTouchPlayer(x, y);
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
     * �^�b�`��Ԃ��痣���ꂽ�u�ԂɌĂ΂�郁�\�b�h
     *
     * @param x �^�b�`�C�x���g�œn���ꂽX���W
     * @param y �^�b�`�C�x���g�œn���ꂽY���W
     */
    private void touchUp(float x, float y)
    {
        if (gameState == GAMESTATE_PLAYING)
        {
            // �ړ�����߂�
            player.setSpeed(0.0f);
            // �V���b�g���o���̂���߂�
            player.setShotState(false);
        }
    }

    /**
     * �^�b�`��Ԃ̎w���X���C�h������
     *
     * @param x �^�b�`�C�x���g�œn���ꂽX���W
     * @param y �^�b�`�C�x���g�œn���ꂽY���W
     */
    private void touchMove(float x, float y)
    {
        if (gameState == GAMESTATE_PLAYING)
        {
            updateTouchPlayer(x, y);
        }
    }

    /**
     * @Override �^�b�`�C�x���g
     */
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
        // �^�b�`���ꂽ�u��
        case MotionEvent.ACTION_DOWN:
            touchDown(event.getX(), event.getY());
            break;
        // �����ꂽ�u��
        case MotionEvent.ACTION_UP:
            touchUp(event.getX(), event.getY());
            break;
        // �^�b�`��Ԃ̎w���X���C�h������
        case MotionEvent.ACTION_MOVE:
            touchMove(event.getX(), event.getY());
            break;
        }
        return true;
    }

    /**
     * @Override ���t���[���Ăԕ`�揈��
     */
    protected void draw(GL10 gl)
    {
        // �`��p�o�b�t�@���N���A
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        if (gameState == GAMESTATE_PLAYING)
        {
            // ���@��`��
            player.draw(gl, playerTexID);

            // Enemy��`��
            for (int i = 0; i < ENEMY_NUM; i++)
            {
                if (enemy[i].isAlive)
                    enemy[i].draw(gl, enemyTexID);
            }

            // �G�̃V���b�g��`��
            for (int i = 0; i < ENEMY_SHOT_NUM; i++)
            {
                if (enemyShot[i].isAlive)
                    enemyShot[i].draw(gl, enemyBulletTexID);
            }

            // �V���b�g��`��
            for (int i = 0; i < PLAYER_SHOT_NUM; i++)
            {
                if (playerShot[i].isAlive)
                    playerShot[i].draw(gl, playerBulletTexID);
            }

            // �e�L�X�g��`��
            // ������`��
            labels.beginDrawing(gl, surfaceWidth, surfaceHeight);
            labels.draw(gl, 0, surfaceHeight - labels.getHeight(labelFps),
                    labelFps);
            labels.draw(gl, 0, surfaceHeight - labels.getHeight(labelFps) * 2,
                    labelScore);
            labels.endDrawing(gl);
            // ���l�`��
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
            // �����ɕ������`��
            labels.beginDrawing(gl, surfaceWidth, surfaceHeight);
            labels.draw(gl, surfaceWidth / 2 - labels.getWidth(labelStart) / 2,
                    surfaceHeight / 2 - labels.getHeight(labelStart) / 2,
                    labelStart);
            labels.endDrawing(gl);
        }
        else if (gameState == GAMESTATE_GAMEOVER)
        {
            // �����ɕ������`��
            float left = surfaceWidth / 2 - labels.getWidth(labelGameOver) / 2;
            labels.beginDrawing(gl, surfaceWidth, surfaceHeight);
            labels.draw(gl, left,
                    surfaceHeight / 2 + labels.getHeight(labelGameOver),
                    labelGameOver);
            labels.draw(gl, left,
                    surfaceHeight / 2 - labels.getHeight(labelGameOver),
                    labelScore);
            labels.endDrawing(gl);
            // ���l�`��
            numericSprite.setValue(score);
            numericSprite.draw(gl, left + labels.getWidth(labelScore),
                    surfaceHeight / 2 - labels.getHeight(labelGameOver),
                    surfaceWidth, surfaceHeight);
        }
    }

    /**
     * @Override �T�[�t�F�C�X�̃T�C�Y�ύX���ɌĂ΂��
     * @param gl
     * @param width �ύX��̕�
     * @param height �ύX��̍���
     */
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        super.onSurfaceChanged(gl, width, height);
    }

    /**
     * @Override �T�[�t�F�C�X�����������ہE�܂��͍Đ��������ۂɌĂ΂��
     */
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        super.onSurfaceCreated(gl, config);

        // ������𐶐�
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
        labelFps = labels.add(gl, "FPS:", labelPaint);
        labelScore = labels.add(gl, "Score:", labelPaint);
        labelStart = labels.add(gl, "Touch Start!!", labelPaint);
        labelGameOver = labels.add(gl, "GAME OVER", labelPaint);
        labels.endAdding(gl);

        // ���l������𐶐�
        if (numericSprite != null)
        {
            numericSprite.shutdown(gl);
        }
        else
        {
            numericSprite = new NumericSprite();
        }
        numericSprite.initialize(gl, labelPaint);

        // ���\�[�X��ǂݍ���Ńe�N�X�`���𐶐�
        playerTexID = TextureLoader.loadTexture(gl, this, R.drawable.img0);
        enemyTexID = TextureLoader.loadTexture(gl, this, R.drawable.img1);
        playerBulletTexID = TextureLoader
                .loadTexture(gl, this, R.drawable.img2);
        enemyBulletTexID = TextureLoader.loadTexture(gl, this, R.drawable.img3);
    }

    /**
     * @Override �ꎞ��~����̍ĊJ
     */
    protected void onResume()
    {
        super.onResume();
        // �T�E���h���Đ�
        soundPlayer.playBGM(nowBGMID, true);
    }

    /**
     * @Override ��~��Ԃ���̍ĊJ
     */
    protected void onRestart()
    {
        super.onRestart();
        // �T�E���h���Đ�
        soundPlayer.playBGM(nowBGMID, true);
    }

    /**
     * @Override �A�N�e�B�r�e�B�ꎞ��~���ɌĂяo�����
     */
    protected void onPause()
    {
        super.onPause();
        // �ꎞ��~���ɂ͉����~�߂�
        soundPlayer.stopAllSound();
    }

    /**
     * @Override �A�N�e�B�r�e�B��~���ɌĂяo�����
     */
    protected void onStop()
    {
        super.onStop();
        // ��~���ɂ͉����~�߂�
        soundPlayer.stopAllSound();
    }

    /**
     * @Override �A�N�e�B�r�e�B�I�����ɌĂяo�����
     */
    protected void onDestroy()
    {
        super.onDestroy();
        // MediaPlayer���J������
        soundPlayer.finalizeSoundPlayer();
    }
}