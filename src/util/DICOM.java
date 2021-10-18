/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  ij.IJ
 *  ij.ImagePlus
 *  ij.ImageStack
 *  ij.Prefs
 *  ij.io.FileInfo
 *  ij.io.FileOpener
 *  ij.io.OpenDialog
 *  ij.measure.Calibration
 *  ij.plugin.PlugIn
 *  ij.process.ImageProcessor
 */
package util;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.io.FileInfo;
import ij.io.FileOpener;
import ij.io.OpenDialog;
import ij.measure.Calibration;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import util.DicomDecoder;

public class DICOM
extends ImagePlus
implements PlugIn {
    private boolean showErrors = true;
    private boolean gettingInfo;
    private BufferedInputStream inputStream;
    private String info;

    public DICOM() {
    }

    public DICOM(InputStream is) {
        this(new BufferedInputStream(is));
    }

    public DICOM(BufferedInputStream bis) {
        this.inputStream = bis;
    }

    public void run(String arg) {
        FileInfo fi;
        DicomDecoder dd;
        String fileName;
        block23 : {
            OpenDialog od = new OpenDialog("Open Dicom...", arg);
            String directory = od.getDirectory();
            fileName = od.getFileName();
            if (fileName == null) {
                return;
            }
            dd = new DicomDecoder(directory, fileName);
            dd.inputStream = this.inputStream;
            fi = null;
            try {
                fi = dd.getFileInfo();
            }
            catch (IOException e) {
                String msg = e.getMessage();
                IJ.showStatus((String)"");
                if (msg.indexOf("EOF") < 0 && this.showErrors) {
                    IJ.error((String)"DICOM Reader", (String)(e.getClass().getName() + "\n \n" + msg));
                    return;
                }
                if (dd.dicmFound() || !this.showErrors) break block23;
                msg = "This does not appear to be a valid\nDICOM file. It does not have the\ncharacters 'DICM' at offset 128.";
                IJ.error((String)"DICOM Reader", (String)msg);
                return;
            }
        }
        if (this.gettingInfo) {
            this.info = dd.getDicomInfo();
            return;
        }
        if (fi != null && fi.width > 0 && fi.height > 0 && fi.offset > 0) {
            FileOpener fo = new FileOpener(fi);
            ImagePlus imp = fo.open(false);
            ImageProcessor ip = imp.getProcessor();
            if (Prefs.openDicomsAsFloat) {
                ip = ip.convertToFloat();
                if (dd.rescaleSlope != 1.0) {
                    ip.multiply(dd.rescaleSlope);
                }
                if (dd.rescaleIntercept != 0.0) {
                    ip.add(dd.rescaleIntercept);
                }
                imp.setProcessor(ip);
            } else if (fi.fileType == 1) {
                if (dd.rescaleIntercept != 0.0 && dd.rescaleSlope == 1.0) {
                    ip.add(dd.rescaleIntercept);
                }
            } else if (dd.rescaleIntercept != 0.0 && (dd.rescaleSlope == 1.0 || fi.fileType == 0)) {
                double[] coeff = new double[]{dd.rescaleIntercept, dd.rescaleSlope};
                imp.getCalibration().setFunction(0, coeff, "Gray Value");
            }
            if (dd.windowWidth > 0.0) {
                double min = dd.windowCenter - dd.windowWidth / 2.0;
                double max = dd.windowCenter + dd.windowWidth / 2.0;
                if (Prefs.openDicomsAsFloat) {
                    min -= dd.rescaleIntercept;
                    max -= dd.rescaleIntercept;
                } else {
                    Calibration cal = imp.getCalibration();
                    min = cal.getRawValue(min);
                    max = cal.getRawValue(max);
                }
                ip.setMinAndMax(min, max);
                if (IJ.debugMode) {
                    IJ.log((String)("window: " + min + "-" + max));
                }
            }
            if (imp.getStackSize() > 1) {
                this.setStack(fileName, imp.getStack());
            } else {
                this.setProcessor(fileName, imp.getProcessor());
            }
            this.setCalibration(imp.getCalibration());
            this.setProperty("Info", (Object)dd.getDicomInfo());
            this.setFileInfo(fi);
            if (arg.equals("")) {
                this.show();
            }
        } else if (this.showErrors) {
            IJ.error((String)"DICOM Reader", (String)"Unable to decode DICOM header.");
        }
        IJ.showStatus((String)"");
    }

    public void open(String path) {
        this.showErrors = false;
        this.run(path);
    }

    public String getInfo(String path) {
        this.showErrors = false;
        this.gettingInfo = true;
        this.run(path);
        return this.info;
    }

    void convertToUnsigned(ImagePlus imp, FileInfo fi) {
        int i;
        ImageProcessor ip = imp.getProcessor();
        short[] pixels = (short[])ip.getPixels();
        int min = Integer.MAX_VALUE;
        for (i = 0; i < pixels.length; ++i) {
            int value = pixels[i] & 65535;
            if (value >= min) continue;
            min = value;
        }
        if (IJ.debugMode) {
            IJ.log((String)("min: " + (min - 32768)));
        }
        if (min >= 32768) {
            for (i = 0; i < pixels.length; ++i) {
                pixels[i] = (short)(pixels[i] - 32768);
            }
            ip.resetMinAndMax();
            Calibration cal = imp.getCalibration();
            cal.setFunction(20, null, "Gray Value");
            fi.fileType = 2;
        }
    }
}

