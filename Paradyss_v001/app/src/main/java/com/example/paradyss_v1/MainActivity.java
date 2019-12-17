
package com.example.paradyss_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    //Using the Accelometer & Gyroscoper
    private SensorManager mSensorManager = null;

    //Using the Accelometer
    private SensorEventListener mAccLis;
    private Sensor mAccelometerSensor = null;

    //게임 화면 보정용
    public int x = 0;
    public int y = 0;

    //게임 화면 센서 조정 용도
    public double Xval = 0;
    public double Yval = 0;
    public double Zval = 0;

    // 위 아래 블록 세트 여부 0이면 1->2 / 1이면 2->1
    public int blockBool = 0;
    public int blockNow = 0;

    // 블록 전용 - 일단 시간 세로 값 20행 26열
    private int [][] Stand1 = new int[20][26];
    private int [][] Stand2 = new int[20][26];

    // 20행, 70열 - 1행당 실제로는 25개의 블록이 된다.
    private int [][] Row1 = new int[20][50];
    private int [][] Row2 = new int[20][50];

    private BlockMaker [][] blocks1 = new BlockMaker[20][25];
    //둘을 교체하는 증간 깂
    //BlockMaker[][] blocksM1 = new BlockMaker[1][25];

    private BlockMaker [][] blocks2 = new BlockMaker[20][25];
    //둘을 교체하는 중간 값
    //BlockMaker[][] blocksM2 = new BlockMaker[1][25];

    //10개 행 중에서 4칸을 넘어가면 새로

    MyView my_nv;
    // 우선 뷰는 새로 하나 만든다.
    //MyView my_nv = new MyView(this);

    //시작, 끝 관련 인수
    int Start_Count = 0;
    int End_count = 0;


    //점프 관련 값
    int hopOn = 0;
    double hopCount = 0;
    int hopMaintain = 0;

    double hopAccel = 0;

    //비어있는 공간 측정, 현재까지 뚫린 개수도 보관
    int emptycount = 0;
    int [] emptyBlock = new int[100];

    //블록 타입을 평볌하게 저장
    int [] blockType = new int[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //풀 스크린은 메니페스트에서 적용했고, 하단 바 삭제는 아래서 진행한다.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("Is on?", "Turning immersive mode mode off. ");
        } else {
            Log.i("Is on?", "Turning immersive mode mode on.");
        }

        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);


        //회전 불가 옵션
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);


        // 이게 My view를 받아서 동작시키는 제일 중요한 함수----------------------------------------
        //setContentView(new MyView(this));

        my_nv = new MyView(this);

        //랜덤 타입 블록 생성--------------------------
        rand_block_type(my_nv.random_block_out,my_nv.random_block_in);
        //시작할 때 구멍 1개 뚫기
        emptycount=0;
        emptyBlock[emptycount] = 20;


        setContentView(my_nv);


        //그림 그릴 때는 아래를 지운다 일반 레이아웃은 없애야 겠지..
        //setContentView(R.layout.activity_main);


        //본격 로테이션 벡터 받아서 작동시키기!
        //Using the Gyroscope & Accelometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);



        //Using the Accelometer
        //mAccelometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //본격적인 처리, 작동을 시작한다.
        mAccelometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mAccLis = new AccelometerListener();
        mSensorManager.registerListener(mAccLis, mAccelometerSensor, SensorManager.SENSOR_DELAY_UI);


        //아랫 부분은 주석처리로
        //Touch Listener for Accelometer
        /*
        findViewById(R.id.a_start).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        mSensorManager.registerListener(mAccLis, mAccelometerSensor, SensorManager.SENSOR_DELAY_UI);
                        break;

                    case MotionEvent.ACTION_UP:
                        mSensorManager.unregisterListener(mAccLis);
                        break;

                }
                return false;
            }
        });
        */

        //블록 생산 30행과 50열
        //이게 넘어가면




        //이게 게임 진행의 타이머 역할을 한다.
        // 휴.. 성공!

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {

                //특정 프레임 후 시작한다.
                if (Start_Count != -1){

                    StartDelay(20);
                }
                else if (Start_Count == -1) {
                    if (Xval >= 0) {
                        hopper(44, 74, -1);
                    } else if (Xval <= 0) {
                        hopper(44, 74, 1);
                    }
                    //my_nv.x = my_nv.x+5;
                    //my_nv.y = my_nv.y+5;

                }

                EndDelay();
            }
        };
        //이건 사실상 프레임 지정 용도이다..
        Timer timer = new Timer();
        timer.schedule(tt, 0,50);

    }

    public void StartDelay(int i){
        my_nv.emptycount = emptycount;
        my_nv.emptyblock = emptyBlock;
        Start_Count = Start_Count + 1;
        if(Start_Count == i){
            Start_Count = -1;
        }

    }

    public void EndDelay(){
        if(my_nv.life <= 0){
            Start_Count = 500;
        }
    }

    public void hopper(int xx,int yy, int dir){

        //뛰지 않는 버전 0 / 뛰는 버전 1
        // 0은 준비 시간 / 1은 뛰는 시간
        if(hopOn == 0){
            if(hopCount >= 10){
                hopCount = 10;
                hopOn = 1;
                hopMaintain = dir;
            }
            else {
                hopCount = hopCount + 1 +hopAccel;
            }
        //그냥 돌진이 아닐 때의 모습을 그려보자
        if (Xval>0.5){
            //제일 큰 값
            my_nv.digger_mode = 10;
        }
        else if (Xval > 0.45){
            my_nv.digger_mode = 9;
        }
        else if (Xval > 0.4){
            my_nv.digger_mode = 8;
        }
        else if (Xval > 0.35){
            my_nv.digger_mode = 7;
        }
        else if (Xval > 0.3){
            my_nv.digger_mode = 6;
        }
        else if (Xval > 0.25){
            my_nv.digger_mode = 5;
        }
        else if (Xval > 0.20){
            my_nv.digger_mode = 4;
        }
        else if (Xval > 0.15){
            my_nv.digger_mode = 3;
        }
        else if (Xval > 0.10){
            my_nv.digger_mode = 2;
        }
        else if (Xval > 0.05){
            my_nv.digger_mode = 1;
        }
        else if (Xval > -0.05){
            my_nv.digger_mode = 0;
        }
        else if (Xval > -0.10){
            my_nv.digger_mode = 11;
        }
        else if (Xval > -0.15){
            my_nv.digger_mode = 12;
        }
        else if (Xval > -0.20){
            my_nv.digger_mode = 13;
        }
        else if (Xval > -0.25){
            my_nv.digger_mode = 14;
        }
        else if (Xval > -0.30){
            my_nv.digger_mode = 15;
        }
        else if (Xval > -0.35){
            my_nv.digger_mode = 16;
        }
        else if (Xval > -0.40){
            my_nv.digger_mode = 17;
        }
        else if (Xval > -0.45){
            my_nv.digger_mode = 18;
        }
        else if (Xval > -0.5){
            my_nv.digger_mode = 19;
        }
        else if (Xval <= -0.5){
            my_nv.digger_mode = 20;
        }
        else{
            my_nv.digger_mode = 0;
        }


        }
        else if(hopOn == 1){
            my_nv.life = my_nv.life - 5;
            if(hopCount > 0){
                if(hopCount == 10){
                    if(hopMaintain == 1) {
                        my_nv.x = my_nv.x - 30;
                        my_nv.y = my_nv.y - 50;
                    }
                    else if(hopMaintain == - 1){
                        my_nv.x = my_nv.x + 30;
                        my_nv.y = my_nv.y - 50;
                    }
                }
                else if(hopCount == 8){
                    if(hopMaintain == 1) {
                        my_nv.x = my_nv.x - 50;
                        my_nv.y = my_nv.y - 84;
                    }
                    else if(hopMaintain == - 1){
                        my_nv.x = my_nv.x + 50;
                        my_nv.y = my_nv.y - 84;
                    }
                }
                //여기서 그림을 없앤다.
                else if(hopCount == 6){
                    if(hopMaintain == 1) {
                        if((emptycount % 2)==0){
                            emptycount++;
                            emptyBlock[emptycount]=emptyBlock[emptycount-1];
                            my_nv.emptycount = emptycount;
                            my_nv.emptyblock = emptyBlock;
                            my_nv.x = my_nv.x - 70;
                            my_nv.y = my_nv.y - 117;
                        }
                        else if((emptycount % 2)==1){
                            emptycount++;
                            emptyBlock[emptycount]=emptyBlock[emptycount-1]+1;
                            my_nv.emptycount = emptycount;
                            my_nv.emptyblock = emptyBlock;
                            my_nv.x = my_nv.x - 70;
                            my_nv.y = my_nv.y - 117;
                        }
                    }
                    else if(hopMaintain == - 1){
                        if((emptycount % 2)==0){
                            emptycount++;
                            emptyBlock[emptycount]=emptyBlock[emptycount-1]-1;
                            my_nv.emptycount = emptycount;
                            my_nv.emptyblock = emptyBlock;
                            my_nv.x = my_nv.x + 70;
                            my_nv.y = my_nv.y - 117;
                        }
                        else if((emptycount % 2)==1){
                            emptycount++;
                            emptyBlock[emptycount]=emptyBlock[emptycount-1];
                            my_nv.emptycount = emptycount;
                            my_nv.emptyblock = emptyBlock;
                            my_nv.x = my_nv.x + 70;
                            my_nv.y = my_nv.y - 117;
                        }
                    }
                }
                else if(hopCount == 4){
                    if(hopMaintain == 1) {
                        my_nv.x = my_nv.x - 40;
                        my_nv.y = my_nv.y - 67;
                    }
                    else if(hopMaintain == - 1){
                        my_nv.x = my_nv.x + 40;
                        my_nv.y = my_nv.y - 67;
                    }
                } //가속을 추가로 적는다..
                else if(hopCount == 2){
                    if(hopMaintain == 1) {
                        my_nv.x = my_nv.x - 30;
                        my_nv.y = my_nv.y - 52;
                        if(hopAccel < 1) {
                            hopAccel = hopAccel + 0.07;
                        }
                    }
                    else if(hopMaintain == - 1){
                        my_nv.x = my_nv.x + 30;
                        my_nv.y = my_nv.y - 52;
                        if(hopAccel < 1) {
                            hopAccel = hopAccel + 0.07;
                        }
                    }
                }



                hopCount = hopCount - 2;
            }
            else if(hopCount == 0){
                hopMaintain = 0;
                hopOn = 0;
            }
        }

    }


    @Override
    public void onPause(){
        super.onPause();
        Log.e("LOG", "onPause()");
        mSensorManager.unregisterListener(mAccLis);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e("LOG", "onDestroy()");
        mSensorManager.unregisterListener(mAccLis);
    }

    // 대형 클래스
    public class AccelometerListener implements SensorEventListener {

        /*
        public double Xval = 0;
        public double Yval = 0;
        public double Zval = 0;
        */
        @Override
        public void onSensorChanged(SensorEvent event) {
            double q0 = event.values[3];
            double q1 = event.values[0];
            double q2 = event.values[1];
            double q3 = event.values[2];

            Xval = 2 * ((q1*q3)-(q0*q2));
            Yval = 2 * ((q2*q3)+(q0*q1));
            Zval = ((q0*q0)-(q1*q1)-(q2*q2)+(q3*q3));

            /*
            Log.e("LOG", "ACCELOMETER           [q0]:" + String.format("%.4f", q0)
                    + "           [q1]:" + String.format("%.4f", q1)
                    + "           [q2]:" + String.format("%.4f", q2)
                    + "           [q3]:" + String.format("%.4f", q3)
                    + "           [Xval]:" + String.format("%.4f", Xval)
                    + "           [Yval]:" + String.format("%.4f", Yval)
                    + "           [Zval]:" + String.format("%.4f", Zval) );
            */

            /*
            double accX = event.values[0];
            double accY = event.values[1];
            double accZ = event.values[2];

            //double angleXZ = Math.atan2(accX,  accZ) * 180/Math.PI;
            //double angleYZ = Math.atan2(accY,  accZ) * 180/Math.PI;

            Log.e("LOG", "ACCELOMETER           [X]:" + String.format("%.4f", event.values[0])
                    + "           [Y]:" + String.format("%.4f", event.values[1])
                    + "           [Z]:" + String.format("%.4f", event.values[2])
                    + "           [angleXZ]: " + String.format("%.4f", angleXZ)
                    + "           [angleYZ]: " + String.format("%.4f", angleYZ));
            */
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }//클래스

    //시작 열 생성 함수
    public void blockCreate_S(){

        for(int i = 0; i<5;i++) {
            Row1[0][(i*10)+0] = 2;
            Row1[0][(i*10)+1] = 2;
            Row1[0][(i*10)+2] = 3;
            Row1[0][(i*10)+3] = 2;
            Row1[0][(i*10)+4] = 3;
            //
            Row1[0][(i*10)+5] = 3;
            Row1[0][(i*10)+6] = 2;
            Row1[0][(i*10)+7] = 3;
            Row1[0][(i*10)+8] = 3;
            Row1[0][(i*10)+9] = 2;
        }

        for(int k=0; k<20; k++){
            Stand1[k][0] = 0;
        }

    }
    //본격 블록 계산하기 함수

    public void blockCreate() {

            /*
            private int [] Stand1 = new int[20];
            private int [] Stand2 = new int[20];

            // 20행, 70열 - 1행당 실제로는 25개의 블록이 된다.
            private int [][] Row1 = new int[20][50];
            private int [][] Row2 = new int[20][50];

            private BlockMaker [][] blocks1 = new BlockMaker[20][25];
            //둘을 교체하는 증간 깂
            //BlockMaker[][] blocksM1 = new BlockMaker[1][25];

            private BlockMaker [][] blocks2 = new BlockMaker[20][25];
            */
        //1에서 2, 2에서 1 으로 구분해서 만든다.

        int [] impN = new int[3];
        impN[0] = 0;
        impN[1] = 0;
        impN[2] = 0;


        int impM1, impM2, impM3;
        int [] impRes= new int[3];

        if (blockBool == 0) {

            for (int i = 0; i < 19; i++) {
                if (i % 2 == 0) {
                    for (int j = 0; j < 25; j++) {
                        impM1 = Row1[i ][2 * j];
                        impM2 = Row1[i ][2 * j + 1];
                        impM3 = Stand1[i ][j];

                        impRes = rand_blocks(impM1, impM2, impM3);
                        Row1[i+1][2 * j] = impRes[0];
                        Row1[i+1][2 * j + 1] = impRes[1];
                        Stand1[i+1][j + 1] = impRes[2];
                    }
                }//홀수 기준 배치
                else {
                    Row1[i+1][0] = Row1[i][0];

                    for (int j = 1; j < 25; j++) {
                        impM1 = Row1[i ][2 * j-1];
                        impM2 = Row1[i ][2 * j];
                        impM3 = Stand1[i ][j-1];

                        impRes = rand_blocks(impM1, impM2, impM3);
                        Row1[i+1][2 * j-1] = impRes[0];
                        Row1[i+1][2 * j] = impRes[1];
                        Stand1[i+1][j] = impRes[2];
                    }
                    Row1[i+1][49] = Row1[i][49];
                }//홀수 기준 배치
            }//반복문 끝, 이제 다음 첫째 열만 제시한다.



        }
        else if (blockBool == 1){




        }

    }
    //함수 끝

    //본격 랜덤한 값 생성
    public int[] rand_blocks(int LT,int RT, int LM){

        Random rand = new Random();
        //int tt = rand.nextInt(10); //0~9 출력한다.
        int rand_num = 0;

        int count = 0;
        //RM 기준으로 작성한다.
        int [] example_LD = new int[10];
        //임시로 이 3개의 값을 연산
        int imp_RD = 0;
        int imp_LD = 0;
        int imp_RM = 0;

        for(int i=1; i<LT+RT; i++){
            imp_LD = i;
            imp_RD = LT+RT-i;
            imp_RM = (LT+LM+imp_LD)-(RT+imp_RD);
            if(imp_RM > 0){
                example_LD[count] = imp_LD;
                count = count + 1;
            }
        }
        rand_num = rand.nextInt(count);
        imp_LD = example_LD[rand_num];
        imp_RD = LT+RT-imp_LD;
        imp_RM = (LT+LM+imp_LD)-(RT+imp_RD);




        //LD, RD, RM 전송하기
        int [] blocks = new int[3];
        blocks[0] = imp_LD;
        blocks[1] = imp_RD;
        blocks[2] = imp_RM;
        return blocks;
    }

    void rand_block_type(int []a, int []b){
        int totalBlocks = 4000;
        Random rand = new Random();
        int nn = 0;
        for(int i = 0; i< totalBlocks; i++){
            nn = rand.nextInt(5); //0부터 4까지 제공
            if(nn == 0){
                a[i] = Color.rgb(94,44,0);
                b[i] = Color.rgb(114,70,0);
                //c[i] = 0;
            }
            else if(nn == 1){
                a[i] = Color.rgb(80,10,0);
                b[i] = Color.rgb(104,41,0);
                //c[i] = 1;
            }
            else if(nn == 2){
                a[i] = Color.rgb(59,13,13);
                b[i] = Color.rgb(78,34,17);
                //c[i] = 2;
            }
            else if(nn == 3){
                a[i] = Color.rgb(41,16,19);
                b[i] = Color.rgb(84,62,58);
                //c[i] = 3;
            }
            else if(nn == 4){
                a[i] = Color.rgb(45,39,39);
                b[i] = Color.rgb(81,75,77);
                //c[i] = 4;
            }
        }
    }

}

