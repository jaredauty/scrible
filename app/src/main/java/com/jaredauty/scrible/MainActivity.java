package com.jaredauty.scrible;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jaredauty.scrible.bible.Bible;
import com.jaredauty.scrible.bible.BibleReference;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class MainActivity extends Activity {

    public static final String BOOK_EXTRA = "book_extra";
    public static final String CHAPTER_EXTRA = "chapter_extra";
    public static final int PASSAGE_REQUEST_CODE = 1;

    private Button cleanButton;
    private Button debugButton;
    private Button lookupButton;
    private Bible m_bible;
    private ArrayList<LoadVerseTask> m_verseLoadTasks;
    //private MainSurface mainSurface;
    private SurfaceFragment m_surfaceFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_bible = new Bible("ESV", getApplicationContext());
        m_verseLoadTasks = new ArrayList<LoadVerseTask>();
        m_surfaceFragment = (SurfaceFragment) getFragmentManager().findFragmentById(R.id.surfaceFragment);

        cleanButton = (Button) findViewById(R.id.cleanButton);
        cleanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_surfaceFragment.clean();
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
                m_surfaceFragment.toggleDebug();
            }
        });
    }
    protected void openPassageLookup() {
        Log.i("info", "pressed the lookup button");
        Intent intent = new Intent(this, PassageLookupActivity.class);
        // TODO figure out a way to send the bible object we're using over to the lookup activity.
        startActivityForResult(intent, PASSAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PASSAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String book = data.getStringExtra(BOOK_EXTRA);
            int chapter = data.getIntExtra(CHAPTER_EXTRA, 1);
            m_surfaceFragment.setPassage(book, chapter);
            loadVerses(book, chapter);
        }
    }

    protected void loadVerses(String book, int chapter) {
        // Start off the asychronous verse loads.
        m_surfaceFragment.cleanVerses();
        // Make sure any previous loads are cancelled.
        for(LoadVerseTask loadTask: m_verseLoadTasks) {
            loadTask.cancel(true);
        }
        for(Integer verseNumber: m_bible.getVerses(book, chapter)) {
            BibleReference reference = new BibleReference(book, chapter, verseNumber);
            LoadVerseTask loadVerseTask = new LoadVerseTask(m_bible, m_surfaceFragment);
            m_verseLoadTasks.add(loadVerseTask);
            loadVerseTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, reference);
        }
    }
}

class LoadVerseTask extends AsyncTask<BibleReference, Void, String > {
    protected Bible m_bible;
    protected SurfaceFragment m_fragment;
    public LoadVerseTask(Bible bible, SurfaceFragment fragment) {
        super();
        m_bible = bible;
        m_fragment = fragment;
    }
    @Override
    protected String doInBackground(BibleReference... bibleReferences) {
        return m_bible.getVerse(bibleReferences[0]);
    }

    protected void onPostExecute(String result) {
        m_fragment.addVerse(result);
    }
}
