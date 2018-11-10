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
import com.alwaystinkering.sandbot.ui.sequence.SequenceEditActivity;
import com.alwaystinkering.sandbot.ui.sequence.SequenceListAdapter;

import java.util.ArrayList;

public class SequenceFragment extends SandBotTab {

    private static final String TAG = "BotFragment";

    private ListView sequenceList;
    private FloatingActionButton newSequence;

    public SequenceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sequence, container, false);
        sequenceList = rootView.findViewById(R.id.sandSequenceList);
        newSequence = rootView.findViewById(R.id.newSequence);
        newSequence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(getActivity(), SequenceEditActivity.class);
                getActivity().startActivity(editIntent);

            }
        });
        refresh();

        return rootView;
    }

    @Override
    void refresh() {
        SequenceListAdapter sequenceListAdapter =
                new SequenceListAdapter((MainActivity) getActivity(), 0, new ArrayList<>(SandBotStateManager.getSandBotSettings().getSequences().values()));
        sequenceList.setAdapter(sequenceListAdapter);

    }

    @Override
    void enable() {
        newSequence.setEnabled(true);
    }

    @Override
    void disable() {
        newSequence.setEnabled(false);
        sequenceList.setAdapter(null);
    }

}