package xyz.zhenhua.slideableview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import xyz.zhenhua.slidablelayout.SlidableLayout;

public class MainActivity extends AppCompatActivity {
    private SlidableLayout slidableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slidableLayout = findViewById(R.id.slideablelayout);
        slidableLayout.init();

        slidableLayout.setSlideableListener(new SlidableLayout.SlideableListener() {
            @Override
            public void onSlideStart() {
                Log.d("SlideableView", "开始滑动");
            }

            @Override
            public void onSpringStart() {
                Log.d("SlideableView", "开始回弹");
            }

            @Override
            public void onSpringStop() {
                Log.d("SlideableView", "回弹结束");
            }

            @Override
            public void onDismiss() {
                Log.d("SlideableView", "消失");
            }
        });
    }

}
