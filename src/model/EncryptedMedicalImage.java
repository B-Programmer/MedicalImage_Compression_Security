/*
 * Decompiled with CFR 0_110.
 */
package model;

import model.CompressedMedicalImage;

public class EncryptedMedicalImage {
    private CompressedMedicalImage compressedImage;
    private String encryptionTime;
    private String imageStatus;

    public CompressedMedicalImage getCompressedImage() {
        return this.compressedImage;
    }

    public void setCompressedImage(CompressedMedicalImage compressedImage) {
        this.compressedImage = compressedImage;
    }

    public String getEncryptionTime() {
        return this.encryptionTime;
    }

    public void setEncryptionTime(String encryptionTime) {
        this.encryptionTime = encryptionTime;
    }

    public String getImageStatus() {
        return this.imageStatus;
    }

    public void setImageStatus(String imageStatus) {
        this.imageStatus = imageStatus;
    }
}

