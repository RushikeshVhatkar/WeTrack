package com.rushikeshsantoshv.wetrack.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.rushikeshsantoshv.wetrack.R;

public class StickyNotesFragment extends Fragment {

    ActionBar actionBar;
    TextView title;

    public StickyNotesFragment(ActionBar actionBar, TextView title) {
        this.actionBar= actionBar;
        this.title= title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_sticky_notes, container, false);

        actionBar.setTitle("Sticky Notes");
        title.setText("Sticky Notes");
        return v;
    }
}