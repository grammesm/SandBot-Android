package com.alwaystinkering.sandbot.ui.sandbot;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alwaystinkering.sandbot.R;
import com.alwaystinkering.sandbot.model.state.SandBotStateManager;
import com.alwaystinkering.sandbot.ui.pattern.PatternEditActivity;
import com.alwaystinkering.sandbot.ui.pattern.PatternListAdapter;

import java.util.ArrayList;

public class PatternFragment extends SandBotTab {

    private static final String TAG = "BotFragment";

    private ListView patternList;
    private FloatingActionButton newPattern;

    public PatternFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pattern, container, false);
        patternList = rootView.findViewById(R.id.sandPatternList);
        newPattern = rootView.findViewById(R.id.newPattern);
        newPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(getActivity(), PatternEditActivity.class);
                getActivity().startActivity(editIntent);

            }
        });
        refresh();
        return rootView;
    }

    @Override
    void refresh() {
        PatternListAdapter patternListAdapter =
                new PatternListAdapter((MainActivity)getActivity(), 0, new ArrayList<>(SandBotStateManager.getSandBotSettings().getPatterns().values()));
        patternList.setAdapter(patternListAdapter);
    }

    @Override
    void enable() {
        newPattern.setEnabled(true);
    }

    @Override
    void disable() {
        newPattern.setEnabled(false);
        patternList.setAdapter(null);
    }
}
