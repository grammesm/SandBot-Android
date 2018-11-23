package com.alwaystinkering.sandbot.ui.sandbot;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alwaystinkering.sandbot.R;
import com.alwaystinkering.sandbot.model.state.SandBotStateManager;
import com.alwaystinkering.sandbot.ui.pattern.PatternEditActivity;
import com.alwaystinkering.sandbot.ui.pattern.PatternRecyclerAdapter;

import java.util.ArrayList;

public class PatternFragment extends SandBotTab {

    private static final String TAG = "PatternFragment";

    //private ListView patternList;
    private RecyclerView patternList;
    private RecyclerView.Adapter patternListAdapter;
    private RecyclerView.LayoutManager patternListLayoutManager;
    private FloatingActionButton newPattern;

    public PatternFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pattern, container, false);
        patternList = rootView.findViewById(R.id.sandPatternList);
        patternListLayoutManager = new LinearLayoutManager(getActivity());
        patternList.setLayoutManager(patternListLayoutManager);

//        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
//                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//                    @Override
//                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
//                            target) {
//                        return false;
//                    }
//                    @Override
//                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
////                        input.remove(viewHolder.getAdapterPosition());
////                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
//                    }
//                };
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
//        itemTouchHelper.attachToRecyclerView(patternList);


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
        patternListAdapter =
                new PatternRecyclerAdapter((MainActivity) getActivity(),
                        new ArrayList<>(SandBotStateManager.getSandBotSettings().getPatterns().values()));
        patternList.setAdapter(patternListAdapter);
    }

    @Override
    void enable() {
        newPattern.setEnabled(true);
        refresh();
    }

    @Override
    void disable() {
        //newPattern.setEnabled(false);
        patternList.setAdapter(null);
    }
}
