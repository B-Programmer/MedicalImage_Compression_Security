/*
 * Decompiled with CFR 0_110.
 */
package model;

public class MedicalImage {
    private String fileName;
    private String fileExtension;
    private String filePath;
    private long fileSize;
    private String acquisitionTime;

    public boolean isValid() {
        boolean isValid = true;
        if (this.fileName.isEmpty()) {
            isValid = false;
        }
        if (this.filePath.isEmpty()) {
            isValid = false;
        }
        String extension = this.fileExtension;
        if (extension.equalsIgnoreCase("dic") || extension.equalsIgnoreCase("dcm") | extension.equalsIgnoreCase("dicm") || extension.equalsIgnoreCase("dicom")) {
            isValid = true;
        }
       if (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpeg") | extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("bmp")) {
            isValid = false;
       }
       else {
           isValid = false;
       }
        return isValid;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return this.fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getAcquisitionTime() {
        return this.acquisitionTime;
    }

    public void setAcquisitionTime(String acquisitionTime) {
        this.acquisitionTime = acquisitionTime;
    }
}

