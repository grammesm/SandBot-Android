package com.alwaystinkering.sandbot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alwaystinkering.sandbot.model.state.SandBotStateManager;
import com.alwaystinkering.sandbot.model.web.Result;
import com.alwaystinkering.sandbot.model.web.SandBotWeb;
import com.alwaystinkering.sandbot.ui.pattern.PatternEditActivity;
import com.alwaystinkering.sandbot.ui.pattern.PatternListAdapter;
import com.alwaystinkering.sandbot.ui.sequence.SequenceEditActivity;
import com.alwaystinkering.sandbot.ui.sequence.SequenceListAdapter;
import com.alwaystinkering.sandbot.ui.settings.SettingsActivity;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SandBotActivity extends AppCompatActivity {

    private static final String TAG = "SandBotActivity";


    private ListView patternList;
    private ListView sequenceList;
    private ImageView patternAdd;
    private ImageView sequenceAdd;

    private SharedPreferences sharedPreferences;

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals(getResources().getString(R.string.pref_server_key))) {
                String serverIp = sharedPreferences.getString(getResources().getString(R.string.pref_server_key),"").trim();
                // todo validate
                Log.d(TAG, "Creating new server interface to: " + serverIp);
                SandBotWeb.createInterface(serverIp);
                getSettings();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sand_bot);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(getResources().getColor(R.color.darkSand));
        }

        TextView homeButton = findViewById(R.id.sandBotHome);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Result> call = SandBotWeb.getInterface().home();
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Log.d(TAG, "onResponse: " + response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                    }
                });
            }
        });

        Button pauseButton = findViewById(R.id.sandBotPause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Result> call = SandBotWeb.getInterface().pause();
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Log.d(TAG, "onResponse: " + response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                    }
                });
            }
        });

        TextView resumeButton = findViewById(R.id.sandBotResume);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Result> call = SandBotWeb.getInterface().resume();
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Log.d(TAG, "onResponse: " + response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                    }
                });
            }
        });

        TextView stopButton = findViewById(R.id.sandBotStop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Result> call = SandBotWeb.getInterface().stop();
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Log.d(TAG, "onResponse: " + response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                    }
                });
            }
        });

        patternAdd = findViewById(R.id.sandBotPatternAdd);
        patternAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(SandBotActivity.this, PatternEditActivity.class);
                SandBotActivity.this.startActivity(editIntent);

            }
        });

        sequenceAdd = findViewById(R.id.sandBotSequenceAdd);
        sequenceAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(SandBotActivity.this, SequenceEditActivity.class);
                SandBotActivity.this.startActivity(editIntent);
            }
        });

        patternList = findViewById(R.id.sandPatternList);
        sequenceList = findViewById(R.id.sandSequenceList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        getSettings();
    }

    public void getSettings() {
        Log.d(TAG, "Get Settings");
        Call<ResponseBody> call = SandBotWeb.getInterface().getSettings();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    Log.d(TAG, "Return JSON: " + json);
                    SandBotStateManager.getSandBotSettings().clear();
                    SandBotStateManager.getSandBotSettings().parse(json);
                    Log.d(TAG, "onResponse : " + SandBotStateManager.getSandBotSettings().toJson());
                    PatternListAdapter patternListAdapter =
                            new PatternListAdapter(SandBotActivity.this, 0, new ArrayList<>(SandBotStateManager.getSandBotSettings().getPatterns().values()));
                    patternList.setAdapter(patternListAdapter);

                    SequenceListAdapter sequenceListAdapter =
                            new SequenceListAdapter(SandBotActivity.this, 0, new ArrayList<>(SandBotStateManager.getSandBotSettings().getSequences().values()));
                    sequenceList.setAdapter(sequenceListAdapter);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });
    }
}
