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
import java.util.List;
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

        // Start at the very beginning
        loadVerses("Genesis", 1);
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
            loadVerses(book, chapter);
        }
    }

    protected void loadVerses(String book, int chapter) {
        // Start off the asychronous verse loads.
        m_surfaceFragment.cleanVerses();
        m_surfaceFragment.setPassage(book, chapter);
        // Make sure any previous loads are cancelled.
        for(LoadVerseTask loadTask: m_verseLoadTasks) {
            loadTask.cancel(true);
        }
        m_verseLoadTasks.clear();
        // We chunk the verses so that the main ui doesn't get caught up processing all
        // the results.
        ArrayList<Integer> verses = m_bible.getVerses(book, chapter);
        int maxChunkSize = 2;
        int currentChunkSize = 0;
        ArrayList<BibleReference> references = new ArrayList<BibleReference>();
        for(Integer verseNumber: verses) {
            BibleReference reference = new BibleReference(book, chapter, verseNumber);
            references.add(reference);
            if(currentChunkSize >= maxChunkSize) {
                LoadVerseTask currentTask = new LoadVerseTask(m_bible, m_surfaceFragment);
                m_verseLoadTasks.add(currentTask);
                currentTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, references);
                references = new ArrayList<BibleReference>();
                currentChunkSize = 0;
                // Increase the chunk size for ones lower down since we don't see them immediately.
                maxChunkSize *=4;
            } else {
                currentChunkSize++;
            }
        }
    }
}

class LoadVerseTask extends AsyncTask<Object, Void, ArrayList<String> > {
    protected Bible m_bible;
    protected SurfaceFragment m_fragment;
    public LoadVerseTask(Bible bible, SurfaceFragment fragment) {
        super();
        m_bible = bible;
        m_fragment = fragment;
    }
    @Override
    protected ArrayList<String> doInBackground(Object... params) {
        List<BibleReference> bibleReferences = (List<BibleReference>) params[0];
        ArrayList<String> verses = new ArrayList<String>();
        for(BibleReference reference: bibleReferences) {
            verses.add(m_bible.getVerse(reference));
        }
        return verses;
    }

    protected void onPostExecute(ArrayList<String> result) {
        m_fragment.addVerses(result);
    }
}
