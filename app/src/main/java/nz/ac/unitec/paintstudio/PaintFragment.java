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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.savvisingh.colorpickerdialog.ColorPickerDialog;

import java.util.ArrayList;

/**
 * Created by eugene on 21/08/2017.
 */

public class PaintFragment extends Fragment implements ColorChangedEventListener {

    View mView;
    ImageButton btnToolStrokeColor;
    ImageButton btnToolFillColor;
    ArrayList<Integer> closestColorsList;

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

        final CustomView customView = (CustomView)mView.findViewById(R.id.customView);
        closestColorsList = customView.GetColorsList();
        customView.setColorChangedEventListener(this);

        final ImageButton btnToolUndo = (ImageButton) mView.findViewById(R.id.btnToolUndo);
        final ImageButton btnToolRedo = (ImageButton) mView.findViewById(R.id.btnToolRedo);
        final ImageButton btnToolClear = (ImageButton) mView.findViewById(R.id.btnToolClear);
        final ImageButton btnToolBrush = (ImageButton) mView.findViewById(R.id.btnToolBrush);
        final ImageButton btnToolEllipse = (ImageButton) mView.findViewById(R.id.btnToolEllipse);
        btnToolStrokeColor = (ImageButton) mView.findViewById(R.id.btnToolStrokeColor);
        btnToolFillColor = (ImageButton) mView.findViewById(R.id.btnToolFillColor);
        final Switch swStrokeColorCycling = (Switch) mView.findViewById(R.id.swStrokeColorCycling);
        final Switch swFillColorCycling = (Switch) mView.findViewById(R.id.swFillColorCycling);
        final Switch swStrokeColorRand = (Switch) mView.findViewById(R.id.swStrokeColorRand);
        final Switch swFillColorRand = (Switch) mView.findViewById(R.id.swFillColorRand);

        btnToolUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customView.Undo();
            }
        });
        btnToolRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customView.Redo();
            }
        });
        btnToolClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customView.Clear();
            }
        });
        btnToolBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnToolBrush.setBackgroundColor(getResources().getColor(R.color.colorSpindle));
                btnToolEllipse.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                customView.SetTool(CustomView.Tool.BRUSH);
            }
        });
        btnToolEllipse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnToolEllipse.setBackgroundColor(getResources().getColor(R.color.colorSpindle));
                btnToolBrush.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                customView.SetTool(CustomView.Tool.ELLIPSE);
            }
        });
        SeekBar sbStrokeWidth = (SeekBar) mView.findViewById(R.id.sbStrokeWidth);
        sbStrokeWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView) mView.findViewById(R.id.tvStrokeWidth)).setText(progress + "");
                customView.SetStrokeWidth(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

                dialog.show(getFragmentManager(), "tag");
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

                dialog.show(getFragmentManager(), "tag");
            }
        });
        swStrokeColorCycling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                customView.SetStrokeColorCycling(isChecked);
            }
        });
        swFillColorCycling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                customView.SetFillColorCycling(isChecked);
            }
        });
        swStrokeColorRand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                customView.SetStrokeColorRand(isChecked);
            }
        });
        swFillColorRand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                customView.SetFillColorRand(isChecked);
            }
        });
    }

    @Override
    public void onColorChanged(int strokeColor, int fillColor) {
        btnToolStrokeColor.setBackgroundColor(strokeColor);
        btnToolFillColor.setBackgroundColor(fillColor);
    }
}
