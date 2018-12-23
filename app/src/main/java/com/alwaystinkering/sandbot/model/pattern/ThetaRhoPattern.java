package com.alwaystinkering.sandbot.model.pattern;

import android.util.Log;

import com.alwaystinkering.sandbot.model.web.SandBotFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ThetaRhoPattern extends AbstractPattern {

    private static final String TAG = "ThetaRhoPattern";

    private List<String> commands = new ArrayList<>();
    private int lineCount = 0;
    private int radius = 380;

    private float _curTheta;
    private float _curRho;
    private int _totalSteps;
    private int _curStep;
    private float _thetaInc;
    private float _rhoInc;

    private float _maxAngle = Double.valueOf(Math.PI / 64.0).floatValue();

    private boolean lineInProgress = false;


    public ThetaRhoPattern(String name) {
        super(name, FileType.THETA_RHO);
    }


    @Override
    public Coordinate processNextEvaluation() {
        Log.d(TAG, "process Next. Line in progress: " + lineInProgress + ", Count: " + lineCount);
        if (!lineInProgress) {
            String line = commands.get(lineCount);
            Log.d(TAG, "Processing line: " + line);
            String[] parts = line.split(" ");
            float theta = Float.parseFloat(parts[0].trim());
            float rho = Float.parseFloat(parts[1].trim());
            calculateIncrements(theta, rho);
            lineCount++;
        }
        return advance();
    }

    private void calculateIncrements(float theta, float rho) {
        float thetaDiff = theta - _curTheta;
        float rhoDiff = rho - _curRho;

        _thetaInc = thetaDiff;
        _rhoInc = rhoDiff;
        _totalSteps = 1;

//        if (Math.abs(thetaDiff) < _maxAngle) {
//            _totalSteps = 1;
//            _thetaInc = 0;
//            _rhoInc = rhoDiff; //? not sure here
//        } else {
//            _thetaInc = (thetaDiff < 0) ?  -_maxAngle : _maxAngle;
//            _rhoInc = _maxAngle / Math.abs(thetaDiff) * (rhoDiff);
//            _totalSteps = (int)((double)Math.abs(thetaDiff) / _maxAngle);
//        }
        _curStep = 0;
        lineInProgress = true;

    }

    private Coordinate advance() {
        _curTheta += _thetaInc;
        _curRho += _rhoInc;
        _curStep++;
        if (_curStep >= _totalSteps)
        {
            Log.d(TAG, "Finished with line. Current Step: " + _curStep + ", Total Steps: " + _totalSteps);
            lineInProgress = false;
        }
        float xVal = (float)Math.sin(_curTheta) * _curRho * radius;
        float yVal = (float)Math.cos(_curTheta) * _curRho * radius;
        Log.d(TAG, "Eval. Theta: " + _curTheta + ", Rho: " + _curRho + ", X: " + xVal + ", Y: " + yVal);
        return new Coordinate(xVal, yVal);
    }

    @Override
    public boolean isStopped() {
        return !lineInProgress && lineCount >= commands.size();
    }

    @Override
    public void reset() {
        lineCount = 0;
    }

    @Override
    public boolean processSandbotFile(SandBotFile file) {
        return false;
    }

    public boolean processFile(File file) {
        commands.clear();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                commands.add(line);
            }
            Log.d(TAG, "Processed " + commands.size() + " commands from " + file.getAbsolutePath());
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
