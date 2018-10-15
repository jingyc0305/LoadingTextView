package custom.com.loading_textview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
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
 * @Author: JIngYuchun
 * @Date: 2018/10/10
 * @Description: 自定义带加载进度的view
 */
public class LoadingTextView extends View {
    private Paint circlePaint;
    private Paint pointPaint;
    private TextPaint textPaint;
    private int radius = 50;
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

    public LoadingTextView(Context context) {
        super(context);
        init();
    }

    public LoadingTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public LoadingTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mColors = new int[4];
        mColors[0] = getResources().getColor(R.color.circular_blue);
        mColors[1] = getResources().getColor(R.color.circular_green);
        mColors[2] = getResources().getColor(R.color.circular_red);
        mColors[3] = getResources().getColor(R.color.circular_yellow);

        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);
        circlePaint.setStrokeWidth(5);
        circlePaint.setAntiAlias(true);
        SweepGradient lg = new SweepGradient(0, 0, Color.RED, Color.BLUE);
        circlePaint.setShader(lg);

        pointPaint = new Paint();
        pointPaint.setStrokeWidth(3);
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.STROKE);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(30);
        textPaint.setAlpha(10);

        path = new Path();
        path.addCircle(0, 0, radius, Path.Direction.CW);
        path2 = new Path();
        path2.addCircle(0, 0, radius - 30, Path.Direction.CCW);
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
                int color_index = new Random().nextInt(mColors.length);
                circlePaint.setColor(mColors[color_index]);
                pointPaint.setColor(mColors[color_index]);
            }
        });
        valueAnimator.setDuration(800);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();
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
        Path dst1 = new Path();
        Path dst2 = new Path();
        float stopD = pathMeasure.getLength() * distanceRatio;
        float startD = (float) (stopD - ((0.5 - Math.abs(distanceRatio - 0.5)) * pathMeasure
                .getLength())); //当前截取的开始点
        pathMeasure.getSegment(startD, stopD, dst1, true);
        pathMeasure2.getSegment(startD, stopD, dst2, true);
        canvas.drawPath(dst1, circlePaint);
        canvas.drawPath(dst2, circlePaint);
        canvas.drawCircle(0, 0, 8, pointPaint);
    }
}
