package com.alwaystinkering.sandbot.ui.sandbot;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.alwaystinkering.sandbot.R;
import com.alwaystinkering.sandbot.model.state.SandBotStateManager;
import com.alwaystinkering.sandbot.model.web.Result;
import com.alwaystinkering.sandbot.model.web.SandBotSettings;
import com.alwaystinkering.sandbot.model.web.SandBotWeb;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BotFragment extends SandBotTab {

    private static final String TAG = "BotFragment";

    private View rootView;

    private SeekBar ledBrightness;
    private ImageView manualBrightness;
    private ImageView autoBrightness;
    private SeekBar speed;

    private FloatingActionButton play;
    private FloatingActionButton pause;
    private FloatingActionButton stop;
    private FloatingActionButton home;

    public BotFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bot, container, false);

        ledBrightness = rootView.findViewById(R.id.ledBrightnessSeekBar);
        manualBrightness = rootView.findViewById(R.id.ledManualBrightness);
        autoBrightness = rootView.findViewById(R.id.ledAutoBrightness);
        speed = rootView.findViewById(R.id.speedSeekBar);
        play = rootView.findViewById(R.id.botPlay);
        pause = rootView.findViewById(R.id.botPause);
        stop = rootView.findViewById(R.id.botStop);
        home = rootView.findViewById(R.id.botHome);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Result> call = SandBotWeb.getInterface().home();
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Log.d(TAG, "onResponse: " + response.body().toString());
                        Snackbar.make(rootView, "Homing", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                    }
                });
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Result> call = SandBotWeb.getInterface().pause();
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Log.d(TAG, "onResponse: " + response.body().toString());
                        Snackbar.make(rootView, "Paused", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                    }
                });
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Result> call = SandBotWeb.getInterface().resume();
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Log.d(TAG, "onResponse: " + response.body().toString());
                        Snackbar.make(rootView, "Resumed", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                    }
                });
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Result> call = SandBotWeb.getInterface().stop();
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Log.d(TAG, "onResponse: " + response.body().toString());
                        Snackbar.make(rootView, "Stopped", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                    }
                });
            }
        });

        ledBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SandBotStateManager.getSandBotSettings().setLedBrightness(ledBrightness.getProgress());
                SandBotStateManager.getSandBotSettings().writeConfig(new SandBotSettings.ConfigWriteListener() {
                    @Override
                    public void writeConfigResult(boolean success) {
                        if (success) {
                            refresh();
                        }
                    }
                });
            }
        });

        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SandBotStateManager.getSandBotSettings().setSpeed(speed.getProgress());
                SandBotStateManager.getSandBotSettings().writeConfig(new SandBotSettings.ConfigWriteListener() {
                    @Override
                    public void writeConfigResult(boolean success) {
                        if (success) {
                            refresh();
                        }
                    }
                });
            }
        });


        manualBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SandBotStateManager.getSandBotSettings().setLedAutoDim(false);
                SandBotStateManager.getSandBotSettings().writeConfig(new SandBotSettings.ConfigWriteListener() {
                    @Override
                    public void writeConfigResult(boolean success) {
                        if (success) {
                            Snackbar.make(rootView, "Manual Brightness Enabled", Snackbar.LENGTH_SHORT).show();
                            refresh();
                        }
                    }
                });
            }
        });
        autoBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SandBotStateManager.getSandBotSettings().setLedAutoDim(true);
                SandBotStateManager.getSandBotSettings().writeConfig(new SandBotSettings.ConfigWriteListener() {
                    @Override
                    public void writeConfigResult(boolean success) {
                        if (success) {
                            Snackbar.make(rootView, "Auto Brightness Enabled", Snackbar.LENGTH_SHORT).show();
                            refresh();
                        }
                    }
                });
            }
        });
        refresh();
        return rootView;
    }

    @Override
    void refresh() {
        ledBrightness.setProgress(SandBotStateManager.getSandBotSettings().getLedBrightness());
        if (SandBotStateManager.getSandBotSettings().isLedAutoDim()) {
            autoBrightness.setColorFilter(getResources().getColor(R.color.green_selected));
            manualBrightness.setColorFilter(getResources().getColor(R.color.light_gray));
            ledBrightness.setEnabled(false);
        } else {
            autoBrightness.setColorFilter(getResources().getColor(R.color.light_gray));
            manualBrightness.setColorFilter(getResources().getColor(R.color.green_selected));
            ledBrightness.setEnabled(true);
        }
    }

    @Override
    void enable() {
        Log.d(TAG, "Enable");
        ledBrightness.setEnabled(!SandBotStateManager.getSandBotSettings().isLedAutoDim());
        speed.setEnabled(true);
        play.setEnabled(true);
        pause.setEnabled(true);
        stop.setEnabled(true);
        home.setEnabled(true);
    }

    @Override
    void disable() {
        Log.d(TAG, "Disable");
        ledBrightness.setEnabled(false);
        speed.setEnabled(false);
        play.setEnabled(false);
        pause.setEnabled(false);
        stop.setEnabled(false);
        home.setEnabled(false);
    }
}
