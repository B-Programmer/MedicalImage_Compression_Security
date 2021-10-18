/*
 * Decompiled with CFR 0_110.
 */
package model;

import model.MedicalImage;

public class CompressedMedicalImage {
    private boolean lzwUsed;
    private boolean bchUsed;
    private Double newFileSizeAfterLZW;
    private String compressionTimeAfterLZW;
    private String compressionRatioAfterLZW;
    private Double newFileSizeAfterBCH;
    private String compressionTimeAfterBCH;
    private String compressionRatioAfterBCH;
    private MedicalImage acquiredImage;
    private String compressedImagePath;

    public MedicalImage getAcquiredImage() {
        return this.acquiredImage;
    }

    public void setAcquiredImage(MedicalImage acquiredImage) {
        this.acquiredImage = acquiredImage;
    }

    public boolean isLzwUsed() {
        return this.lzwUsed;
    }

    public void setLzwUsed(boolean lzwUsed) {
        this.lzwUsed = lzwUsed;
    }

    public boolean isBchUsed() {
        return this.bchUsed;
    }

    public void setBchUsed(boolean bchUsed) {
        this.bchUsed = bchUsed;
    }

    public Double getNewFileSizeAfterLZW() {
        return this.newFileSizeAfterLZW;
    }

    public void setNewFileSizeAfterLZW(Double newFileSizeAfterLZW) {
        this.newFileSizeAfterLZW = newFileSizeAfterLZW;
    }

    public String getCompressionTimeAfterLZW() {
        return this.compressionTimeAfterLZW;
    }

    public void setCompressionTimeAfterLZW(String compressionTimeAfterLZW) {
        this.compressionTimeAfterLZW = compressionTimeAfterLZW;
    }

    public String getCompressionRatioAfterLZW() {
        return this.compressionRatioAfterLZW;
    }

    public void setCompressionRatioAfterLZW(String compressionRatioAfterLZW) {
        this.compressionRatioAfterLZW = compressionRatioAfterLZW;
    }

    public Double getNewFileSizeAfterBCH() {
        return this.newFileSizeAfterBCH;
    }

    public void setNewFileSizeAfterBCH(Double newFileSizeAfterBCH) {
        this.newFileSizeAfterBCH = newFileSizeAfterBCH;
    }

    public String getCompressionTimeAfterBCH() {
        return this.compressionTimeAfterBCH;
    }

    public void setCompressionTimeAfterBCH(String compressionTimeAfterBCH) {
        this.compressionTimeAfterBCH = compressionTimeAfterBCH;
    }

    public String getCompressionRatioAfterBCH() {
        return this.compressionRatioAfterBCH;
    }

    public void setCompressionRatioAfterBCH(String compressionRatioAfterBCH) {
        this.compressionRatioAfterBCH = compressionRatioAfterBCH;
    }

    public String getCompressedImagePath() {
        return this.compressedImagePath;
    }

    public void setCompressedImagePath(String compressedImagePath) {
        this.compressedImagePath = compressedImagePath;
    }
}

