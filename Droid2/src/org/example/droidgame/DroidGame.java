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
 * �Q�[���ŗL�̏������L�q�����N���X
 */
public class DroidGame extends GameBase
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
   // private int playerTexID, enemyTexID, playerBulletTexID, enemyBulletTexID;
    private int  droidTexID, droidBulletTexID;
    private EnumMap<DroidType, Integer> droidEnumMap;

    // �^�b�`���Ɏ��@��G���Ă��邩�ǂ������肷�邽�߂̃T�C�Y
    private static final int FINGER_RADIUS = 50;
//    // ���@
//    private static final float PLAYER_SPEED = 6.0f;
//    private Player player;

//    // �V���b�g�p�̔z��
//    private static final int PLAYER_SHOT_NUM = 16;
//    private PlayerShot[] playerShot = new PlayerShot[PLAYER_SHOT_NUM];

    // �G�p�̔z��
    private static final int MUL_DROID_TIME = 10;//�K�������ԃ����_�����iMIN_DROID_TIME�ɏ�悹�j
    private static final int MIN_DROID_TIME = 10;//�ŏ��G��������
    private static final int DROID_NUM = 20;//�z��̐�
    private Droid[] droids = new Droid[DROID_NUM];
    // ���ɓG�𐶐�����܂ł̃J�E���^
    private int droidCounter;

    // �G�̒e�p�̔z��
//    private static final int ENEMY_SHOT_NUM = 32;
//    private EnemyShot[] enemyShot = new EnemyShot[ENEMY_SHOT_NUM];

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
    public DroidGame()
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
        //playerTexID = -1;
        droidTexID = -1;
        //playerBulletTexID = -1;
        droidBulletTexID = -1;

        // ��������Z�b�g
        labels = null;
        numericSprite = null;
        labelPaint = new Paint();
        labelPaint.setTextSize(32);
        labelPaint.setAntiAlias(true);
        labelPaint.setARGB(0xff, 0xff, 0xff, 0xff);

        // ���@�𐶐�
      //  player = new Player(this, soundPlayer, 0.0f, Player.RADIUS);

        // �V���b�g�p�̔z��𐶐�
//        for (int i = 0; i < PLAYER_SHOT_NUM; i++)
//            playerShot[i] = new PlayerShot();
        // Enemy�p�̔z��𐶐�
        
        for (int i = 0; i < DROID_NUM; i++){        	
        	 droids[i] = new Droid(this, soundPlayer);
        }
       //     enemy[i] = new Enemy(this, soundPlayer);
        	  droidCounter = 0;

          	  
        	  
        	  
        	  
        // �G�V���b�g�p�̔z��𐶐�
//        for (int i = 0; i < ENEMY_SHOT_NUM; i++)
//            enemyShot[i] = new EnemyShot();

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
        for (int i = 0; i < DROID_NUM; i++)
            droids[i].isAlive = false;

        droidCounter = 0;
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

            // �S�G���X�V
            for (int i = 0; i < DROID_NUM; i++)
            {
                if (droids[i].isAlive)
                {
                	droids[i].update();

                    // ��ʒ[�ƒ��������Œ��˕Ԃ�
                    if (droids[i].x < 0 || droids[i].x > surfaceWidth)
                    	droids[i].speedX = -droids[i].speedX;
                    if (droids[i].y < surfaceHeight / 2
                            || droids[i].y > surfaceHeight)
                    	droids[i].speedY = -droids[i].speedY;
                }
            }

            // �G�𐶐�
            if (droidCounter < 0)
                createEnemy();
            droidCounter--;


            // �V���b�g�ƓG�̓����蔻��
//            for (int i = 0; i < PLAYER_SHOT_NUM; i++)
//            {
//                if (playerShot[i].isAlive)
//                {
//                    for (int j = 0; j < DROID_NUM; j++)
//                    {
//                        if (droids[j].isAlive && !droids[j].isDeathState)
//                        {
//                            // �G�ƃV���b�g���Փ˂��Ă����
//                            if (playerShot[i].checkCollision(droids[j]))
//                            {
//                                // ���C�t������āA�G�����񂾂�X�R�A���Z
//                                if (droids[j].decLife())
//                                    score++;
//                                // �V���b�g������
//                                playerShot[i].isAlive = false;
//                            }
//                        }
//                    }
//                }
//            }

            // ��ʊO�ɏo�Ă�����폜
            for (int i = 0; i < DROID_NUM; i++)
            {
                // �L���ȃV���b�g�ɂ��Ă̂ݏ���
                if (droids[i].isAlive)
                {
                    if (droids[i].y + droids[i].r > surfaceHeight)
                    	droids[i].isAlive = false;
                }
            }

        }
    }

    /**
     * �t���[�ȃC���X�^���X�������āAEnemy�𐶐�����
     */
    private void createEnemy()
    {
        for (int i = 0; i < DROID_NUM; i++)
        {
            // ���g�p��Enemy�Ȃ�
            if (!droids[i].isAlive)
            {
            	DroidType type = DroidType.getRandomType();
                // �L���ɂ���
            	droids[i].execute(type,droidEnumMap.get(type));

                // ���ʉ��Đ�
                soundPlayer.playSE(R.raw.se_apeear_enemy);
                break;
            }
        }
        // ����Enemy�𐶐����鎞�Ԃ��Z�b�g
        droidCounter = (int) (Math.random() * MUL_DROID_TIME) + MIN_DROID_TIME;
    }





