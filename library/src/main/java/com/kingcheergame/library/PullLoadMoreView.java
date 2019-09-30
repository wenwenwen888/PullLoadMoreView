package com.kingcheergame.library;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by won on 2019-09-26.
 * 下拉查看更多的自定义View
 */
public class PullLoadMoreView extends LinearLayout {

    private ViewGroup headLayout;//顶部view的子控件,由用户添加
    private Scroller mScroller;//滑动器
    private boolean mTouchEvent = false;//是否拦截点击事件
    private float scrollYValue = 0f;//手指Y轴滑动的距离
    private int subViewHeight = 0;//顶部view的height
    private NestedScrollView nestedScrollView;
    private double decayRatio = 0.5;  //阻尼系数
    private int mPaddingBottom = 0;//顶部view的paddingBottom
    private int mPaddingTop = 0;//顶部view的PaddingTop
    private float mLastY = 0f;//手指按下的y轴坐标值
    private float mLastX = 0f;//手指按下的y轴坐标值
    private VIewState stateView = VIewState.CLOSE;//顶部view的显示状态,默认是关闭状态
    private TouchStateMove stateMove = TouchStateMove.NORMAL;//手势滑动状态
    private DotView dotView;//小圆点
    private LinearLayout topLayout;//顶部view的父控件
    private int topBackGroundColor;//顶部view的背景颜色

    public PullLoadMoreView(Context context) {
        super(context);
    }

