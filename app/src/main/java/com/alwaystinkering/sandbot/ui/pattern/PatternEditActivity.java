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
import com.alwaystinkering.sandbot.model.pattern.ParametricPattern;
import com.alwaystinkering.sandbot.model.state.SandBotStateManager;
import com.alwaystinkering.sandbot.model.web.SandBotSettings;


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
            if (originalName != null) {
                //parametricPattern = SandBotStateManager.getSandBotSettings().getPatterns().get(originalName);
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
        declaraions.setText(parametricPattern.getDeclarationString());
        expressions.setText(parametricPattern.getExpressionString());
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

        String prefRadius = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getResources().getString(R.string.pref_diameter_key), getResources().getString(R.string.pref_diameter_default));
        final int tableRadius = Integer.valueOf(prefRadius);

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
