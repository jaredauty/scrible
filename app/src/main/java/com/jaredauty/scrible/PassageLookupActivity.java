package com.jaredauty.scrible;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.jaredauty.scrible.bible.Bible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jared on 06/08/2016.
 */
public class PassageLookupActivity extends Activity{

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<Integer>> listDataChild;
    private String selectedBook;
    private Integer selectedChapter;
    private Bible m_bible;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passage_lookup);
        m_bible = new Bible("ESV", getApplicationContext());

        expListView = (ExpandableListView) findViewById(R.id.expandableListView);

        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition).toString(), Toast.LENGTH_SHORT).show();
                selectedBook = listDataHeader.get(groupPosition);
                selectedChapter = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                doneButton();
                return false;
            }
        });

    }
    protected void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Integer>>();
        for (String bookName: m_bible.getBookNames()) {
            listDataHeader.add(bookName);
            listDataChild.put(bookName, m_bible.getChapterNumbers(bookName));
        }
    }

    protected void doneButton () {
        Intent output = new Intent();
        output.putExtra(MainActivity.BOOK_EXTRA, selectedBook);
        output.putExtra(MainActivity.CHAPTER_EXTRA, selectedChapter);
        setResult(RESULT_OK, output);
        finish();
    }

}
