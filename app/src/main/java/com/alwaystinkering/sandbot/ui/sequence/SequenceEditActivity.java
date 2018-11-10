package com.alwaystinkering.sandbot.ui.sequence;

import android.os.Bundle;
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
import com.alwaystinkering.sandbot.model.pattern.Pattern;
import com.alwaystinkering.sandbot.model.sequence.Sequence;
import com.alwaystinkering.sandbot.model.state.SandBotStateManager;
import com.alwaystinkering.sandbot.model.web.SandBotSettings;
import com.alwaystinkering.sandbot.ui.pattern.PatternSimulationDialog;


public class SequenceEditActivity extends AppCompatActivity {

    private static final String TAG = "SequenceEditActivity";

    private EditText name;
    private EditText commands;
    private Button saveButton;

    private Sequence sequence;

    private String originalName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence_edit);

        if (getIntent().getExtras() != null) {
            Log.d(TAG, "Extras supplied");
            originalName = getIntent().getStringExtra(Sequence.SEQUENCE_NAME_EXTRA_KEY);
            if (originalName != null) {
                sequence = SandBotStateManager.getSandBotSettings().getSequences().get(originalName);
            } else {
                Log.d(TAG, "Sequence name not found!");
                sequence = new Sequence("", "", false);
            }
        } else {
            Log.d(TAG, "No sequence name defined, creating new");
            sequence = new Sequence("", "", false);
        }

        name = findViewById(R.id.sequenceNameInput);
        commands = findViewById(R.id.sequenceCommandInput);
        name.setText(sequence.getName());
        commands.setText(sequence.getCommands());
        commands.addTextChangedListener(new TextWatcher() {
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

        saveButton = findViewById(R.id.sequenceSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSequence();
            }
        });


        saveButton.setEnabled(false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void textChanged() {
        sequence.setName(name.getText().toString().trim());
        sequence.setCommands(commands.getText().toString().trim());
        saveButton.setEnabled(true);
    }

    private void saveSequence() {
        if (name.getText().toString().trim().isEmpty()) {
            Log.e(TAG, "Name can not be empty!");
            new AlertDialog.Builder(SequenceEditActivity.this)
                    .setTitle("ERROR")
                    .setMessage("Sequence name must be defined to save")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Remove the old pattern if it exists
        if (originalName != null) {
            SandBotStateManager.getSandBotSettings().getSequences().remove(originalName);
        }

        // Save the newly created/edited pattern
        SandBotStateManager.getSandBotSettings().getSequences().put(sequence.getName(), sequence);
        SandBotStateManager.getSandBotSettings().writeConfig(new SandBotSettings.ConfigWriteListener() {
            @Override
            public void writeConfigResult(boolean success) {
                if (success) {
                    SequenceEditActivity.this.finish();
                } else {
                    Log.e(TAG, "Write Failed!");
                }
            }
        });
    }
}
