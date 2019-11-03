package com.alwaystinkering.sandbot.model.state;

import android.util.Log;

import com.alwaystinkering.sandbot.model.pattern.AbstractPattern;
import com.alwaystinkering.sandbot.model.pattern.FileType;
import com.alwaystinkering.sandbot.model.pattern.ParametricPattern;
import com.alwaystinkering.sandbot.model.pattern.ThetaRhoPattern;
import com.alwaystinkering.sandbot.model.web.FileListResult;
import com.alwaystinkering.sandbot.model.web.SandBotFile;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {

    public interface FileManagerListener {
        void listUpdated();
        void storageSize(long totalBytes, long usedBytes);
    }

    private static final String TAG = "FileManager";
    private static Map<String, AbstractPattern> files = new HashMap<>();

    private static String fileSystemName = "";

    private static long totalBytes;
    private static long usedBytes;

    private static List<FileManagerListener> listeners = new ArrayList<>();

    public static String getFileSystemName() {
        return fileSystemName;
    }

    public static void addListener(FileManagerListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
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

        if (pattern != null) {
            files.put(pattern.getName(), pattern);
        }

        return true;
    }

    public static void processFileList(FileListResult fileListResult) {
        totalBytes = fileListResult.getDiskSize().longValue();
        usedBytes = fileListResult.getDiskUsed().longValue();
        fileSystemName = fileListResult.getFsName();

        for (FileManagerListener l : listeners) {
            l.storageSize(totalBytes, usedBytes);
        }

        for (SandBotFile file : fileListResult.getSandBotFiles()) {
            createPatternFromFile(file);
        }
        for (FileManagerListener l : listeners) {
            l.listUpdated();
        }
    }

    public static long getTotalBytes() {
        return totalBytes;
    }

    public static long getUsedBytes() {
        return usedBytes;
    }

    public static void initializeTestFiles() {
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

                for (FileManagerListener l : listeners) {
                    l.listUpdated();
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

    public static Comparator<AbstractPattern> FILE_NAME_COMPARATOR = new Comparator<AbstractPattern>() {
        @Override
        public int compare(AbstractPattern o1, AbstractPattern o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
}
