package com.alwaystinkering.sandbot.model.web;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FileListResult {
    @SerializedName("rslt")
    @Expose
    private String rslt;
    @SerializedName("fsName")
    @Expose
    private String fsName;
    @SerializedName("fsBase")
    @Expose
    private String fsBase;
    @SerializedName("diskSize")
    @Expose
    private Double diskSize;
    @SerializedName("diskUsed")
    @Expose
    private Double diskUsed;
    @SerializedName("folder")
    @Expose
    private String folder;
    @SerializedName("files")
    @Expose
    private List<SandBotFile> sandBotFiles = null;

    public String getRslt() {
        return rslt;
    }

    public void setRslt(String rslt) {
        this.rslt = rslt;
    }

    public String getFsName() {
        return fsName;
    }

    public void setFsName(String fsName) {
        this.fsName = fsName;
    }

    public String getFsBase() {
        return fsBase;
    }

    public void setFsBase(String fsBase) {
        this.fsBase = fsBase;
    }

    public Double getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(Double diskSize) {
        this.diskSize = diskSize;
    }

    public Double getDiskUsed() {
        return diskUsed;
    }

    public void setDiskUsed(Double diskUsed) {
        this.diskUsed = diskUsed;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public List<SandBotFile> getSandBotFiles() {
        return sandBotFiles;
    }

    public void setSandBotFiles(List<SandBotFile> sandBotFiles) {
        this.sandBotFiles = sandBotFiles;
    }

    @Override
    public String toString() {
        return "FileListResult{" +
                "rslt='" + rslt + '\'' +
                ", fsName='" + fsName + '\'' +
                ", fsBase='" + fsBase + '\'' +
                ", diskSize=" + diskSize +
                ", diskUsed=" + diskUsed +
                ", folder='" + folder + '\'' +
                ", sandBotFiles=" + sandBotFiles +
                '}';
    }
}