//    /**
//     * �^�b�`���̃v���C���[���W�ړ����� touchDown, touchMove�ŌĂ΂��
//     *
//     * @param x �^�b�`�C�x���g�œn���ꂽX���W
//     * @param y �^�b�`�C�x���g�œn���ꂽY���W
//     */
//    private void updateTouchPlayer(float x, float y)
//    {
//        // �X�N���[�����W����OpenGL�̃��[���h���W�ɕϊ�����
//        float tmp_y = this.surfaceHeight - y;
//
//        if (tmp_y < player.y + FINGER_RADIUS
//                && tmp_y > player.y - FINGER_RADIUS)
//        {
//            // ���@���^�b�`���Ă���΃V���b�g���o��
//            if ((x - player.x) * (x - player.x) + (tmp_y - player.y)
//                    * (tmp_y - player.y) <= FINGER_RADIUS * FINGER_RADIUS)
//            {
//                player.setShotState(true);
//                player.setSpeed(0.0f);
//            }
//            // ���@��荶���^�b�`���Ă���΍���
//            else if (x < player.x - FINGER_RADIUS)
//            {
//                player.setSpeed(-PLAYER_SPEED);
//            }
//            // ���@���E���^�b�`���Ă���ΉE��
//            else if (x > player.x + FINGER_RADIUS)
//            {
//                player.setSpeed(PLAYER_SPEED);
//            }
//        }
//    }

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
           //TODO �G�ƏՓ˔��肷��
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
    //�Ƃ肠�����������Ȃ�
//        if (gameState == GAMESTATE_PLAYING)
//        {
//            // �ړ�����߂�
//            player.setSpeed(0.0f);
//            // �V���b�g���o���̂���߂�
//            player.setShotState(false);
//        }
    }

    /**
     * �^�b�`��Ԃ̎w���X���C�h������
     *
     * @param x �^�b�`�C�x���g�œn���ꂽX���W
     * @param y �^�b�`�C�x���g�œn���ꂽY���W
     */
    private void touchMove(float x, float y)
    {
        //�Ƃ肠�����������Ȃ�    	
        if (gameState == GAMESTATE_PLAYING)
        {
          //  updateTouchPlayer(x, y);
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
//            player.draw(gl, playerTexID);

            // Enemy��`��
            for (int i = 0; i < DROID_NUM; i++)
            {
                if (droids[i].isAlive)
                	droids[i].draw(gl, droidTexID);
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
   //     labelFps = labels.add(gl, "FPS:", labelPaint);
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
  
        
  		// �h���C�h�N�̎�ށitype�j���ƂɃe�N�X�`���𐶐�����EnumMap��type��texture_id��o�^���Ă���
  		droidEnumMap = new EnumMap<DroidType, Integer>(DroidType.class);

  		DroidType[] doidTypeValue = DroidType.values();
  		for (DroidType type : doidTypeValue) {
  			Integer TextureID = TextureLoader.loadTexture(gl, this,
  					type.getImageId());// �e�N�X�`������āA�A�N�Z�X�ł���texture_id ��Ԃ�
  			droidEnumMap.put(type, TextureID);
  		}      


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
