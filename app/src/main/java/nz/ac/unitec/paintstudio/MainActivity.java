package nz.ac.unitec.paintstudio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private CustomView customView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customView = (CustomView)findViewById(R.id.custom);
    }

    public void undoClicked(View view) {
        customView.Undo();
    }

    public void redoClicked(View view) {
        customView.Redo();
    }

    public void toolBrushClicked(View view) {
        customView.SetTool(CustomView.Tool.BRUSH);
    }

    public void toolEllipseClicked(View view) {
        customView.SetTool(CustomView.Tool.ELLIPSE);
    }
}
