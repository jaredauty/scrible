package com.jaredauty.scrible;

import android.app.Fragment;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jared on 8/13/2016.
 */
public class SurfaceFragment extends Fragment {
    protected MainSurface m_surface;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView (LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState)
    {
        View mainView = inflater.inflate(R.layout.fragment_surface, container, false);
        m_surface = (MainSurface) mainView.findViewById(R.id.mainSurface);
        return mainView;
    }
    public void toggleDebug(){
        m_surface.toggleDebug();
        m_surface.repaint();
    }

    public void clean() {
        m_surface.clean();
        m_surface.repaint();
    }

    public void setPassage(String book, int chapterNum) {
        m_surface.setPassage(book, chapterNum);
        m_surface.repaint();
    }

    public void addVerses(List<String> verses) {
        m_surface.addVerses(verses);
        m_surface.repaint();
    }
    public void cleanVerses() {
        m_surface.cleanVerses();
        m_surface.repaint();
    }
}
