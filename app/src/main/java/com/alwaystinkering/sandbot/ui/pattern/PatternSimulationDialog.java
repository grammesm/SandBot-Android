package com.alwaystinkering.sandbot.ui.pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.alwaystinkering.sandbot.R;
import com.alwaystinkering.sandbot.model.pattern.Pattern;

public class PatternSimulationDialog {

    private static final String TAG = "PatternSimulationDialog";

    private AlertDialog dialog;

    private int radius = 200;
    private Pattern pattern;

    private Context context;

    private SimulatedSandView sandView;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton stopButton;

    private boolean running = false;

    private boolean stop;

    public PatternSimulationDialog(int radius, Pattern pattern, Context context) {
        this.pattern = pattern;
        this.context = context;
        this.radius = radius;
    }

    public void show() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View dialogView = layoutInflater.inflate(R.layout.dialog_pattern_sim, null);

        sandView = dialogView.findViewById(R.id.simulatedSandView);
        sandView.initTableRadius(radius);

        playButton = dialogView.findViewById(R.id.simPlay);
        pauseButton = dialogView.findViewById(R.id.simPause);
        stopButton = dialogView.findViewById(R.id.simStop);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSim();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseSim();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSim();
            }
        });

        dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setNegativeButton("Close", null)
                .create();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                running = false;
            }
        });

        dialog.show();


        startSim();

    }

    private void startSim() {
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);

        new Thread(simRunnable).start();
    }

    private void pauseSim() {
        running = false;
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void stopSim() {
        //t = 0;
        running = false;
        sandView.clear();
        pattern.reset();
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    private Runnable simRunnable = new Runnable() {
        @Override
        public void run() {
            running = true;

            int count = 0;
            while (!stop && running) {

//                x = Double.valueOf((190 - t * 5) * Math.sin(t)).floatValue();
//                y = Double.valueOf((190 - t * 5) * Math.cos(t)).floatValue();
//                Coordinate c = new Coordinate(x, y);
                sandView.addCoordinateAndRender(pattern.processNextEvaluation());
                //t += 0.01;

                stop = pattern.isStopped();//count >= 2000;//t >= 30;

                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
            }

            Log.d(TAG, "Simulation Thread stopped");

            if (stop) {
                Log.d(TAG, "Stop condition met");
                pattern.reset();
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(true);
            }
        }
    };

}