class MyView extends View {

    //일단 현재 블록 개수는 2000개이다.
    static int totalBlocks = 4000;

    Paint[] paint_block;
    Path[] path_block;

    Paint[] paint_blockline;
    Path[] path_blockline;

    //이 만큼 이동했다고 보정해서 출력해 주는 것이다!
    public int x;
    public int y;

    int[] random_block_in = new int[totalBlocks];
    int[] random_block_out = new int[totalBlocks];

    //그리지 말아야 하는 블록 표기
    int emptycount = 0;
    int[] emptyblock = new int[100];

    //Paint paint_block2;
    //Path path_block2;

    //게임을 위한 시간 값
    long timer;

    //땅굴맨 그리기 전용
    public  Bitmap [] digger = new Bitmap[21];
    public int digger_mode = 0; // 이 값을 기준으로 표시한다.


    //남은 생명을 위한 값
    int life = 800;

    public MyView(Context context) {
        super(context);
        base_setting();

        //좌우 반전용으로 만들었다.
        Matrix sideInversion = new Matrix();
        sideInversion.setScale(-1, 1);



        Resources r = context.getResources();

        digger[0] = BitmapFactory.decodeResource(r, R.drawable.character_0);

        digger[1] = BitmapFactory.decodeResource(r, R.drawable.character_1);
        digger[11] = Bitmap.createBitmap(digger[1], 0, 0,
                digger[1].getWidth(), digger[1].getHeight(), sideInversion, false);

        digger[2] = BitmapFactory.decodeResource(r, R.drawable.character_2);
        digger[12] = Bitmap.createBitmap(digger[2], 0, 0,
                digger[2].getWidth(), digger[2].getHeight(), sideInversion, false);

        digger[3] = BitmapFactory.decodeResource(r, R.drawable.character_3);
        digger[13] = Bitmap.createBitmap(digger[3], 0, 0,
                digger[3].getWidth(), digger[3].getHeight(), sideInversion, false);

        digger[4] = BitmapFactory.decodeResource(r, R.drawable.character_4);
        digger[14] = Bitmap.createBitmap(digger[4], 0, 0,
                digger[4].getWidth(), digger[4].getHeight(), sideInversion, false);

        digger[5] = BitmapFactory.decodeResource(r, R.drawable.character_5);
        digger[15] = Bitmap.createBitmap(digger[5], 0, 0,
                digger[5].getWidth(), digger[5].getHeight(), sideInversion, false);

        digger[6] = BitmapFactory.decodeResource(r, R.drawable.character_6);
        digger[16] = Bitmap.createBitmap(digger[6], 0, 0,
                digger[6].getWidth(), digger[6].getHeight(), sideInversion, false);

        digger[7] = BitmapFactory.decodeResource(r, R.drawable.character_7);
        digger[17] = Bitmap.createBitmap(digger[7], 0, 0,
                digger[7].getWidth(), digger[7].getHeight(), sideInversion, false);

        digger[8] = BitmapFactory.decodeResource(r, R.drawable.character_8);
        digger[18] = Bitmap.createBitmap(digger[8], 0, 0,
                digger[8].getWidth(), digger[8].getHeight(), sideInversion, false);

        digger[9] = BitmapFactory.decodeResource(r, R.drawable.character_9);
        digger[19] = Bitmap.createBitmap(digger[9], 0, 0,
                digger[9].getWidth(), digger[9].getHeight(), sideInversion, false);

        digger[10] = BitmapFactory.decodeResource(r, R.drawable.character_10);
        digger[20] = Bitmap.createBitmap(digger[10], 0, 0,
                digger[10].getWidth(), digger[10].getHeight(), sideInversion, false);



    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void base_setting() {
        paint_block = new Paint[totalBlocks];
        for (int i = 0; i < totalBlocks; i++) {
            paint_block[i] = new Paint();
            paint_block[i].setStrokeWidth(40);
        }

        path_block = new Path[totalBlocks];
        for (int i = 0; i < totalBlocks; i++) {
            path_block[i] = new Path();
        }

        //바깥 외곽성
        paint_blockline = new Paint[totalBlocks];
        path_blockline = new Path[totalBlocks];
        for (int i = 0; i < totalBlocks; i++) {
            paint_blockline[i] = new Paint();
            paint_blockline[i].setStrokeWidth(7);
            path_blockline[i] = new Path();
        }

    }


    //여기서 사실상 모든 것을 그린다.
    @Override
    public void onDraw(Canvas canvas) {

        //SystemClock.elapsedRealtime();

        super.onDraw(canvas);
        canvas.drawColor(Color.rgb(145, 80, 7));

        // 약간의 위치 보정 값
        // x는 n y는 m
        int n = 135;
        int m = 100;

        int changer = 0;

        int n_d = 110;

        //외곽선 보정값
        int b_1 = 20;
        int b_2 = 17;
        int b_3 = 10;

        int a = Color.rgb(114, 70, 0);


        int game_width = 40;

        //j는 높이, i는 너비
        for (int j = 0; j < 100; j++) {
            for (int i = 0; i < game_width; i++) {

                //짝수모드 0 / 홀수모드 1
                if (changer == 1) {
                    //이게 기본이 되어야 한다.
                    path_block[i + game_width * j].reset();
                    path_block[i + game_width * j].moveTo(200 + (400 + 40) * (i - 20) + n + x, 400 + (480 - n_d) * (j - 3) + m + y);
                    path_block[i + game_width * j].lineTo(400 + (400 + 40) * (i - 20) + n + x, 300 + (480 - n_d) * (j - 3) + m + y);
                    path_block[i + game_width * j].lineTo(600 + (400 + 40) * (i - 20) + n + x, 400 + (480 - n_d) * (j - 3) + m + y);
                    path_block[i + game_width * j].lineTo(600 + (400 + 40) * (i - 20) + n + x, 640 + (480 - n_d) * (j - 3) + m + y);
                    path_block[i + game_width * j].lineTo(400 + (400 + 40) * (i - 20) + n + x, 740 + (480 - n_d) * (j - 3) + m + y);
                    path_block[i + game_width * j].lineTo(200 + (400 + 40) * (i - 20) + n + x, 640 + (480 - n_d) * (j - 3) + m + y);
                    path_block[i + game_width * j].lineTo(200 + (400 + 40) * (i - 20) + n + x, 400 + (480 - n_d) * (j - 3) + m + y);
                    path_block[i + game_width * j].close();

                    path_blockline[i + game_width * j].reset();
                    path_blockline[i + game_width * j].moveTo(200 + (400 + 40) * (i - 20) + n - b_2 + x, 400 + (480 - n_d) * (j - 3) - b_3 + m + y);
                    path_blockline[i + game_width * j].lineTo(400 + (400 + 40) * (i - 20) + n + x, 300 + (480 - n_d) * (j - 3) - b_1 + m + y);
                    path_blockline[i + game_width * j].lineTo(600 + (400 + 40) * (i - 20) + n + b_2 + x, 400 + (480 - n_d) * (j - 3) - b_3 + m + y);
                    path_blockline[i + game_width * j].lineTo(600 + (400 + 40) * (i - 20) + n + b_2 + x, 640 + (480 - n_d) * (j - 3) + b_3 + m + y);
                    path_blockline[i + game_width * j].lineTo(400 + (400 + 40) * (i - 20) + n + x, 740 + (480 - n_d) * (j - 3) + b_1 + m + y);
                    path_blockline[i + game_width * j].lineTo(200 + (400 + 40) * (i - 20) + n - b_2 + x, 640 + (480 - n_d) * (j - 3) + b_3 + m + y);
                    path_blockline[i + game_width * j].lineTo(200 + (400 + 40) * (i - 20) + n - b_2 + x, 400 + (480 - n_d) * (j - 3) - b_3 + m + y);
                    path_blockline[i + game_width * j].close();


                } else {
                    path_block[i + game_width * j].reset();
                    path_block[i + game_width * j].moveTo(200 + 220 + (400 + 40) * (i - 20) + n + x, 400 + (480 - n_d) * (j - 3) + m + y);
                    path_block[i + game_width * j].lineTo(400 + 220 + (400 + 40) * (i - 20) + n + x, 300 + (480 - n_d) * (j - 3) + m + y);
                    path_block[i + game_width * j].lineTo(600 + 220 + (400 + 40) * (i - 20) + n + x, 400 + (480 - n_d) * (j - 3) + m + y);
                    path_block[i + game_width * j].lineTo(600 + 220 + (400 + 40) * (i - 20) + n + x, 640 + (480 - n_d) * (j - 3) + m + y);
                    path_block[i + game_width * j].lineTo(400 + 220 + (400 + 40) * (i - 20) + n + x, 740 + (480 - n_d) * (j - 3) + m + y);
                    path_block[i + game_width * j].lineTo(200 + 220 + (400 + 40) * (i - 20) + n + x, 640 + (480 - n_d) * (j - 3) + m + y);
                    path_block[i + game_width * j].lineTo(200 + 220 + (400 + 40) * (i - 20) + n + x, 400 + (480 - n_d) * (j - 3) + m + y);
                    path_block[i + game_width * j].close();

                    path_blockline[i + game_width * j].reset();
                    path_blockline[i + game_width * j].moveTo(200 + 220 + (400 + 40) * (i - 20) + n - b_2 + x, 400 + (480 - n_d) * (j - 3) - b_3 + m + y);
                    path_blockline[i + game_width * j].lineTo(400 + 220 + (400 + 40) * (i - 20) + n + x, 300 + (480 - n_d) * (j - 3) - b_1 + m + y);
                    path_blockline[i + game_width * j].lineTo(600 + 220 + (400 + 40) * (i - 20) + n + b_2 + x, 400 + (480 - n_d) * (j - 3) - b_3 + m + y);
                    path_blockline[i + game_width * j].lineTo(600 + 220 + (400 + 40) * (i - 20) + n + b_2 + x, 640 + (480 - n_d) * (j - 3) + b_3 + m + y);
                    path_blockline[i + game_width * j].lineTo(400 + 220 + (400 + 40) * (i - 20) + n + x, 740 + (480 - n_d) * (j - 3) + b_1 + m + y);
                    path_blockline[i + game_width * j].lineTo(200 + 220 + (400 + 40) * (i - 20) + n - b_2 + x, 640 + (480 - n_d) * (j - 3) + b_3 + m + y);
                    path_blockline[i + game_width * j].lineTo(200 + 220 + (400 + 40) * (i - 20) + n - b_2 + x, 400 + (480 - n_d) * (j - 3) - b_3 + m + y);
                    path_blockline[i + game_width * j].close();
                }
            }
            if (changer == 0) {
                changer = 1;
            } else {
                changer = 0;
            }
        }
        /*
        path_block2.reset();
        path_block2.moveTo(200+400,400);
        path_block2.lineTo(400+400,300);
        path_block2.lineTo(600+400,400);
        path_block2.lineTo(600+400, 600);
        path_block2.lineTo(400+400,700);
        path_block2.lineTo(200+400,600);
        path_block2.lineTo(200+400,400);
        path_block2.close();
        */

        for (int j = 0; j < 100; j++) {
            for (int i = 0; i < game_width; i++) {

                //보정 값
                if (j>=3){
                    if(i != emptyblock[j-3]){

                        paint_block[j * game_width + i].setColor(random_block_in[j * game_width + i]);
                        paint_block[j * game_width + i].setStyle(Paint.Style.FILL);
                        canvas.drawPath(path_block[j * game_width + i], paint_block[j * game_width + i]);
                        //속 채우기

                        paint_block[j * game_width + i].setColor(random_block_out[j * game_width + i]);
                        paint_block[j * game_width + i].setStyle(Paint.Style.STROKE);
                        canvas.drawPath(path_block[j * game_width + i], paint_block[j * game_width + i]);
                        //외곽선

                        paint_blockline[j * game_width + i].setColor(Color.rgb(38, 21, 2));
                        paint_blockline[j * game_width + i].setStyle(Paint.Style.STROKE);
                        canvas.drawPath(path_blockline[j * game_width + i], paint_blockline[j * game_width + i]);
                        //검은 외곽선


                    }
                }
                else{
                    paint_block[j * game_width + i].setColor(random_block_in[j * game_width + i]);
                    paint_block[j * game_width + i].setStyle(Paint.Style.FILL);
                    canvas.drawPath(path_block[j * game_width + i], paint_block[j * game_width + i]);
                    //속 채우기

                    paint_block[j * game_width + i].setColor(random_block_out[j * game_width + i]);
                    paint_block[j * game_width + i].setStyle(Paint.Style.STROKE);
                    canvas.drawPath(path_block[j * game_width + i], paint_block[j * game_width + i]);
                    //외곽선

                    paint_blockline[j * game_width + i].setColor(Color.rgb(38, 21, 2));
                    paint_blockline[j * game_width + i].setStyle(Paint.Style.STROKE);
                    canvas.drawPath(path_blockline[j * game_width + i], paint_blockline[j * game_width + i]);
                }


            }
        }


        Paint paint=new Paint();
        paint.setAntiAlias(true);// 확대해도 선이 울퉁불퉁하지 않고 매끈하게 설정

        Paint paint2 = new Paint();
        paint.setAntiAlias(true);

        if(life > 400){
            paint.setColor(Color.BLUE);
        }
        else if(life > 200){
            paint.setColor(Color.YELLOW);
        }
        else{
            paint.setColor(Color.RED);
        }
        paint2.setColor(Color.GRAY);


        //여기 캐릭터를 그린다. 회전하는 캐릭터


        int w=digger[digger_mode].getWidth();
        int h=digger[digger_mode].getHeight();
        Rect dst = new Rect(350, 430, 350 + w*2, 430 + h*2);
        canvas.drawBitmap(digger[digger_mode],null,dst,null);




        if(life>0) {
            RectF rect4 = new RectF(50, 50, 50 + 800, 50 + 80);
            RectF rect3 = new RectF(50, 50, 50 + life, 50 + 80); //(시작X,시작Y,끝X,끝y)
            canvas.drawRoundRect(rect4, 20, 20, paint2);
            canvas.drawRoundRect(rect3, 20, 20, paint);
            //생명 칸
        }


        invalidate();


    }
}
//게임 내 시간을 흐르게 만드는 기능
class CustomTimer extends TimerTask{
    @Override
    public void run(){


    }

}



/*
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
 */
