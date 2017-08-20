package nz.ac.unitec.paintstudio;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class CustomView extends View implements View.OnTouchListener {

    public enum Tool {
        BRUSH,
        ELLIPSE
    }

    private class Sketch {

        protected int mStrokeColor;

        protected int mStrokeWidth;

        public int getStrokeColor() {
            return mStrokeColor;
        }

        public int getStrokeWidth() {
            return mStrokeWidth;
        }
    }

    private class PathSketch extends Sketch {

        private Path mPath;

        PathSketch(int strokeColor, int strokeWidth) {
            mPath = new Path();
            this.mStrokeColor = strokeColor;
            this.mStrokeWidth = strokeWidth;
        }

        public Path getPath() {
            return mPath;
        }
    }

    private class EllipseSketch extends Sketch {

        private int mFillColor;
        private RectF mRectF;
        private float mCenterX, mCenterY;

        public int getFillColor() {
            return mFillColor;
        }

        EllipseSketch(float centerX, float centerY, int strokeColor, int strokeWidth, int fillColor) {
            this.mRectF = new RectF();
            this.mCenterX = centerX;
            this.mCenterY = centerY;
            this.mStrokeColor = strokeColor;
            this.mStrokeWidth = strokeWidth;
            this.mFillColor = fillColor;
        }

        public RectF getRectF() {
            return mRectF;
        }

        public float getCenterX() {
            return mCenterX;
        }

        public float getCenterY() {
            return mCenterY;
        }
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

        mCurrentPath = new PathSketch(mStrokeColor, mStrokeWidth);
        mCurrentEllipse = new EllipseSketch(0, 0, mStrokeColor, mStrokeWidth, mFillColor);

        setOnTouchListener(this);
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

    public void SetTool(Tool tool) {
        this.mTool = tool;
    }

    public void SetStrokeWidth(int strokeWidth) {
        this.mStrokeWidth = strokeWidth;
    }


    ///--- OVERRIDDEN METHODS ---///

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        for(Sketch s : mSketchList) {
            mPaint.setColor(s.getStrokeColor());
            mPaint.setStrokeWidth(s.getStrokeWidth());
            mPaint.setStyle(Paint.Style.STROKE);
            if (s instanceof PathSketch) {
                canvas.drawPath(((PathSketch) s).getPath(), mPaint);
            } else if(s instanceof EllipseSketch) {
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
        if(mTool == Tool.BRUSH) {
            if(mCurrentPath != null) {
                canvas.drawPath(mCurrentPath.getPath(), mPaint);
            }
        } else if(mTool == Tool.ELLIPSE) {
            if(mCurrentEllipse != null) {
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
                if(mTool == Tool.BRUSH) {
                    mCurrentPath = new PathSketch(mStrokeColor, mStrokeWidth);
                    mCurrentPath.getPath().moveTo(touchX, touchY);
                } else if (mTool == Tool.ELLIPSE) {
                    mCurrentEllipse = new EllipseSketch(touchX, touchY, mStrokeColor, mStrokeWidth, mFillColor);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(mTool == Tool.BRUSH) {
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
                if(mTool == Tool.BRUSH) {
                    mSketchList.add(mCurrentPath);
                    mCurrentPath = null;
                } else if (mTool == Tool.ELLIPSE) {
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
    static class SavedContext extends BaseSavedState {

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
