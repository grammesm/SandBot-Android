package com.alwaystinkering.sandbot.ui.sandbot;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaystinkering.sandbot.R;
import com.alwaystinkering.sandbot.SandBotActivity;
import com.alwaystinkering.sandbot.model.pattern.Pattern;
import com.alwaystinkering.sandbot.model.sequence.Sequence;
import com.alwaystinkering.sandbot.model.state.SandBotStateManager;
import com.alwaystinkering.sandbot.model.web.Result;
import com.alwaystinkering.sandbot.model.web.SandBotSettings;
import com.alwaystinkering.sandbot.model.web.SandBotWeb;
import com.alwaystinkering.sandbot.ui.pattern.PatternEditActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SequenceListAdapter extends ArrayAdapter<Sequence> {

    private static final String TAG = "SequenceListAdapter";

    private SandBotActivity context;

    static class ViewHolder {
        public TextView name;
        public ImageView run;
        public ImageView runAtStartup;
        public ImageView edit;
        public ImageView delete;
    }

    public SequenceListAdapter(@NonNull SandBotActivity context, int resource, @NonNull List<Sequence> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_sequence, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = rowView.findViewById(R.id.sequenceTitle);
            viewHolder.runAtStartup = rowView.findViewById(R.id.sequenceAutoRun);
            viewHolder.run = rowView.findViewById(R.id.sequenceRun);
            viewHolder.edit = rowView.findViewById(R.id.sequenceEdit);
            viewHolder.delete = rowView.findViewById(R.id.sequenceDelete);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        final Sequence sequence = getItem(position);
        if (sequence == null) {
            return null;
        }
        holder.name.setText(sequence.getName());
        if (sequence.isAutoRun()) {
            holder.runAtStartup.setColorFilter(context.getResources().getColor(R.color.nav));
        } else {
            holder.runAtStartup.setColorFilter(Color.argb(255, 255, 255, 255));
        }
        holder.run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Call<Result> call = SandBotWeb.getInterface().startPattern(pattern.getName());
//                call.enqueue(new Callback<Result>() {
//                    @Override
//                    public void onResponse(Call<Result> call, Response<Result> response) {
//                        Log.d(TAG, "start " + pattern.getName() + " response: " + response.body().toString());
//                    }
//
//                    @Override
//                    public void onFailure(Call<Result> call, Throwable t) {
//                        Log.e(TAG, "start " + pattern.getName() + " fail: " + t.getLocalizedMessage());
//                    }
//                });
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent editIntent = new Intent(context, PatternEditActivity.class);
//                editIntent.putExtra(Pattern.PATTERN_NAME_EXTRA_KEY, pattern.getName());
//                context.startActivity(editIntent);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete " + sequence.getName())
                        .setMessage("Are you sure you want to delete the pattern " + sequence.getName() + "?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SandBotStateManager.getSandBotSettings().getPatterns().remove(sequence.getName());
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


        return rowView;
    }
}
