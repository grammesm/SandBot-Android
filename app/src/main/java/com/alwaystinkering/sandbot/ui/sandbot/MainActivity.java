package com.alwaystinkering.sandbot.ui.sandbot;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
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
import com.alwaystinkering.sandbot.model.state.SandBotStateManager;
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
            }
        }
    };

    private TextView commsError;
    private TabPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        commsError = findViewById(R.id.commsError);

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

    }

    @Override
    public void onStart() {
        super.onStart();
        SandBotStateManager.getSandBotSettings().clear();
        sectionsPagerAdapter.disable();
        getSettings();
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
                    sectionsPagerAdapter.enable();
                    sectionsPagerAdapter.refresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                commsError.setVisibility(View.VISIBLE);
                SandBotStateManager.getSandBotSettings().clear();
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                sectionsPagerAdapter.disable();
            }
        });
    }
}
