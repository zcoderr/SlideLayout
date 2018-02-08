package xyz.zhenhua.slidablelayout;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by zachary on 2018/2/8.
 */

public class SlidableLayout extends RelativeLayout {
    private RelativeLayout containerView;
    private VelocityTracker vt = null;
    private LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

    private float dX = 0f;  // x轴位移
    private float dY = 0f;  // y轴位移
    private float downX;    // 触摸点横坐标
    private float downY;    // 触摸点纵坐标
    private float moveX;
    private float moveY;

    private float STIFFNESS = 300f; // 弹性动画硬度
    private float DAMPING_RATIO = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY;    // 弹性动画阻尼
    private int maxSpeed = 700;
    private int maxDistance = 1000;
    private boolean isFlyOff = true;

    private SpringAnimation xAnimation; // x方向回弹动画
    private SpringAnimation yAnimation; // y方向回弹动画
    private SpringAnimation rAnimation; // 旋转回弹动画
    private SpringAnimation enterAnimation; // 入场回弹动画
    private SlideableListener slideableListener; // 监听

    public SlidableLayout(Context context) {
        super(context);
    }

    public SlidableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        containerView = new RelativeLayout(context);
    }

    // 设置弹性动画阻尼和硬度
    public void setStiffnessAndDampingatio(@FloatRange(from = 0.0) Float stiffness, @FloatRange(from = 0.0) Float dampingRatio) {
        this.STIFFNESS = stiffness;
        this.DAMPING_RATIO = dampingRatio;
    }

    // 设置触发关闭的滑动速度
    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    // 设置触发关闭的滑动距离
    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    // 设置控件消失效果为飞出还是直接消失
    public void isFlyOff(boolean isFlyOff) {
        this.isFlyOff = isFlyOff;
    }

    // 进行初始化工作
    public void init() {
        // 限制直接子view的数量为1
        if (getChildCount() != 1) {
            throw new RuntimeException("SlideableView must have and just one direct childview（SlideableView有且只能有一个直接子View)");
        }

        View childView = getChildAt(0);
        ViewGroup.LayoutParams childLp = childView.getLayoutParams();
        removeView(childView);
        containerView.addView(childView, childLp);

        addView(containerView, layoutParams);

        containerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                xAnimation = createTranslationSpringAnimation(containerView, SpringAnimation.X, containerView.getX(), STIFFNESS, DAMPING_RATIO);
                yAnimation = createTranslationSpringAnimation(containerView, SpringAnimation.Y, containerView.getY(), STIFFNESS, DAMPING_RATIO);
                rAnimation = createRoationSpringAnimation(containerView, SpringAnimation.ROTATION, containerView.getRotation(), STIFFNESS, DAMPING_RATIO);
                enterAnimation = createEntrancSpringAnimation(containerView, SpringAnimation.Y, containerView.getY(), STIFFNESS, DAMPING_RATIO);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    containerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    containerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                containerView.animate().y(5).setDuration(0).start();
                enterAnimation.start();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 触摸位置
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                double speed = Math.sqrt(Math.pow(vt.getXVelocity(), 2) + Math.pow(vt.getYVelocity(), 2));
                double distance = Math.sqrt(Math.pow(moveX, 2) + Math.pow(moveY, 2));

                // 如果滑动速度或滑动距离满足条件，则关闭动画
                if (speed > maxSpeed || distance > maxDistance) {
                    if (isFlyOff == true) {
                        flyOff();
                    } else {
                        containerView.animate().alpha(0).setDuration(0).start();
                    }

                    if (slideableListener != null) {
                        slideableListener.onDismiss();
                    }
                    break;
                }

                // 开始回弹动画
                xAnimation.start();
                yAnimation.start();
                rAnimation.start();

                if (slideableListener != null) {
                    slideableListener.onSpringStart();
                }

                break;
            case MotionEvent.ACTION_DOWN:
                xAnimation.cancel();
                yAnimation.cancel();
                rAnimation.cancel();

                dX = containerView.getX() - event.getRawX();
                dY = containerView.getY() - event.getRawY();

                downX = event.getRawX();
                downY = event.getRawY();

                // 初始化velocityTracker的对象 vt 用来监测motionevent的动作
                if (vt == null) {
                    vt = VelocityTracker.obtain();
                } else {
                    vt.clear();
                }
                vt.addMovement(event);

                if (slideableListener != null) {
                    slideableListener.onSlideStart();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                vt.addMovement(event);
                vt.computeCurrentVelocity(100);
                moveX = event.getRawX() - downX;
                moveY = event.getRawY() - downY;

                // 纵向跟手，横向旋转动画
                containerView.animate()
                        .rotation(moveX / 5)
                        .x(x + dX)
                        .y(y + dY)
                        .setDuration(0)
                        .start();
                break;
            default:
                break;
        }

        return true;
    }

    public void flyOff() {
        containerView.animate().x(moveX > 0 ? 1000 : -1000).y(moveY > 0 ? 1000 : -1000).rotation(moveX > 0 ? 400 : -400).setDuration(1000).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                containerView.animate().alpha(0).setDuration(0).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    // 创建纵向位移的回弹动画
    private SpringAnimation createTranslationSpringAnimation(View view,
                                                             DynamicAnimation.ViewProperty property,
                                                             Float finalPosition,
                                                             @FloatRange(from = 0.0) Float stiffness,
                                                             @FloatRange(from = 0.0) Float dampingRatio) {
        SpringAnimation animation = new SpringAnimation(view, property);
        SpringForce spring = new SpringForce(finalPosition);

        spring.setStiffness(stiffness);
        spring.setDampingRatio(dampingRatio);
        animation.setSpring(spring);

        return animation;
    }

    // 创建横向旋转的回弹动画
    private SpringAnimation createRoationSpringAnimation(View view,
                                                         DynamicAnimation.ViewProperty property,
                                                         Float finalPosition,
                                                         @FloatRange(from = 0.0) Float stiffness,
                                                         @FloatRange(from = 0.0) Float dampingRatio) {
        SpringAnimation animation = new SpringAnimation(view, property);
        SpringForce spring = new SpringForce(finalPosition);

        spring.setStiffness(stiffness);
        spring.setDampingRatio(dampingRatio);
        animation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                if (slideableListener != null) {
                    slideableListener.onSpringStop();
                }
            }
        });
        animation.setSpring(spring);

        return animation;
    }

    // 创建具有弹性效果的进入动画
    private SpringAnimation createEntrancSpringAnimation(View view,
                                                         DynamicAnimation.ViewProperty property,
                                                         Float finalPosition,
                                                         @FloatRange(from = 0.0) Float stiffness,
                                                         @FloatRange(from = 0.0) Float dampingRatio) {
        SpringAnimation animation = new SpringAnimation(view, property);
        SpringForce spring = new SpringForce(finalPosition);

        spring.setStiffness(stiffness);
        spring.setDampingRatio(dampingRatio);
        animation.setSpring(spring);

        return animation;
    }

    public void setSlideableListener(SlideableListener slideableListener) {
        this.slideableListener = slideableListener;
    }

    public interface SlideableListener {
        public void onSlideStart();

        public void onSpringStart();

        public void onSpringStop();

        public void onDismiss();
    }
}
