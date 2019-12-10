
package com.example.paradyss_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //Using the Accelometer & Gyroscoper
    private SensorManager mSensorManager = null;

    //Using the Accelometer
    private SensorEventListener mAccLis;
    private Sensor mAccelometerSensor = null;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Using the Gyroscope & Accelometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Using the Accelometer
        //mAccelometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccelometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mAccLis = new AccelometerListener();

        //Touch Listener for Accelometer
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

        //블록 생산 30행과 50열
        //이게 넘어가면

        //


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
    private class AccelometerListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            double q0 = event.values[3];
            double q1 = event.values[0];
            double q2 = event.values[1];
            double q3 = event.values[2];

            double Xval = 2 * ((q1*q3)-(q0*q2));
            double Yval = 2 * ((q2*q3)+(q0*q1));
            double Zval = ((q0*q0)-(q1*q1)-(q2*q2)+(q3*q3));

            Log.e("LOG", "ACCELOMETER           [q0]:" + String.format("%.4f", q0)
                    + "           [q1]:" + String.format("%.4f", q1)
                    + "           [q2]:" + String.format("%.4f", q2)
                    + "           [q3]:" + String.format("%.4f", q3)
                    + "           [Xval]:" + String.format("%.4f", Xval)
                    + "           [Yval]:" + String.format("%.4f", Yval)
                    + "           [Zval]:" + String.format("%.4f", Zval) );

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
