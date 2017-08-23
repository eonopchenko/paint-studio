package nz.ac.unitec.paintstudio;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class CustomView extends View implements View.OnTouchListener {

    public enum Tool {
        BRUSH,
        ELLIPSE,
    }

    private enum ColorThreadState {
        IDLE,
        STARTED,
        ACTIVE,
        COMPLETED
    }

    ColorThreadState mColorThreadState = ColorThreadState.IDLE;

    /// Root sketch class
    private class Sketch {

        int mStrokeColor;
        int mStrokeWidth;

        int getStrokeColor() {
            return mStrokeColor;
        }

        void setStrokeColor(int strokeColor) {
            this.mStrokeColor = strokeColor;
        }

        int getStrokeWidth() {
            return mStrokeWidth;
        }
    }

    /// Path sketch class
    private class PathSketch extends Sketch {

        private Path mPath;

        PathSketch(int strokeColor, int strokeWidth) {
            mPath = new Path();
            this.mStrokeColor = strokeColor;
            this.mStrokeWidth = strokeWidth;
        }

        Path getPath() {
            return mPath;
        }
    }

    /// Ellipse sketch class
    private class EllipseSketch extends Sketch {

        private int mFillColor;
        private RectF mRectF;
        private float mCenterX, mCenterY;

        EllipseSketch(float centerX, float centerY, int strokeColor, int strokeWidth, int fillColor) {
            this.mRectF = new RectF();
            this.mCenterX = centerX;
            this.mCenterY = centerY;
            this.mStrokeColor = strokeColor;
            this.mStrokeWidth = strokeWidth;
            this.mFillColor = fillColor;
        }

        RectF getRectF() {
            return mRectF;
        }

        float getCenterX() {
            return mCenterX;
        }

        float getCenterY() {
            return mCenterY;
        }

        int getFillColor() {
            return mFillColor;
        }

        void setFillColor(int fillColor) {
            this.mFillColor = fillColor;
        }
    }

    public ArrayList<Integer> GetColorsList() {
        return colorsList;
    }

    private Paint mPaint;
    private Tool mTool = Tool.BRUSH;

    private PathSketch mCurrentPath;
    private EllipseSketch mCurrentEllipse;

    private ArrayList<Sketch> mSketchList = new ArrayList<>();
    private ArrayList<Sketch> mUndoneSketchList = new ArrayList<>();

    private int mStrokeWidth;
    private int mStrokeColor;
    private int mFillColor;
    private int mStrokeColorIdx;
    private int mFillColorIdx;
    private boolean mStrokeColorCycling;
    private boolean mFillColorCycling;
    private boolean mStrokeColorRand;
    private boolean mFillColorRand;

    ArrayList<Integer> colorsList;

    public CustomView(Context context) {
        super(context);
        init();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mStrokeWidth = 10;
        mStrokeColor = Color.RED;
        mFillColor = Color.YELLOW;
        mStrokeColorCycling = false;
        mFillColorCycling = false;
        mStrokeColorRand = false;
        mFillColorRand = false;

        mCurrentPath = new PathSketch(mStrokeColor, mStrokeWidth);
        mCurrentEllipse = new EllipseSketch(0, 0, mStrokeColor, mStrokeWidth, mFillColor);

        colorsList = new ArrayList<>();
        colorsList.add(getResources().getColor(R.color.colorRed));
        colorsList.add(getResources().getColor(R.color.colorOrange));
        colorsList.add(getResources().getColor(R.color.colorYellow));
        colorsList.add(getResources().getColor(R.color.colorGreen));
        colorsList.add(getResources().getColor(R.color.colorBlue));
        colorsList.add(getResources().getColor(R.color.colorIndigo));
        colorsList.add(getResources().getColor(R.color.colorViolet));

        mStrokeColorIdx = 0;
        mFillColorIdx = 2;

        setOnTouchListener(this);

        mColorThreadState = ColorThreadState.IDLE;

        final Handler h = new Handler();
        Thread t = new Thread(new Runnable() {
            public void run() {
                long time = 0;
                while(true) {
                    switch (mColorThreadState) {
                        case IDLE: {
                            break;
                        }

                        case STARTED: {
                            time = SystemClock.currentThreadTimeMillis();
                            mColorThreadState = ColorThreadState.ACTIVE;
                            break;
                        }

                        case ACTIVE: {
                            if (SystemClock.currentThreadTimeMillis() - time >= 1000) {
                                mColorThreadState = ColorThreadState.COMPLETED;
                            }
                            break;
                        }

                        case COMPLETED: {
                            h.post(updateColor);
                            mColorThreadState = ColorThreadState.STARTED;
                            break;
                        }
                    }
                }
            }
        });
        t.start();
    }


    ///--- INTERFACES ---///

    public void Undo() {

        int size = mSketchList.size();

        if (size > 0) {
            mUndoneSketchList.add(mSketchList.get(size - 1));
            mSketchList.remove(size - 1);
        }

        invalidate();
    }

    public void Redo() {

        int size = mUndoneSketchList.size();

        if (size > 0) {
            mSketchList.add(mUndoneSketchList.get(size - 1));
            mUndoneSketchList.remove(size - 1);
        }

        invalidate();
    }

    public void Clear() {

        for(Sketch sketch : mSketchList) {
            mUndoneSketchList.add(sketch);
        }

        mSketchList.clear();

        invalidate();
    }

    Runnable updateColor = new Runnable() {
        public void run() {
            if(mStrokeColorCycling) {
                if (++mStrokeColorIdx > colorsList.size() - 1) {
                    mStrokeColorIdx = 0;
                }
                mStrokeColor = colorsList.get(mStrokeColorIdx);
            }
            if(mFillColorCycling) {
                if (++mFillColorIdx > colorsList.size() - 1) {
                    mFillColorIdx = 0;
                }
                mFillColor = colorsList.get(mFillColorIdx);
            }
            if(mStrokeColorCycling || mFillColorCycling) {
                invalidate();
            }
        }
    };

    public void SetTool(Tool tool) {
        this.mTool = tool;
    }

    public void SetStrokeWidth(int strokeWidth) {
        this.mStrokeWidth = strokeWidth;
    }

    public void SetStrokeColor(int color) {
        mStrokeColor = color;
    }

    public void SetFillColor(int color) {
        mFillColor = color;
    }

    public void SetStrokeColorCycling(boolean state) {
        this.mStrokeColorCycling = state;
    }

    public void SetFillColorCycling(boolean state) {
        this.mFillColorCycling = state;
    }

    public void SetStrokeColorRand(boolean state) {
        this.mStrokeColorRand = state;
    }

    public void SetFillColorRand(boolean state) {
        this.mFillColorRand = state;
    }


    ///--- OVERRIDDEN METHODS ---///

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        for (Sketch s : mSketchList) {
            mPaint.setColor(s.getStrokeColor());
            mPaint.setStrokeWidth(s.getStrokeWidth());
            mPaint.setStyle(Paint.Style.STROKE);
            if (s instanceof PathSketch) {
                canvas.drawPath(((PathSketch) s).getPath(), mPaint);
            } else if (s instanceof EllipseSketch) {
                RectF rectF = ((EllipseSketch) s).getRectF();
                canvas.drawOval(rectF, mPaint);
                mPaint.setColor(((EllipseSketch) s).getFillColor());
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawOval(rectF, mPaint);
            }
        }

        mPaint.setColor(mStrokeColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        if (mTool == Tool.BRUSH) {
            if (mCurrentPath != null) {
                canvas.drawPath(mCurrentPath.getPath(), mPaint);
            }
        } else if (mTool == Tool.ELLIPSE) {
            if (mCurrentEllipse != null) {
                canvas.drawOval(mCurrentEllipse.getRectF(), mPaint);
                mPaint.setColor(mFillColor);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawOval(mCurrentEllipse.getRectF(), mPaint);
            }
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedContext savedContext = new SavedContext(superState);
        savedContext.sketches = mSketchList;
        return savedContext;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedContext savedContext = (SavedContext) state;
        super.onRestoreInstanceState(savedContext.getSuperState());
        this.mSketchList = savedContext.sketches;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mTool == Tool.BRUSH) {
                    mCurrentPath = new PathSketch(mStrokeColor, mStrokeWidth);
                    mCurrentPath.getPath().moveTo(touchX, touchY);
                } else if (mTool == Tool.ELLIPSE) {
                    mCurrentEllipse = new EllipseSketch(touchX, touchY, mStrokeColor, mStrokeWidth, mFillColor);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                mColorThreadState = ColorThreadState.STARTED;
                if (mStrokeColorRand) {
                    if (++mStrokeColorIdx > colorsList.size() - 1) {
                        mStrokeColorIdx = 0;
                    }
                    mStrokeColor = colorsList.get(mStrokeColorIdx);
                }

                if (mFillColorRand) {
                    if (++mFillColorIdx > colorsList.size() - 1) {
                        mFillColorIdx = 0;
                    }
                    mFillColor = colorsList.get(mFillColorIdx);
                }

                if (mTool == Tool.BRUSH) {
                    mCurrentPath.getPath().lineTo(touchX, touchY);
                } else if (mTool == Tool.ELLIPSE) {
                    float centerX = mCurrentEllipse.getCenterX();
                    float centerY = mCurrentEllipse.getCenterY();
                    float width = touchX - centerX;
                    float height = touchY - centerY;
                    width = width < 0 ? -width : width;
                    height = height < 0 ? -height : height;
                    mCurrentEllipse.getRectF().set(centerX - width, centerY - height, centerX + width, centerY + height);
                }
                break;

            case MotionEvent.ACTION_UP:
                mColorThreadState = ColorThreadState.IDLE;
                if (mTool == Tool.BRUSH) {
                    mCurrentPath.setStrokeColor(mStrokeColor);
                    mSketchList.add(mCurrentPath);
                    mCurrentPath = null;
                } else if (mTool == Tool.ELLIPSE) {
                    mCurrentEllipse.setStrokeColor(mStrokeColor);
                    mCurrentEllipse.setFillColor(mFillColor);
                    mSketchList.add(mCurrentEllipse);
                    mCurrentEllipse = null;
                }
                mUndoneSketchList.clear();
                break;

            default:
                return false;
        }

        invalidate();
        return true;
    }

    ///--- SAVE CONTEXT (STATIC) ---///
    private static class SavedContext extends BaseSavedState {

        ArrayList<Sketch> sketches;

        SavedContext(Parcelable superState) {
            super(superState);
        }

        private SavedContext(Parcel in) {
            super(in);
            in.readList(sketches, sketches.getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeList(sketches);
        }

        public static final Parcelable.Creator<SavedContext> CREATOR
                = new Parcelable.Creator<SavedContext>() {
            public SavedContext createFromParcel(Parcel in) {
                return new SavedContext(in);
            }
            public SavedContext[] newArray(int size) {
                return new SavedContext[size];
            }
        };
    }
}
