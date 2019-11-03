package com.alwaystinkering.sandbot.ui.sandbot;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alwaystinkering.sandbot.R;
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern;
import com.alwaystinkering.sandbot.model.state.FileManager;
import com.alwaystinkering.sandbot.ui.pattern.PatternEditActivity;
import com.alwaystinkering.sandbot.ui.pattern.PatternRecyclerAdapter;

import java.util.Collections;
import java.util.List;

public class PatternFragment extends SandBotTab {

    private static final String TAG = "PatternFragment";

    //private ListView patternList;
    private RecyclerView patternList;
    private RecyclerView.Adapter patternListAdapter;
    private RecyclerView.LayoutManager patternListLayoutManager;
    private FloatingActionButton newPattern;

    private int numFiles = 0;

    public PatternFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file, container, false);
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
        int files = FileManager.getFilesMap().size();
        if (patternListAdapter == null || numFiles != files) {
            List<AbstractPattern> fileList = FileManager.getFiles();
            Collections.sort(fileList, FileManager.FILE_NAME_COMPARATOR);
            patternListAdapter =
                    new PatternRecyclerAdapter((MainActivity) getActivity(), fileList);
            patternList.setAdapter(patternListAdapter);
            numFiles = files;
        } else {
            patternListAdapter.notifyDataSetChanged();
        }
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
