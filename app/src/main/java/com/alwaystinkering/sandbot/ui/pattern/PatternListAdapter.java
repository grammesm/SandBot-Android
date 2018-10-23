package com.alwaystinkering.sandbot.ui.pattern;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.alwaystinkering.sandbot.model.state.SandBotStateManager;
import com.alwaystinkering.sandbot.model.web.Result;
import com.alwaystinkering.sandbot.model.web.SandBotSettings;
import com.alwaystinkering.sandbot.model.web.SandBotWeb;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatternListAdapter extends ArrayAdapter<Pattern> {

    private static final String TAG = "PatternListAdapter";

    private SandBotActivity context;

    static class ViewHolder {
        public TextView name;
        public ImageView run;
        public ImageView edit;
        public ImageView delete;
    }

    public PatternListAdapter(@NonNull SandBotActivity context, int resource, @NonNull List<Pattern> objects) {
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
            rowView = inflater.inflate(R.layout.list_item_pattern, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = rowView.findViewById(R.id.patternTitle);
            viewHolder.run = rowView.findViewById(R.id.patternRun);
            viewHolder.edit = rowView.findViewById(R.id.patternEdit);
            viewHolder.delete = rowView.findViewById(R.id.patternDelete);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        final Pattern pattern = getItem(position);
        if (pattern == null) {
            return null;
        }
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


        return rowView;
    }
}
