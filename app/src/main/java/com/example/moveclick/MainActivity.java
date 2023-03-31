package com.example.moveclick;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private FrameLayout container;
    private ImageView joystick;
    private GestureDetector gestureDetector;
    private float centerX, centerY;
    private float lastX, lastY;
    private static final int RADIUS = 30;

    private OnJoystickMoveListener joystickMoveListener;
    private TextView tv_remove;

    private int directionCounter = 0;

    private Handler handler = new Handler(Looper.getMainLooper());
    private ImageView imageView1;
    private ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);
        joystick = findViewById(R.id.joystick);
        tv_remove = (TextView) findViewById(R.id.tv_remove);
        gestureDetector = new GestureDetector(this, this);


        generateRandomImageViews();




        ViewTreeObserver viewTreeObserver = container.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                centerX = container.getWidth() / 2f;
                centerY = container.getHeight() * 5 / 6f;
            }
        });





        joystickMoveListener = new OnJoystickMoveListener() {
            private float curTranslationX = 0f;
            private float curTranslationY = 0f;

            @Override
            public void onJoystickMove(String direction) {
                // 在这里实现接口方法，可以根据方向来做一些操作
                // 比如移动控件、播放动画、发送网络请求等
                // 这里只是简单地打印一下方向信息
                Log.d("Joystick", "Joystick direction: " + direction);

                if (directionCounter < 2) {
                    switch (direction) {
                        case "up":
                            curTranslationY -= 20;
                            break;
                        case "down":
                            curTranslationY += 20;
                            break;
                        case "left":
                            curTranslationX -= 20;
                            break;
                        case "right":
                            curTranslationX += 20;
                            break;
                        default:
                            break;
                    }
                    // 使用ObjectAnimator平滑移动tv_remove
                    ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(tv_remove, "translationX", curTranslationX);
                    ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(tv_remove, "translationY", curTranslationY);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(translationXAnimator, translationYAnimator);
                    animatorSet.setDuration(400);
                    animatorSet.start();

                    directionCounter++;
                    if (directionCounter == 1) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                directionCounter = 0;
                            }
                        }, 100);
                    }
                }
                /*resultLocation();*/

            }

            ;
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        lastX = e.getX();
        lastY = e.getY();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float deltaX = e2.getX() - centerX;
        float deltaY = e2.getY() - centerY;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        float angle = (float) Math.atan2(deltaY, deltaX);
        if (distance > RADIUS) {
            distance = RADIUS;
            deltaX = (float) (RADIUS * Math.cos(angle));
            deltaY = (float) (RADIUS * Math.sin(angle));
        }
        joystick.setX(centerX + deltaX - joystick.getWidth() / 2);
        joystick.setY(centerY + deltaY - joystick.getHeight() / 2);

        String direction = "";
        if (deltaY < -RADIUS/2) {
            direction = "up";
        } else if (deltaY > RADIUS/2) {
            direction = "down";
        } else if (deltaX < -RADIUS/2) {
            direction = "left";
        } else if (deltaX > RADIUS/2) {
            direction = "right";
        }

        joystickMoveListener.onJoystickMove(direction);


        resultLocation();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // Do nothing
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // Do nothing
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // Do nothing
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }



    public void generateRandomImageViews() {
        // 获取屏幕尺寸
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // 随机生成两个 ImageView
        imageView1 = new ImageView(this);
        imageView1.setImageResource(R.drawable.star);
        imageView2 = new ImageView(this);
        imageView2.setImageResource(R.drawable.star2);

        // 设置 ImageView 的布局参数，使其可以在屏幕上任意位置显示
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                150, 150);
        layoutParams.leftMargin = (int) (Math.random() * (screenWidth - 150));
        layoutParams.topMargin = (int) (Math.random() * (screenHeight - 150));
        imageView1.setLayoutParams(layoutParams);

        layoutParams = new FrameLayout.LayoutParams(
                150, 150);
        layoutParams.leftMargin = (int) (Math.random() * (screenWidth - 150));
        layoutParams.topMargin = (int) (Math.random() * (screenHeight - 150));
        imageView2.setLayoutParams(layoutParams);

        // 将 ImageView 添加到布局中
        FrameLayout container = findViewById(R.id.container);
        container.addView(imageView1);
        container.addView(imageView2);

        // 获取 ImageView 的位置
        int[] imageView1Location = new int[2];
        imageView1.getLocationOnScreen(imageView1Location);
        int[] imageView2Location = new int[2];
        imageView2.getLocationOnScreen(imageView2Location);
        int imageView1X = imageView1Location[0];
        int imageView1Y = imageView1Location[1];
        int imageView2X = imageView2Location[0];
        int imageView2Y = imageView2Location[1];

        // 输出位置信息
        Log.d("ImageView1", "X:" + imageView1X + ", Y:" + imageView1Y);
        Log.d("ImageView2", "X:" + imageView2X + ", Y:" + imageView2Y);


    }

    public void resultLocation(){


        float x_remove = tv_remove.getX();
        float y_remove = tv_remove.getY();


        float x_1 = imageView1.getX();
        float y_1 = imageView1.getY();
        float x_2 = imageView2.getX();
        float y_2 = imageView2.getY();



        float x_M1 = Math.abs(Math.abs(x_remove) - Math.abs(x_1));
        float y_M1 = Math.abs(Math.abs(y_remove) - Math.abs(y_1));

        float x_M2 = Math.abs(Math.abs(x_remove) - Math.abs(x_2));
        float y_M2 = Math.abs(Math.abs(x_remove) - Math.abs(y_2));

        System.out.println("kkkkkkkkkkkkkkkk"+" "+x_remove+"   "+y_remove+"   "+"abs："+x_M1+" "+y_M1+" startloction:   "+ x_1 +" "+y_1);


        if (x_M1<40&&y_M1<40){
            imageView1.setVisibility(View.GONE);
        }
        if (x_M2<20&&y_M2<20){
            imageView2.setVisibility(View.GONE);

        }

    }



}
