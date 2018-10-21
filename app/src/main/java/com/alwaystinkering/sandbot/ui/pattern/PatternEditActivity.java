package com.alwaystinkering.sandbot.ui.pattern;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alwaystinkering.sandbot.R;
import com.alwaystinkering.sandbot.model.pattern.Pattern;
import com.alwaystinkering.sandbot.model.state.SandBotStateManager;


public class PatternEditActivity extends AppCompatActivity {

    private static final String TAG = "PatternEditActivity";

    private EditText name;
    private EditText declaraions;
    private EditText expressions;
    private Button validateButton;
    private ImageView check;
    private Button simButton;

    private Pattern pattern;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_edit);

        if (getIntent().getExtras() != null) {
            Log.d(TAG, "Extras supplied");
            String name = getIntent().getStringExtra(Pattern.PATTERN_NAME_EXTRA_KEY);
            pattern = SandBotStateManager.getSandBotSettings().getPatterns().get(name);
        }

        name = findViewById(R.id.patternNameInput);
        declaraions = findViewById(R.id.patternDecInput);
        expressions = findViewById(R.id.patternExpInput);
        name.setText(pattern.getName());
        declaraions.setText(pattern.getDeclarationString());
        expressions.setText(pattern.getExpressionString());
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
        validateButton = findViewById(R.id.patternValidateButton);
        check = findViewById(R.id.validationCheck);
        simButton = findViewById(R.id.patternRunButton);

        simButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PatternSimulationDialog(200, pattern,PatternEditActivity.this).show();
            }
        });

        simButton.setEnabled(false);

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pattern != null) {
                    boolean valid = pattern.validate();
                    simButton.setEnabled(valid);
                    if (valid) {
                        check.setVisibility(View.VISIBLE);
                        // Pop up log

                    } else {
                        check.setVisibility(View.GONE);
                        Log.d(TAG, pattern.getValidationResults());
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
        pattern.setDeclarationString(declaraions.getText().toString());
        pattern.setExpressionString(expressions.getText().toString());
        simButton.setEnabled(false);
        check.setVisibility(View.GONE);
    }

    private void savePattern() {

    }
}
