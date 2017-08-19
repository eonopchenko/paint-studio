package nz.ac.unitec.paintstudio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import java.util.ArrayList;

public class CustomView extends View implements View.OnTouchListener {

    private Path drawPath;
    private Paint drawPaint;

    private ArrayList<Path> paths = new ArrayList<>();
    private ArrayList<Path> undonePaths = new ArrayList<>();

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
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(0xFF660000);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        setOnTouchListener(this);
    }

    public void Undo() {
        int size = paths.size();
        if (size > 0) {
            undonePaths.add(paths.get(size - 1));
            paths.remove(size - 1);
            invalidate();
        }
    }

    public void Redo() {
        int size = undonePaths.size();
        if (size > 0) {
            paths.add(undonePaths.get(size - 1));
            undonePaths.remove(size - 1);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);

        for (Path p : paths) {
            canvas.drawPath(p, drawPaint);
        }
        canvas.drawPath(drawPath, drawPaint);
    }

    public void setPaths(ArrayList<Path> paths) {
        this.paths = paths;
    }

    public void setUndonePaths(ArrayList<Path> undonePaths) {
        this.undonePaths = undonePaths;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedPaths savedPaths = new SavedPaths(superState);
        savedPaths.paths = paths;
        savedPaths.undonePaths = undonePaths;
        return savedPaths;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedPaths savedPaths = (SavedPaths) state;
        super.onRestoreInstanceState(savedPaths.getSuperState());
        setPaths(savedPaths.paths);
        setUndonePaths(savedPaths.undonePaths);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            drawPath.moveTo(touchX, touchY);
            break;
        case MotionEvent.ACTION_MOVE:
            drawPath.lineTo(touchX, touchY);
            break;
        case MotionEvent.ACTION_UP:
            paths.add(new Path(drawPath));
            drawPath.reset();
            undonePaths.clear();
            break;
        default:
            return false;
        }

        invalidate();
        return true;
    }

    static class SavedPaths extends BaseSavedState {
        ArrayList<Path> paths;
        ArrayList<Path> undonePaths;

        SavedPaths(Parcelable superState) {
            super(superState);
        }

        private SavedPaths(Parcel in) {
            super(in);
            in.readList(paths, paths.getClass().getClassLoader());
            in.readList(undonePaths, undonePaths.getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeList(paths);
            out.writeList(undonePaths);
        }

        public static final Parcelable.Creator<SavedPaths> CREATOR
                = new Parcelable.Creator<SavedPaths>() {
            public SavedPaths createFromParcel(Parcel in) {
                return new SavedPaths(in);
            }
            public SavedPaths[] newArray(int size) {
                return new SavedPaths[size];
            }
        };
    }
}
