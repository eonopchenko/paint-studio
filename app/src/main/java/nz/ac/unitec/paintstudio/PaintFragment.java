package nz.ac.unitec.paintstudio;

import android.app.Fragment;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.savvisingh.colorpickerdialog.ColorPickerDialog;

import java.util.ArrayList;

/**
 * Created by eugene on 21/08/2017.
 */

public class PaintFragment extends Fragment {

    View mView;
    ArrayList<Integer> closestColorsList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_paint, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closestColorsList.add(getResources().getColor(R.color.colorRed));
        closestColorsList.add(getResources().getColor(R.color.colorOrange));
        closestColorsList.add(getResources().getColor(R.color.colorYellow));
        closestColorsList.add(getResources().getColor(R.color.colorGreen));
        closestColorsList.add(getResources().getColor(R.color.colorBlue));
        closestColorsList.add(getResources().getColor(R.color.colorIndigo));
        closestColorsList.add(getResources().getColor(R.color.colorViolet));

        final CustomView customView = (CustomView)mView.findViewById(R.id.customView);
        final ImageButton btnToolUndo = (ImageButton) mView.findViewById(R.id.btnToolUndo);
        final ImageButton btnToolRedo = (ImageButton) mView.findViewById(R.id.btnToolRedo);
        final ImageButton btnToolBrush = (ImageButton) mView.findViewById(R.id.btnToolBrush);
        final ImageButton btnToolEllipse = (ImageButton) mView.findViewById(R.id.btnToolEllipse);
        final ImageButton btnToolRectangle = (ImageButton) mView.findViewById(R.id.btnToolRectangle);
        final ImageButton btnToolStrokeColor = (ImageButton) mView.findViewById(R.id.btnToolStrokeColor);
        final ImageButton btnToolFillColor = (ImageButton) mView.findViewById(R.id.btnToolFillColor);
        btnToolUndo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        btnToolUndo.setBackgroundColor(getResources().getColor(R.color.colorSpindle));
                        break;
                    case MotionEvent.ACTION_UP:
                        btnToolUndo.setBackgroundColor(getResources().getColor(R.color.colorChambray));
                        customView.Undo();
                        break;
                }
                return true;
            }
        });
        btnToolRedo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        btnToolRedo.setBackgroundColor(getResources().getColor(R.color.colorSpindle));
                        break;
                    case MotionEvent.ACTION_UP:
                        btnToolRedo.setBackgroundColor(getResources().getColor(R.color.colorChambray));
                        customView.Redo();
                        break;
                }
                return true;
            }
        });
        btnToolBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnToolBrush.setBackgroundColor(getResources().getColor(R.color.colorSpindle));
                btnToolEllipse.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                btnToolRectangle.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                customView.SetTool(CustomView.Tool.BRUSH);
            }
        });
        btnToolEllipse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnToolEllipse.setBackgroundColor(getResources().getColor(R.color.colorSpindle));
                btnToolBrush.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                btnToolRectangle.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                customView.SetTool(CustomView.Tool.ELLIPSE);
            }
        });
        btnToolRectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnToolRectangle.setBackgroundColor(getResources().getColor(R.color.colorSpindle));
                btnToolBrush.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                btnToolEllipse.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                customView.SetTool(CustomView.Tool.RECTANGLE);
            }
        });
        btnToolStrokeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ColorPickerDialog dialog = ColorPickerDialog.newInstance(
                        ColorPickerDialog.SELECTION_SINGLE,
                        closestColorsList,
                        3,
                        ColorPickerDialog.SIZE_SMALL);

                dialog.setOnDialodButtonListener(new ColorPickerDialog.OnDialogButtonListener() {
                    @Override
                    public void onDonePressed(ArrayList<Integer> mSelectedColors) {
                        int color = mSelectedColors.get(0);
                        btnToolStrokeColor.setBackgroundColor(color);
                        customView.SetStrokeColor(color);
                    }

                    @Override
                    public void onDismiss() {

                    }
                });

                dialog.show(getFragmentManager(), "some_tag");

            }
        });
        btnToolFillColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ColorPickerDialog dialog = ColorPickerDialog.newInstance(
                        ColorPickerDialog.SELECTION_SINGLE,
                        closestColorsList,
                        3,
                        ColorPickerDialog.SIZE_SMALL);

                dialog.setOnDialodButtonListener(new ColorPickerDialog.OnDialogButtonListener() {
                    @Override
                    public void onDonePressed(ArrayList<Integer> mSelectedColors) {
                        int color = mSelectedColors.get(0);
                        btnToolFillColor.setBackgroundColor(color);
                        customView.SetFillColor(color);
                    }

                    @Override
                    public void onDismiss() {

                    }
                });

                dialog.show(getFragmentManager(), "some_tag");
            }
        });
    }
}
