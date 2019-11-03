package com.alwaystinkering.sandbot.ui.pattern;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alwaystinkering.sandbot.R;
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern;
import com.alwaystinkering.sandbot.model.pattern.ParametricPattern;
import com.alwaystinkering.sandbot.model.state.FileManager;
import com.alwaystinkering.sandbot.model.state.SandBotStateManager;
import com.alwaystinkering.sandbot.model.web.ParametricFile;
import com.alwaystinkering.sandbot.model.web.SandBotSettings;
import com.alwaystinkering.sandbot.model.web.SandBotWeb;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PatternEditActivity extends AppCompatActivity {

    private static final String TAG = "PatternEditActivity";

    private EditText name;
    private EditText declaraions;
    private EditText expressions;
    private Button validateButton;
    private ImageView check;
    private Button simButton;
    private Button saveButton;

    private ParametricPattern parametricPattern;

    private String originalName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param_pattern_edit);

        if (getIntent().getExtras() != null) {
            Log.d(TAG, "Extras supplied");
            originalName = getIntent().getStringExtra(ParametricPattern.PATTERN_NAME_EXTRA_KEY);
            Log.d(TAG, "originalName: " + originalName);
            if (originalName != null) {
                AbstractPattern pattern = FileManager.getFilesMap().get(originalName);
                if (pattern instanceof ParametricPattern) {
                    parametricPattern = (ParametricPattern) pattern;
                } else {
                    Log.e(TAG, originalName + " is not a parametric pattern");
                    finish();
                    return;
                }
            } else {
                Log.d(TAG, "ParametricPattern name not found!");
                parametricPattern = new ParametricPattern("", "", "");
            }
        } else {
            Log.d(TAG, "No parametricPattern name defined, creating new");
            parametricPattern = new ParametricPattern("", "", "");
        }

        name = findViewById(R.id.patternNameInput);
        declaraions = findViewById(R.id.patternDecInput);
        expressions = findViewById(R.id.patternExpInput);
        name.setText(parametricPattern.getName());
//        declaraions.setText(parametricPattern.getDeclarationString());
//        expressions.setText(parametricPattern.getExpressionString());
        declaraions.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textChanged();
            }
        });
        expressions.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textChanged();
            }
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textChanged();
            }
        });
        validateButton = findViewById(R.id.patternValidateButton);
        check = findViewById(R.id.validationCheck);
        simButton = findViewById(R.id.patternRunButton);

        String prefDiameter = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getResources().getString(R.string.pref_diameter_key), getResources().getString(R.string.pref_diameter_default));
        final int tableRadius = Integer.valueOf(prefDiameter) / 2;

        simButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PatternSimulationDialog(tableRadius, parametricPattern, PatternEditActivity.this).show();
            }
        });

        saveButton = findViewById(R.id.patternSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePattern();
            }
        });


        saveButton.setEnabled(false);
        simButton.setEnabled(false);


        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parametricPattern != null) {
                    boolean valid = parametricPattern.validate();
                    simButton.setEnabled(valid);
                    saveButton.setEnabled(valid);
                    if (valid) {
                        check.setVisibility(View.VISIBLE);
                        // Pop up log

                    } else {
                        check.setVisibility(View.GONE);
                        Log.d(TAG, parametricPattern.getValidationResults());
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Try and download
        downloadFile();

    }

    public void downloadFile() {
        Log.d(TAG, "Download File: " + parametricPattern.getName());
        Call<ParametricFile> call = SandBotWeb.getInterface().getParametricFile(FileManager.getFileSystemName(), parametricPattern.getName());
        call.enqueue(new Callback<ParametricFile>() {
            @Override
            public void onResponse(Call<ParametricFile> call, Response<ParametricFile> response) {
                Log.d(TAG, "SandBotFile Downloaded: " + parametricPattern.getName());
                final ParametricFile result = response.body();
                if (result != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            processFile(result);
                        }
                    });
                } else {
                    Log.e(TAG, "SandBotFile : " + parametricPattern.getName() + " could not be parsed");
                }
            }

            @Override
            public void onFailure(Call<ParametricFile> call, Throwable t) {
                Log.e(TAG, "SandBotFile Download Fail: " + parametricPattern.getName());
            }
        });
    }

    private void processFile(ParametricFile file) {
        declaraions.setText(file.getSetup());
        expressions.setText(file.getLoop());
    }


    private void textChanged() {
        parametricPattern.setName(name.getText().toString().trim());
        parametricPattern.setDeclarationString(declaraions.getText().toString().trim());
        parametricPattern.setExpressionString(expressions.getText().toString().trim());
        saveButton.setEnabled(false);
        simButton.setEnabled(false);
        check.setVisibility(View.GONE);
    }

    private void savePattern() {
        if (name.getText().toString().trim().isEmpty()) {
            Log.e(TAG, "Name can not be empty!");
            new AlertDialog.Builder(PatternEditActivity.this)
                    .setTitle("ERROR")
                    .setMessage("ParametricPattern name must be defined to save")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Remove the old parametricPattern if it exists
        if (originalName != null) {
            //SandBotStateManager.getSandBotSettings().getPatterns().remove(originalName);
        }

        // Save the newly created/edited parametricPattern
        //SandBotStateManager.getSandBotSettings().getPatterns().put(parametricPattern.getName(), parametricPattern);
        SandBotStateManager.getSandBotSettings().writeConfig(new SandBotSettings.ConfigWriteListener() {
            @Override
            public void writeConfigResult(boolean success) {
                if (success) {
                    PatternEditActivity.this.finish();
                } else {
                    Log.e(TAG, "Write Failed!");
                }
            }
        });
    }
}
