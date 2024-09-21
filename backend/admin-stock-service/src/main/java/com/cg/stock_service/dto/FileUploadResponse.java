package com.cg.stock_service.dto;

public class FileUploadResponse {
    private String message;
    private long fileSize;

    public FileUploadResponse(String message, long fileSize) {
        this.message = message;
        this.fileSize = fileSize;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
