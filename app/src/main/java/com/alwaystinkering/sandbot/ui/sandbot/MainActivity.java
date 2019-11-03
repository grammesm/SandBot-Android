package com.alwaystinkering.sandbot.ui.sandbot;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alwaystinkering.sandbot.R;
import com.alwaystinkering.sandbot.model.state.FileManager;
import com.alwaystinkering.sandbot.model.state.SandBotStateManager;
import com.alwaystinkering.sandbot.model.web.FileListResult;
import com.alwaystinkering.sandbot.model.web.SandBotWeb;
import com.alwaystinkering.sandbot.ui.settings.SettingsActivity;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SharedPreferences sharedPreferences;

    private Thread statusThread;
    private boolean statusRunning = false;

    private boolean test = false;
    public static int tableDiameter;


    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals(getResources().getString(R.string.pref_server_key))) {
                String serverIp = sharedPreferences.getString(getResources().getString(R.string.pref_server_key),"").trim();
                // todo validate
                Log.d(TAG, "Creating new server interface to: " + serverIp);
                SandBotWeb.createInterface(serverIp);
                SandBotStateManager.getSandBotSettings().clear();
                sectionsPagerAdapter.disable();
                //clearView();
                getSettings();
            } else if (s.equals(getResources().getString(R.string.pref_server_key))) {
                String radiusString = sharedPreferences.getString(getResources().getString(R.string.pref_diameter_key), getResources().getString(R.string.pref_diameter_default)).trim();
                tableDiameter = Integer.parseInt(radiusString);
                Log.d(TAG, "Set Diameter to " + tableDiameter);
            }
        }
    };

    private TextView commsError;
    private TextView testMode;
    private TabPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        commsError = findViewById(R.id.commsError);
        testMode = findViewById(R.id.testMode);

        commsError.setVisibility(View.VISIBLE);
        commsError.setText("Communicating...");
        commsError.setBackgroundColor(getResources().getColor(R.color.nav));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        String serverIp = sharedPreferences.getString(getResources().getString(R.string.pref_server_key),getResources().getString(R.string.pref_server_default)).trim();

        if (serverIp.equals(getResources().getString(R.string.pref_server_default))) {
            new AlertDialog.Builder(this)
                    .setTitle("Enter Robot IP Address")
                    .setMessage("It appears you do not have an IP address set up for the sand bot, press OK to be taken to settings to enter the IP address of the bot.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intent);
                        }
                    }).show();
        }
        Log.d(TAG, "Creating new server interface to: " + serverIp);
        SandBotWeb.createInterface(serverIp);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setNavigationBarColor(getResources().getColor(R.color.darkSand));
//        }

        sectionsPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        String radiusString = sharedPreferences.getString(getResources().getString(R.string.pref_diameter_key), getResources().getString(R.string.pref_diameter_default)).trim();
        tableDiameter = Integer.parseInt(radiusString);


    }

    private void showCommsError() {
        commsError.setVisibility(View.VISIBLE);
        commsError.setBackgroundColor(getResources().getColor(R.color.red_error));
        commsError.setText("Error Communicating with Sand Bot");
    }

    @Override
    public void onStart() {
        super.onStart();
        SandBotStateManager.getSandBotSettings().clear();
        sectionsPagerAdapter.disable();
        FileManager.addListener(new FileManager.FileManagerListener() {
            @Override
            public void listUpdated() {
                refresh();
            }

            @Override
            public void storageSize(long totalBytes, long usedBytes) {
                refresh();
            }
        });

        getFiles();

        // Start status
        if (statusThread == null) {
            statusThread = new Thread(statusRunnable);
            statusThread.start();
        }

        if (test) {
            testMode.setVisibility(View.VISIBLE);
            int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck1 != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        33);
            } else {
                FileManager.initializeTestFiles();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 33) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                FileManager.initializeTestFiles();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (statusThread != null) {
            statusRunning = false;
            statusThread = null;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getFiles() {
        Log.d(TAG, "Get Files");
        Call<FileListResult> call = SandBotWeb.getInterface().listFiles();
        call.enqueue(new Callback<FileListResult>() {
            @Override
            public void onResponse(Call<FileListResult> call, Response<FileListResult> response) {
                commsError.setVisibility(View.GONE);
                FileListResult result = response.body();
                if (result != null) {
//                    filesLayout.setVisibility(View.VISIBLE);
//                    long total = result.getDiskUsed().longValue();
//                    long used = result.getDiskSize().longValue();
//                    filesText.setText(FileUtils.byteCountToDisplaySize(used) + "/" + FileUtils.byteCountToDisplaySize(total));
//                    filesProgress.setProgress(Long.valueOf(used / total).intValue());

                    FileManager.processFileList(result);
//                    Log.d(TAG, result.toString());
//                    FileManager.setFileSystemName(result.getFsName());
//                    for (SandBotFile f : response.body().getSandBotFiles()) {
//                        FileManager.createPatternFromFile(f);
//                    }
                }
            }

            @Override
            public void onFailure(Call<FileListResult> call, Throwable t) {
//                filesLayout.setVisibility(View.GONE);
                showCommsError();
                Log.e(TAG, "SandBotFile List Fail");
            }
        });
    }

    public void getSettings() {
        Log.d(TAG, "Get Settings");
        Call<ResponseBody> call = SandBotWeb.getInterface().getSettings();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    commsError.setVisibility(View.GONE);
                    String json = response.body().string();
                    Log.d(TAG, "Return JSON: " + json);
                    SandBotStateManager.getSandBotSettings().clear();
                    SandBotStateManager.getSandBotSettings().parse(json);
                    Log.d(TAG, "onResponse : " + SandBotStateManager.getSandBotSettings().toJson());
                    if (!sectionsPagerAdapter.isEnabled()) {
                        sectionsPagerAdapter.enable();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showCommsError();
                SandBotStateManager.getSandBotSettings().clear();
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                if (sectionsPagerAdapter.isEnabled()) {
                    sectionsPagerAdapter.disable();
                }
            }
        });
    }

    public void refresh() {
        sectionsPagerAdapter.refresh();
    }

    private Runnable statusRunnable = new Runnable() {
        @Override
        public void run() {
            statusRunning = true;

            while (statusRunning) {
                Call<ResponseBody> call = SandBotWeb.getInterface().getStatus();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        commsError.setVisibility(View.GONE);
                        String json = null;
                        try {
                            json = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "Status JSON: " + json);
                        SandBotStateManager.getSandBotStatus().parse(json);
                        if (!sectionsPagerAdapter.isEnabled()) {
                            sectionsPagerAdapter.enable();
                        }
                        refresh();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        showCommsError();
                        if (sectionsPagerAdapter.isEnabled()) {
                            sectionsPagerAdapter.disable();
                        }
                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Log.d(TAG, "Status Thread stopped");
        }
    };
}
