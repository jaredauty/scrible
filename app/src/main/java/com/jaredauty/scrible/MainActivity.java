package com.jaredauty.scrible;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button cleanButton;
    private MainSurface mainSurface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainSurface = (MainSurface) findViewById(R.id.surfaceView);

        cleanButton = (Button) findViewById(R.id.cleanButton);
        cleanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainSurface.clean();
                mainSurface.repaint();
            }
        });
    }
}
