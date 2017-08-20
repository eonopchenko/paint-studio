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

    private class SketchSettings {

        private int mStrokeColor;
        private int mFillColor;

        SketchSettings(int strokeColor, int fillColor) {
            mStrokeColor = strokeColor;
            mFillColor = fillColor;
        }

        SketchSettings(SketchSettings sketchSettings) {
            this.mStrokeColor = sketchSettings.getStrokeColor();
            this.mFillColor = sketchSettings.getFillColor();
        }

        public int getStrokeColor() {
            return mStrokeColor;
        }

        public void setStrokeColor(int strokeColor) {
            mStrokeColor = strokeColor;
        }

        public int getFillColor() {
            return mFillColor;
        }

        public void setFillColor(int fillColor) {
            mFillColor = fillColor;
        }
    }

    private Paint mDrawPaint;
    private SketchSettings mSketchSettings;
    private Path mDrawPath;
    private RectF mDrawOval;
    private Tool mTool = Tool.BRUSH;
    private float mCenterX, mCenterY;

    private ArrayList<Object> mSketchList = new ArrayList<>();
    private ArrayList<Object> mUndoneSketchList = new ArrayList<>();
    private ArrayList<SketchSettings> mSketchSettingsList = new ArrayList<>();
    private ArrayList<SketchSettings> mUndoneSketchSettingsList = new ArrayList<>();

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init() {

        mDrawPath = new Path();
        mDrawOval = new RectF();

        mDrawPaint = new Paint();

        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(20);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

        mSketchSettings = new SketchSettings(Color.YELLOW, Color.GREEN);

        setOnTouchListener(this);
    }

    public void Undo() {

        int pathsSize = mSketchList.size();
        int colorsSize = mSketchSettingsList.size();

        if (pathsSize > 0) {
            mUndoneSketchList.add(mSketchList.get(pathsSize - 1));
            mSketchList.remove(pathsSize - 1);
        }

        if (colorsSize > 0) {
            mUndoneSketchSettingsList.add(mSketchSettingsList.get(colorsSize - 1));
            mSketchSettingsList.remove(colorsSize - 1);
        }

        invalidate();
    }

    public void Redo() {

        int pathsSize = mUndoneSketchList.size();
        int colorsSize = mUndoneSketchSettingsList.size();

        if (pathsSize > 0) {
            mSketchList.add(mUndoneSketchList.get(pathsSize - 1));
            mUndoneSketchList.remove(pathsSize - 1);
        }

        if(colorsSize > 0) {
            mSketchSettingsList.add(mUndoneSketchSettingsList.get(colorsSize - 1));
            mUndoneSketchSettingsList.remove(colorsSize - 1);
        }

        invalidate();
    }

    public void SetTool(Tool tool) {
        this.mTool = tool;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int index = 0; index < mSketchList.size(); index++) {

            Object o = mSketchList.get(index);
            SketchSettings sketchSettings = mSketchSettingsList.get(index);
            mDrawPaint.setColor(sketchSettings.getStrokeColor());
            mDrawPaint.setStyle(Paint.Style.STROKE);

            /// Path
            if(o instanceof Path) {
                canvas.drawPath((Path) o, mDrawPaint);

            /// Ellipse
            } else if(o instanceof RectF) {
                canvas.drawOval((RectF) o, mDrawPaint);
                mDrawPaint.setColor(sketchSettings.getFillColor());
                mDrawPaint.setStyle(Paint.Style.FILL);
                canvas.drawOval((RectF) o, mDrawPaint);
            }
        }

        mDrawPaint.setColor(mSketchSettings.getStrokeColor());
        mDrawPaint.setStyle(Paint.Style.STROKE);
        if(mTool == Tool.BRUSH) {
            canvas.drawPath(mDrawPath, mDrawPaint);
        } else if(mTool == Tool.ELLIPSE) {
            canvas.drawOval(mDrawOval, mDrawPaint);
            mDrawPaint.setColor(mSketchSettings.getFillColor());
            mDrawPaint.setStyle(Paint.Style.FILL);
            canvas.drawOval(mDrawOval, mDrawPaint);
        }
    }

    public void setSketchStack(ArrayList<Object> mSketchStack) {
        this.mSketchList = mSketchStack;
    }

    public void setUndoneSketchStack(ArrayList<Object> mUndoneSketchStack) {
        this.mUndoneSketchList = mUndoneSketchStack;
    }

    public void setColorStack(ArrayList<SketchSettings> mColorStack) {
        this.mSketchSettingsList = mColorStack;
    }

    public void setUndoneColorStack(ArrayList<SketchSettings> mUndoneColorStack) {
        this.mUndoneSketchSettingsList = mUndoneColorStack;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedContext savedContext = new SavedContext(superState);

        savedContext.sketches = mSketchList;
        savedContext.undoneSketches = mUndoneSketchList;
        savedContext.sketchSettings = mSketchSettingsList;
        savedContext.undoneSketchSettings = mUndoneSketchSettingsList;

        return savedContext;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        SavedContext savedContext = (SavedContext) state;
        super.onRestoreInstanceState(savedContext.getSuperState());

        setSketchStack(savedContext.sketches);
        setUndoneSketchStack(savedContext.undoneSketches);
        setColorStack(savedContext.sketchSettings);
        setUndoneColorStack(savedContext.undoneSketchSettings);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(mTool == Tool.BRUSH) {
                    mDrawPath.moveTo(touchX, touchY);
                } else if (mTool == Tool.ELLIPSE) {
                    mCenterX = touchX;
                    mCenterY = touchY;
                    mDrawOval.set(mCenterX, mCenterY, mCenterX, mCenterY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(mTool == Tool.BRUSH) {
                    mDrawPath.lineTo(touchX, touchY);
                } else if (mTool == Tool.ELLIPSE) {
                    float width = touchX - mCenterX;
                    float height = touchY - mCenterY;
                    width = width < 0 ? -width : width;
                    height = height < 0 ? -height : height;
                    mDrawOval.set(mCenterX - width, mCenterY - height, mCenterX + width, mCenterY + height);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mTool == Tool.BRUSH) {
                    mSketchList.add(new Path(mDrawPath));
                    mDrawPath.reset();
                } else if (mTool == Tool.ELLIPSE) {
                    mSketchList.add(new RectF(mDrawOval));
                    mDrawOval.set(0, 0, 0, 0);
                }
                mSketchSettingsList.add(new SketchSettings(mSketchSettings));
                mUndoneSketchList.clear();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    static class SavedContext extends BaseSavedState {
        ArrayList<Object> sketches;
        ArrayList<Object> undoneSketches;
        ArrayList<SketchSettings> sketchSettings;
        ArrayList<SketchSettings> undoneSketchSettings;

        SavedContext(Parcelable superState) {
            super(superState);
        }

        private SavedContext(Parcel in) {
            super(in);
            in.readList(sketches, sketches.getClass().getClassLoader());
            in.readList(undoneSketches, undoneSketches.getClass().getClassLoader());
            in.readList(sketchSettings, sketchSettings.getClass().getClassLoader());
            in.readList(undoneSketchSettings, undoneSketchSettings.getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeList(sketches);
            out.writeList(undoneSketches);
            out.writeList(sketchSettings);
            out.writeList(undoneSketchSettings);
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
