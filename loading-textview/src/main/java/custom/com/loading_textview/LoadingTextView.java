package custom.com.loading_textview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * JIngYuchun
 * 2018/10/10
 * 自定义带加载进度的view
 */
public class LoadingTextView extends View {
    private Paint circlePaint;
    private Paint pointPaint;
    private TextPaint textPaint;
    private int radius = 30;
    private float distanceRatio = 0;
    PathMeasure pathMeasure, pathMeasure2;
    Path path, path2;
    private int[] mColors;
    private boolean isGetResult = false;
    private String resultText = "";
    private int loadingCount = 0;
    private int alpah = 0;
    Timer timer;
    ValueAnimator valueAnimator;
    Rect bounds = new Rect();
    //自定义属性
    private int mStartColor;
    private int mEndColir;
    private int mMainColor;
    private int mLoadingStyle;
    private boolean isGradient = false;
    public LoadingTextView(Context context) {
        super(context);
        init();
    }

    public LoadingTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.LoadingTextView);
        //获取自定义属性值
        mStartColor = array.getColor(R.styleable.LoadingTextView_start_color,Color.RED);
        mEndColir = array.getColor(R.styleable.LoadingTextView_end_color,Color.BLUE);
        mMainColor = array.getColor(R.styleable.LoadingTextView_main_color,Color.GRAY);
        mLoadingStyle = array.getColor(R.styleable.LoadingTextView_loading_style,0);
        isGradient = array.getBoolean(R.styleable.LoadingTextView_loading_gradient,false);
        array.recycle();
        init();

    }

    public LoadingTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mColors = new int[5];
        mColors[0] = getResources().getColor(R.color.circular_gray);
        mColors[1] = getResources().getColor(R.color.circular_green);
        mColors[2] = getResources().getColor(R.color.circular_red);
        mColors[3] = getResources().getColor(R.color.circular_yellow);
        mColors[4] = getResources().getColor(R.color.circular_blue);
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);
        circlePaint.setStrokeWidth(5);
        circlePaint.setColor(mColors[0]);
        circlePaint.setAntiAlias(true);
        if(isGradient){
            SweepGradient lg = new SweepGradient(0, 0, mStartColor, mEndColir);
            circlePaint.setShader(lg);
        }
        pointPaint = new Paint();
        pointPaint.setStrokeWidth(3);
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(mColors[0]);
        pointPaint.setStyle(Paint.Style.STROKE);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(30);
        textPaint.setAlpha(10);

        path = new Path();
        path.addCircle(0, 0, radius, Path.Direction.CW);
        path2 = new Path();
        path2.addCircle(0, 0, radius - 10, Path.Direction.CCW);
        pathMeasure = new PathMeasure(path, false);
        pathMeasure2 = new PathMeasure(path2, false);

        alpah = 0;
        timer = new Timer();
        //loading 动画
        initLoadingAnimal();
    }

    //动画
    private void initLoadingAnimal() {
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                distanceRatio = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                alpah = 0;
                loadingCount = 0;
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {
                loadingCount = 0;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                if(isGradient){
                    int color_index = new Random().nextInt(mColors.length);
                    circlePaint.setColor(mColors[color_index]);
                    pointPaint.setColor(mColors[color_index]);
                }
            }
        });
        valueAnimator.setDuration(800);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);

        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);

        int width=70,height=70;//计算自定义View最终的宽和高
        if(widthMode==MeasureSpec.EXACTLY){
            //如果match_parent或者具体的值，直接赋值
            width=widthSize;
        }else if(widthMode==MeasureSpec.AT_MOST){
            width= 70;
        }
        //高度跟宽度处理方式一样
        if(heightMode==MeasureSpec.EXACTLY){
            height=widthSize;
        }else if(heightMode==MeasureSpec.AT_MOST){
            height=  width;
        }
        //保存测量宽度和测量高度
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        if (!isGetResult && loadingCount <= 3) {
            //loading
            startSegment(canvas);

        } else {
            if (valueAnimator.isRunning()) {
                alpah = 0;
                valueAnimator.cancel();
            }
            //文字淡出动画
            startFontAnimal();
            //写内容
            if ("".equals(resultText) || null == resultText) {
                //这里根于需求自己编 是空字符串还是数字0还是文字说明 随意
                resultText = "0";
            }
            textPaint.getTextBounds(resultText, 0, resultText.length(), bounds);
            //比实际宽度长一些 两边 为了好看
            //float textWidth = textPaint.measureText(resultText);
            //真正宽高 是 边框bounds对象中的width和height
            canvas.drawText(resultText, 0 - bounds.width() / 2, 0 + bounds.height() / 2, textPaint);
        }
    }

    public void setText(String str) {
        isGetResult = true;
        resultText = str;
        loadingCount = 0;
    }

    /**
     * 文字淡出动画
     */
    private void startFontAnimal() {
        loadingCount = 0;
        //文字淡出
        if (timer != null) {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    alpah += 10;
                    if (alpah > 255 && timer != null) {
                        alpah = 255;
                        timer.cancel();
                        timer.purge();
                        timer = null;
                    }
                    //0~255
                    textPaint.setAlpha(alpah);
                    postInvalidate();
                }
            }, 100, 100);
        }
    }

    //截取path实现loading效果
    private void startSegment(Canvas canvas) {
        if(mLoadingStyle == 0){
            Path dst1 = new Path();
            float stopD = pathMeasure.getLength() * distanceRatio;
            float startD = (float) (stopD - ((0.5 - Math.abs(distanceRatio - 0.5)) * pathMeasure
                    .getLength())); //当前截取的开始点
            pathMeasure.getSegment(startD, stopD, dst1, true);
            canvas.drawPath(dst1, circlePaint);
        }else if(mLoadingStyle == 1){
            Path dst1 = new Path();
            Path dst2 = new Path();
            float stopD = pathMeasure.getLength() * distanceRatio;
            float startD = (float) (stopD - ((0.5 - Math.abs(distanceRatio - 0.5)) * pathMeasure
                    .getLength())); //当前截取的开始点
            pathMeasure.getSegment(startD, stopD, dst1, true);
            pathMeasure2.getSegment(startD, stopD, dst2, true);
            canvas.drawPath(dst1, circlePaint);
            canvas.drawPath(dst2, circlePaint);
            canvas.drawCircle(0, 0, 4, pointPaint);
        }
    }
}
