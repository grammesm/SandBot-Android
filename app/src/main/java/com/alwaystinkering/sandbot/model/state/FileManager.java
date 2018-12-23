package com.alwaystinkering.sandbot.model.state;

import android.util.Log;

import com.alwaystinkering.sandbot.model.pattern.AbstractPattern;
import com.alwaystinkering.sandbot.model.pattern.FileType;
import com.alwaystinkering.sandbot.model.pattern.ParametricPattern;
import com.alwaystinkering.sandbot.model.pattern.ThetaRhoPattern;
import com.alwaystinkering.sandbot.model.web.SandBotFile;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {

    public interface FileManagerListener {
        void listUpdated();
    }

    private static final String TAG = "FileManager";
    private static Map<String, AbstractPattern> files = new HashMap<>();

    private static String fileSystemName = "";

    public static String getFileSystemName() {
        return fileSystemName;
    }

    public static void setFileSystemName(String fileSystemName) {
        FileManager.fileSystemName = fileSystemName;
    }

    public static boolean createPatternFromFile(SandBotFile sandBotFile) {
        String extension = FilenameUtils.getExtension(sandBotFile.getName());
        FileType fileType = FileType.fromExtension(extension);
        Log.d(TAG, "SandBotFile Type: " + fileType);

        if (fileType == FileType.UNKNOWN) {
            return false;
        }

        AbstractPattern pattern = null;
        switch (fileType) {
            case PARAM:
                pattern = new ParametricPattern(sandBotFile.getName());
                break;
            case GCODE:
                break;
            case THETA_RHO:
                pattern = new ThetaRhoPattern(sandBotFile.getName());
                break;
            case SEQUENCE:
                break;
        }


        return true;
    }

    public static void initializeTestFiles(FileManagerListener listener) {
        String dir = "/sdcard/sand/";
        Log.d(TAG, "Attempting to get files from: " + dir);
        //Log.d(TAG, "SD Card listing: " + Environment.getExternalStorageDirectory().listFiles().toString());
        File directory = new File(dir);
        if (directory.exists() && directory.isDirectory()) {
            for (File f : directory.listFiles()) {
                Log.d(TAG, "Processing file: " + f.getName());
                String extension = FilenameUtils.getExtension(f.getName());
                FileType fileType = FileType.fromExtension(extension);
                Log.d(TAG, "Test FileType: " + fileType);
                AbstractPattern abstractPattern = null;
                switch (fileType) {
                    case PARAM:
                        break;
                    case GCODE:
                        break;
                    case THETA_RHO:
                        abstractPattern = new ThetaRhoPattern(FilenameUtils.removeExtension(f.getName()));
                        break;
                    case SEQUENCE:
                        break;
                    case UNKNOWN:
                        break;
                }

                if (abstractPattern != null) {
                    ((ThetaRhoPattern) abstractPattern).processFile(f);
                    files.put(abstractPattern.getName(), abstractPattern);
                }

                if (listener != null) {
                    listener.listUpdated();
                }
            }
        }
    }

    public static Map<String, AbstractPattern> getFilesMap() {
        return files;
    }

    public static List<AbstractPattern> getFiles() {
        return new ArrayList<>(files.values());
    }
}
