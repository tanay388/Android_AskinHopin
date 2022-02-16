package com.askinhopin.app;

public class BatchModel {
    int icon;
    String batchType, batchDesc, batchId;

    public BatchModel() {
    }

    public BatchModel(int icon, String batchType, String batchDesc, String batchId) {
        this.icon = icon;
        this.batchType = batchType;
        this.batchDesc = batchDesc;
        this.batchId = batchId;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBatchType() {
        return batchType;
    }

    public void setBatchType(String batchType) {
        this.batchType = batchType;
    }

    public String getBatchDesc() {
        return batchDesc;
    }

    public void setBatchDesc(String batchDesc) {
        this.batchDesc = batchDesc;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
}
