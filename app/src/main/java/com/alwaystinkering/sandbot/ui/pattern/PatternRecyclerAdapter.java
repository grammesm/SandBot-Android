package com.alwaystinkering.sandbot.ui.pattern;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaystinkering.sandbot.R;
import com.alwaystinkering.sandbot.model.pattern.Pattern;
import com.alwaystinkering.sandbot.model.state.SandBotStateManager;
import com.alwaystinkering.sandbot.model.web.Result;
import com.alwaystinkering.sandbot.model.web.SandBotSettings;
import com.alwaystinkering.sandbot.model.web.SandBotWeb;
import com.alwaystinkering.sandbot.ui.sandbot.MainActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatternRecyclerAdapter extends RecyclerView.Adapter<PatternRecyclerAdapter.ViewHolder> {

    private static final String TAG = "PatternRecyclerAdapter";

    private MainActivity context;
    private List<Pattern> patterns;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView name;
        public ImageView run;
        public ImageView edit;
        public ImageView delete;
        public View layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            icon = itemView.findViewById(R.id.patternIcon);
            name = itemView.findViewById(R.id.patternName);
            run = itemView.findViewById(R.id.patternRun);
            edit = itemView.findViewById(R.id.patternEdit);
            delete = itemView.findViewById(R.id.patternDelete);

        }
    }

    public PatternRecyclerAdapter(MainActivity activity, List<Pattern> patterns) {
        this.context = activity;
        this.patterns = patterns;
    }

    public void add(int position, Pattern item) {
        patterns.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        patterns.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_pattern, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Pattern pattern = patterns.get(position);
        holder.name.setText(pattern.getName());
        holder.run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Result> call = SandBotWeb.getInterface().startPattern(pattern.getName());
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Log.d(TAG, "start " + pattern.getName() + " response: " + response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Log.e(TAG, "start " + pattern.getName() + " fail: " + t.getLocalizedMessage());
                    }
                });
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(context, PatternEditActivity.class);
                editIntent.putExtra(Pattern.PATTERN_NAME_EXTRA_KEY, pattern.getName());
                context.startActivity(editIntent);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete " + pattern.getName())
                        .setMessage("Are you sure you want to delete the pattern " + pattern.getName() + "?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SandBotStateManager.getSandBotSettings().getPatterns().remove(pattern.getName());
                                SandBotStateManager.getSandBotSettings().writeConfig(new SandBotSettings.ConfigWriteListener() {
                                    @Override
                                    public void writeConfigResult(boolean success) {
                                        if (success) {
                                            context.getSettings();
                                        } else {
                                            Log.e(TAG, "Write Failed!");
                                        }
                                    }
                                });
                            }
                        }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return patterns.size();
    }
}