    public PullLoadMoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mScroller = new Scroller(getContext(), new DecelerateInterpolator());

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PullLoadMoreView);
        topBackGroundColor = a.getColor(R.styleable.PullLoadMoreView_top_background_color, Color.WHITE);
        a.recycle();

        setOrientation(VERTICAL);
    }

    /**
     * 头部view
     */
    public ViewGroup addHeadView(int resLayout) {
        headLayout = (ViewGroup) View.inflate(getContext(), resLayout, null);
        headLayout.setVisibility(INVISIBLE);
        buildView();
        return headLayout;
    }

    /**
     * 构建整个view布局
     */
    private void buildView() {
        //子布局第一个必须为NestedScrollView
        nestedScrollView = (NestedScrollView) getChildAt(0);

        dotView = new DotView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 50);
        topLayout = new LinearLayout(getContext());
        topLayout.setClickable(true);
        topLayout.setBackgroundColor(topBackGroundColor);
        topLayout.setOrientation(VERTICAL);
        topLayout.post(new Runnable() {
            @Override
            public void run() {
                subViewHeight = topLayout.getHeight();
                mPaddingTop = -subViewHeight;
                int paddingLeft = topLayout.getPaddingLeft();
                int paddingRight = topLayout.getPaddingRight();
                mPaddingBottom = topLayout.getPaddingTop();
                topLayout.setPadding(paddingLeft, mPaddingTop, paddingRight, mPaddingBottom);
            }
        });

        topLayout.addView(headLayout);
        topLayout.addView(dotView, layoutParams);
        addView(topLayout, 0);
    }

    /**
     * 处理触摸拦截事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN://按下
                mLastY = ev.getY();
                mLastX = ev.getX();
                mTouchEvent = false;
                break;
            case MotionEvent.ACTION_MOVE://滑动
                float flX = ev.getX() - mLastX;
                float flY = ev.getY() - mLastY;
                float abs = Math.abs(flY);
                int scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();//手指滑动阈值
                if (abs > scaledTouchSlop) {
                    mTouchEvent = nestedScrollView.getScrollY() == 0;
                    if (flY < 0 && stateView == VIewState.CLOSE) {
                        mTouchEvent = false;
                    }
                    if (stateView == VIewState.OPEN) {
                        if (Math.abs(flX) > abs) {
                            mTouchEvent = false;
                        }
                        if (mLastY < subViewHeight) {//顶部区域打开后不消费事件
                            mTouchEvent = false;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTouchEvent = false;//抬起
                break;
        }
        return mTouchEvent;
    }

    /**
     * 触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://按下
                mTouchEvent = true;
                break;
            case MotionEvent.ACTION_MOVE://滑动
                scrollYValue = (event.getY() - mLastY);
                float abs = Math.abs(scrollYValue);
                int scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (abs > scaledTouchSlop) {
                    mTouchEvent = true;
                    if (scrollYValue > 0) {
                        if (nestedScrollView.getScrollY() == 0) {
                            if (stateView == VIewState.CLOSE) {
                                if (mPaddingTop < 0) {//向下滑动但是头部空间没完全显示
                                    mPaddingTop = (int) (decayRatio * scrollYValue - subViewHeight);
                                    topLayout.setPadding(getPaddingLeft(), mPaddingTop, getPaddingRight(), mPaddingBottom);
                                    stateMove = TouchStateMove.DOWN_NO_OVER;
                                    dotView.setPercent(1 - ((float) mPaddingTop / (-subViewHeight)));
                                    if (mPaddingTop > -subViewHeight / 2) {
                                        showToDown(headLayout, (long) 200);
                                    }
                                    headLayout.setAlpha(1 - ((float) mPaddingTop / (-subViewHeight)));

                                } else if (mPaddingTop >= 0) {//头部空间没完全显示依然向下滑动
                                    mPaddingTop = (int) (0.5 * decayRatio * scrollYValue + 0.5 * (-subViewHeight));
                                    topLayout.setPadding(getPaddingLeft(), mPaddingTop, getPaddingRight(), mPaddingTop);
                                    stateMove = TouchStateMove.DOWN_OVER;
                                    dotView.setPercent(1 - ((float) mPaddingTop / (-subViewHeight)));
                                    headLayout.setAlpha(1 - ((float) mPaddingTop / (-subViewHeight)));
                                }
                            } else {
                                mPaddingTop = (int) (0.5 * decayRatio * scrollYValue);
                                topLayout.setPadding(getPaddingLeft(), mPaddingTop, getPaddingRight(), mPaddingTop);
                                stateMove = TouchStateMove.DOWN_OVER;
                            }
                        }
                    } else {
                        if (nestedScrollView.getScrollY() == 0) {
                            if (stateView == VIewState.CLOSE) {
                                mPaddingTop = -subViewHeight;
                            } else {
                                mPaddingTop = (int) (decayRatio * scrollYValue);
                                if (mPaddingTop <= -subViewHeight) {
                                    topLayout.setPadding(getPaddingLeft(), mPaddingTop, getPaddingRight(), mPaddingBottom);
                                    mPaddingTop = -subViewHeight;
                                    stateView = VIewState.CLOSE;
                                } else {
                                    topLayout.setPadding(getPaddingLeft(), mPaddingTop, getPaddingRight(), mPaddingBottom);
                                    stateMove = TouchStateMove.UP;
                                }
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL://抬起/取消
                if (mPaddingTop > -subViewHeight / 3 && mPaddingTop < 0 && stateMove == TouchStateMove.DOWN_NO_OVER) {
                    moveAnimation(-mPaddingTop, mPaddingTop);
                    stateView = VIewState.OPEN;
                    dotHideAnim();
                }
                if (mPaddingTop <= -subViewHeight / 3 && mPaddingTop < 0 && stateMove == TouchStateMove.DOWN_NO_OVER) {
                    moveAnimation(-mPaddingTop, subViewHeight);
                    stateView = VIewState.CLOSE;
                    headLayout.setVisibility(View.INVISIBLE);
                    dotView.setAlpha(1.0f);
                    dotView.setVisibility(View.VISIBLE);
                }
                if (stateMove == TouchStateMove.DOWN_OVER) {
                    moveAnimation(-mPaddingTop, mPaddingTop);
                    stateView = VIewState.OPEN;
                    dotHideAnim();
                }
                if (stateMove == TouchStateMove.UP) {
                    moveAnimation(-mPaddingTop, subViewHeight);
                    headLayout.setVisibility(View.INVISIBLE);
                    stateView = VIewState.CLOSE;
                    dotView.setAlpha(1.0f);
                    dotView.setVisibility(View.VISIBLE);
                }
                mTouchEvent = false;
                scrollYValue = 0f;
                mLastY = 0f;
                //返回监听回调
                if (listener != null) {
                    listener.onViewState(stateView);
                }
                break;
        }
        return mTouchEvent;
    }

    /**
     * view滚动回弹动画
     */
    private void moveAnimation(int startY, int y) {
        mScroller.startScroll(0, startY, 0, y, 400);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currY = mScroller.getCurrY();
            topLayout.setPadding(getPaddingLeft(), -currY, getPaddingRight(), mPaddingBottom);
        }
        invalidate();//刷新view
    }

    /**
     * 触摸状态
     * DOWN_NO_OVER 向下滑动但是没有超出view的height值
     * DOWN_OVER 向下滑动并且超出了height值
     * UP 向上滑动
     * NORMAL 无状态
     */
    enum TouchStateMove {
        DOWN_NO_OVER, DOWN_OVER, UP, NORMAL
    }

    /**
     * 顶部view的显示状态
     * CLOSE 顶部为关闭
     * OPEN 顶部为打开状态
     */
    public enum VIewState {
        CLOSE, OPEN
    }

    /**
     * 顶部view向下平移动画
     *
     * @param view
     * @param time 动画时间
     */
    private void showToDown(final View view, Long time) {
        if (view.getVisibility() != View.VISIBLE) {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "translationY", -30f, 0f);
            //渐变动画
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animator1, alphaAnimator);
            animatorSet.setDuration(time);
            animatorSet.setInterpolator(new LinearInterpolator());
            animatorSet.start();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    /**
     * 小圆点的隐藏动画
     */
    private void dotHideAnim() {
        ViewPropertyAnimator alpha = dotView.animate().alpha(0f);
        alpha.setDuration(400);
        alpha.start();
        dotView.setVisibility(View.GONE);
        topLayout.setAlpha(1f);
    }

    private ViewStateListener listener;

    public void setViewStateListener(ViewStateListener listener) {
        this.listener = listener;
    }

    public interface ViewStateListener {
        void onViewState(VIewState viewState);
    }

}
