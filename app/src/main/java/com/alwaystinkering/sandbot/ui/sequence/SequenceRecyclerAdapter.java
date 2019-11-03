package com.alwaystinkering.sandbot.ui.sequence;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaystinkering.sandbot.R;
import com.alwaystinkering.sandbot.model.sequence.Sequence;
import com.alwaystinkering.sandbot.model.state.SandBotStateManager;
import com.alwaystinkering.sandbot.model.web.Result;
import com.alwaystinkering.sandbot.model.web.SandBotSettings;
import com.alwaystinkering.sandbot.model.web.SandBotWeb;
import com.alwaystinkering.sandbot.ui.sandbot.MainActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SequenceRecyclerAdapter extends RecyclerView.Adapter<SequenceRecyclerAdapter.ViewHolder> {

    private static final String TAG = "SequenceRecyclerAdapter";

    private MainActivity context;
    private List<Sequence> sequences;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView name;
        public ImageView runAtStartup;
        public ImageView run;
        public ImageView edit;
        public ImageView delete;
        public View layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            icon = itemView.findViewById(R.id.sequenceIcon);
            name = itemView.findViewById(R.id.sequenceName);
            runAtStartup = itemView.findViewById(R.id.sequenceAutoRun);
            run = itemView.findViewById(R.id.sequenceRun);
            edit = itemView.findViewById(R.id.sequenceEdit);
            delete = itemView.findViewById(R.id.sequenceDelete);

        }
    }

    public SequenceRecyclerAdapter(MainActivity activity, List<Sequence> sequences) {
        this.context = activity;
        this.sequences = sequences;
    }

    public void add(int position, Sequence item) {
        sequences.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        sequences.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_sequence, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Sequence sequence = sequences.get(position);
        holder.name.setText(sequence.getName());
        if (sequence.isAutoRun()) {
            holder.runAtStartup.setColorFilter(context.getResources().getColor(R.color.green_selected));
        } else {
            holder.runAtStartup.setColorFilter(context.getResources().getColor(R.color.light_gray));
        }
        holder.runAtStartup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sequence.setAutoRun(!sequence.isAutoRun());
                if (sequence.isAutoRun()) {
                    //SandBotStateManager.getSandBotSettings().setStartup("g28;" + sequence.getName());
                } else {
                    //SandBotStateManager.getSandBotSettings().setStartup("");
                }
                SandBotStateManager.getSandBotSettings().writeConfig(new SandBotSettings.ConfigWriteListener() {
                    @Override
                    public void writeConfigResult(boolean success) {
                        if (success) {
                            context.getSettings();
                        }
                    }
                });
            }
        });
        holder.run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Result> call = SandBotWeb.getInterface().startSequence(sequence.getName());
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Log.d(TAG, "start " + sequence.getName() + " response: " + response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Log.e(TAG, "start " + sequence.getName() + " fail: " + t.getLocalizedMessage());
                    }
                });
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(context, SequenceEditActivity.class);
                editIntent.putExtra(Sequence.SEQUENCE_NAME_EXTRA_KEY, sequence.getName());
                context.startActivity(editIntent);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete " + sequence.getName())
                        .setMessage("Are you sure you want to delete the sequence " + sequence.getName() + "?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sequence.isAutoRun()) {
                                    //SandBotStateManager.getSandBotSettings().setStartup("");
                                }
                                //SandBotStateManager.getSandBotSettings().getSequences().remove(sequence.getName());
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
        return sequences.size();
    }
}
