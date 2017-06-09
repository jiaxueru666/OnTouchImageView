package com.example.administrator.ontouchimageview;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {

    private ImageView mImage;
//创建两个矩阵；
    Matrix matrix=new Matrix();
    Matrix savedMatrix=new Matrix();

    PointF start=new PointF();
    PointF mid=new PointF();
    float oldDist;
    //模式
    static final int NONE=0;
    static final int DRAG=1;
    static final int ZOOM=2;
    int mode=NONE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mImage = (ImageView) findViewById(R.id.image);
        mImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ImageView image = (ImageView) view;
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    //设置拖拉模式
                    case MotionEvent.ACTION_DOWN:
                        matrix.set(image.getImageMatrix());
                        savedMatrix.set(matrix);
                        start.set(motionEvent.getX(), motionEvent.getY());
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;

                    //设置多点触摸模式
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(motionEvent);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, motionEvent);
                            mode = ZOOM;
                        }
                        break;
                    //若为DRAG模式，则点击移动图片
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(motionEvent.getX() - start.x, motionEvent.getY() - start.y);
                        }
                        //若为ZOOM模式，则点击触摸缩放
                        else if (mode == ZOOM) {
                            float newDist = spacing(motionEvent);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oldDist;
                                //设置硕放比例和图片的中点位置
                                matrix.postScale(scale, scale, mid.x, mid.y);
                            }
                        }
                        break;
                }
               image.setImageMatrix(matrix);
                return true;
            }
        });
    }
        //计算移动距离
        private float spacing(MotionEvent event){
            float x=event.getX(0)-event.getX(1);
            float y=event.getY(0)-event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }
        //计算中点位置
        private void midPoint(PointF point,MotionEvent event){
            float x=event.getX(0)+event.getX(1);
            float y=event.getY(0)+event.getY(1);
            point.set(x/2,y/2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
     //MotionEvent.ACTION_MASK支持多点触控；
        switch (event.getAction()&MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                Toast.makeText(MainActivity.this, event.getX()+"", Toast.LENGTH_SHORT).show();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onTouchEvent(event);
    }
}
