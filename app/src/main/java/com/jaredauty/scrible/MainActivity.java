package com.jaredauty.scrible;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    public static final String PASSAGE_EXTRA = "passage_extra";
    public static final int PASSAGE_REQUEST_CODE = 1;

    private Button cleanButton;
    private Button debugButton;
    private Button lookupButton;
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

        lookupButton = (Button) findViewById(R.id.lookupButton);
        lookupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openPassageLookup();
            }
        });

        debugButton = (Button) findViewById(R.id.debugButton);
        debugButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainSurface.toggleDebug();
                mainSurface.repaint();
            }
        });
    }
    protected void openPassageLookup() {
        Log.i("info", "pressed the lookup button");
        Intent intent = new Intent(this, PassageLookupActivity.class);
        startActivityForResult(intent, PASSAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PASSAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String passage = data.getStringExtra(PASSAGE_EXTRA);
            mainSurface.setPassageText(passage);
            mainSurface.repaint();
        }
    }
}
