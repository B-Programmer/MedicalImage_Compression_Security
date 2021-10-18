/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  ij.IJ
 *  ij.Prefs
 *  ij.io.FileInfo
 *  ij.util.Tools
 */
package util;

import ij.IJ;
import ij.Prefs;
import ij.io.FileInfo;
import ij.util.Tools;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import util.DicomDictionary;

class DicomDecoder {
    private static final int PIXEL_REPRESENTATION = 2621699;
    private static final int TRANSFER_SYNTAX_UID = 131088;
    private static final int MODALITY = 524384;
    private static final int SLICE_THICKNESS = 1572944;
    private static final int SLICE_SPACING = 1573000;
    private static final int IMAGER_PIXEL_SPACING = 1577316;
    private static final int SAMPLES_PER_PIXEL = 2621442;
    private static final int PHOTOMETRIC_INTERPRETATION = 2621444;
    private static final int PLANAR_CONFIGURATION = 2621446;
    private static final int NUMBER_OF_FRAMES = 2621448;
    private static final int ROWS = 2621456;
    private static final int COLUMNS = 2621457;
    private static final int PIXEL_SPACING = 2621488;
    private static final int BITS_ALLOCATED = 2621696;
    private static final int WINDOW_CENTER = 2625616;
    private static final int WINDOW_WIDTH = 2625617;
    private static final int RESCALE_INTERCEPT = 2625618;
    private static final int RESCALE_SLOPE = 2625619;
    private static final int RED_PALETTE = 2626049;
    private static final int GREEN_PALETTE = 2626050;
    private static final int BLUE_PALETTE = 2626051;
    private static final int ICON_IMAGE_SEQUENCE = 8913408;
    private static final int ITEM = -73728;
    private static final int ITEM_DELIMINATION = -73715;
    private static final int SEQUENCE_DELIMINATION = -73507;
    private static final int PIXEL_DATA = 2145386512;
    private static final int AE = 16709;
    private static final int AS = 16723;
    private static final int AT = 16724;
    private static final int CS = 17235;
    private static final int DA = 17473;
    private static final int DS = 17491;
    private static final int DT = 17492;
    private static final int FD = 17988;
    private static final int FL = 17996;
    private static final int IS = 18771;
    private static final int LO = 19535;
    private static final int LT = 19540;
    private static final int PN = 20558;
    private static final int SH = 21320;
    private static final int SL = 21324;
    private static final int SS = 21331;
    private static final int ST = 21332;
    private static final int TM = 21581;
    private static final int UI = 21833;
    private static final int UL = 21836;
    private static final int US = 21843;
    private static final int UT = 21844;
    private static final int OB = 20290;
    private static final int OW = 20311;
    private static final int SQ = 21329;
    private static final int UN = 21838;
    private static final int QQ = 16191;
    private static Properties dictionary;
    private String directory;
    private String fileName;
    private static final int ID_OFFSET = 128;
    private static final String DICM = "DICM";
    private BufferedInputStream f;
    private int location = 0;
    private boolean littleEndian = true;
    private int elementLength;
    private int vr;
    private static final int IMPLICIT_VR = 11565;
    private byte[] vrLetters = new byte[2];
    private int previousGroup;
    private String previousInfo;
    private StringBuffer dicomInfo = new StringBuffer(1000);
    private boolean dicmFound;
    private boolean oddLocations;
    private boolean bigEndianTransferSyntax = false;
    double windowCenter;
    double windowWidth;
    double rescaleIntercept;
    double rescaleSlope = 1.0;
    boolean inSequence;
    BufferedInputStream inputStream;
    String modality;
    static char[] buf8;
    char[] buf10;

    public DicomDecoder(String directory, String fileName) {
        File f;
        this.directory = directory;
        this.fileName = fileName;
        String path = null;
        if (dictionary == null && IJ.getApplet() == null && (f = new File(path = Prefs.getImageJDir() + "DICOM_Dictionary.txt")).exists()) {
            try {
                dictionary = new Properties();
                BufferedInputStream is = new BufferedInputStream(new FileInputStream(f));
                dictionary.load(is);
                is.close();
                if (IJ.debugMode) {
                    IJ.log((String)("DicomDecoder: using " + dictionary.size() + " tag dictionary at " + path));
                }
            }
            catch (Exception e) {
                dictionary = null;
            }
        }
        if (dictionary == null) {
            DicomDictionary d = new DicomDictionary();
            dictionary = d.getDictionary();
            if (IJ.debugMode) {
                IJ.log((String)("DicomDecoder: " + path + " not found; using " + dictionary.size() + " tag built in dictionary"));
            }
        }
    }

    String getString(int length) throws IOException {
        int count;
        byte[] buf = new byte[length];
        for (int pos = 0; pos < length; pos += count) {
            count = this.f.read(buf, pos, length - pos);
            if (count != -1) continue;
            throw new IOException("unexpected EOF");
        }
        this.location += length;
        return new String(buf);
    }

    String getUNString(int length) throws IOException {
        String s = this.getString(length);
        if (s != null && s.length() > 60) {
            s = s.substring(0, 60);
        }
        return s;
    }

    int getByte() throws IOException {
        int b = this.f.read();
        if (b == -1) {
            throw new IOException("unexpected EOF");
        }
        ++this.location;
        return b;
    }

    int getShort() throws IOException {
        int b0 = this.getByte();
        int b1 = this.getByte();
        if (this.littleEndian) {
            return (b1 << 8) + b0;
        }
        return (b0 << 8) + b1;
    }

    final int getInt() throws IOException {
        int b0 = this.getByte();
        int b1 = this.getByte();
        int b2 = this.getByte();
        int b3 = this.getByte();
        if (this.littleEndian) {
            return (b3 << 24) + (b2 << 16) + (b1 << 8) + b0;
        }
        return (b0 << 24) + (b1 << 16) + (b2 << 8) + b3;
    }

    double getDouble() throws IOException {
        int b0 = this.getByte();
        int b1 = this.getByte();
        int b2 = this.getByte();
        int b3 = this.getByte();
        int b4 = this.getByte();
        int b5 = this.getByte();
        int b6 = this.getByte();
        int b7 = this.getByte();
        long res = 0;
        if (this.littleEndian) {
            res += (long)b0;
            res += (long)b1 << 8;
            res += (long)b2 << 16;
            res += (long)b3 << 24;
            res += (long)b4 << 32;
            res += (long)b5 << 40;
            res += (long)b6 << 48;
            res += (long)b7 << 56;
        } else {
            res += (long)b7;
            res += (long)b6 << 8;
            res += (long)b5 << 16;
            res += (long)b4 << 24;
            res += (long)b3 << 32;
            res += (long)b2 << 40;
            res += (long)b1 << 48;
            res += (long)b0 << 56;
        }
        return Double.longBitsToDouble(res);
    }

    float getFloat() throws IOException {
        int b0 = this.getByte();
        int b1 = this.getByte();
        int b2 = this.getByte();
        int b3 = this.getByte();
        int res = 0;
        if (this.littleEndian) {
            res += b0;
            res = (int)((long)res + ((long)b1 << 8));
            res = (int)((long)res + ((long)b2 << 16));
            res = (int)((long)res + ((long)b3 << 24));
        } else {
            res += b3;
            res = (int)((long)res + ((long)b2 << 8));
            res = (int)((long)res + ((long)b1 << 16));
            res = (int)((long)res + ((long)b0 << 24));
        }
        return Float.intBitsToFloat(res);
    }

    byte[] getLut(int length) throws IOException {
        if ((length & 1) != 0) {
            String dummy = this.getString(length);
            return null;
        }
        byte[] lut = new byte[length /= 2];
        for (int i = 0; i < length; ++i) {
            lut[i] = (byte)(this.getShort() >>> 8);
        }
        return lut;
    }

    int getLength() throws IOException {
        int b0 = this.getByte();
        int b1 = this.getByte();
        int b2 = this.getByte();
        int b3 = this.getByte();
        this.vr = (b0 << 8) + b1;
        switch (this.vr) {
            case 20290: 
            case 20311: 
            case 21329: 
            case 21838: 
            case 21844: {
                if (b2 == 0 || b3 == 0) {
                    return this.getInt();
                }
                this.vr = 11565;
                if (this.littleEndian) {
                    return (b3 << 24) + (b2 << 16) + (b1 << 8) + b0;
                }
                return (b0 << 24) + (b1 << 16) + (b2 << 8) + b3;
            }
            case 16191: 
            case 16709: 
            case 16723: 
            case 16724: 
            case 17235: 
            case 17473: 
            case 17491: 
            case 17492: 
            case 17988: 
            case 17996: 
            case 18771: 
            case 19535: 
            case 19540: 
            case 20558: 
            case 21320: 
            case 21324: 
            case 21331: 
            case 21332: 
            case 21581: 
            case 21833: 
            case 21836: 
            case 21843: {
                if (this.littleEndian) {
                    return (b3 << 8) + b2;
                }
                return (b2 << 8) + b3;
            }
        }
        this.vr = 11565;
        if (this.littleEndian) {
            return (b3 << 24) + (b2 << 16) + (b1 << 8) + b0;
        }
        return (b0 << 24) + (b1 << 16) + (b2 << 8) + b3;
    }

    int getNextTag() throws IOException {
        int groupWord = this.getShort();
        if (groupWord == 2048 && this.bigEndianTransferSyntax) {
            this.littleEndian = false;
            groupWord = 8;
        }
        int elementWord = this.getShort();
        int tag = groupWord << 16 | elementWord;
        this.elementLength = this.getLength();
        if (this.elementLength == 13 && !this.oddLocations) {
            this.elementLength = 10;
        }
        if (this.elementLength == -1) {
            this.elementLength = 0;
            this.inSequence = true;
        }
        return tag;
    }

    FileInfo getFileInfo() throws IOException {
        FileInfo fi = new FileInfo();
        int bitsAllocated = 16;
        fi.fileFormat = 1;
        fi.fileName = this.fileName;
        if (this.directory.indexOf("://") > 0) {
            URL u = new URL(this.directory + this.fileName);
            fi.inputStream = this.inputStream = new BufferedInputStream(u.openStream());
        } else if (this.inputStream != null) {
            fi.inputStream = this.inputStream;
        } else {
            fi.directory = this.directory;
        }
        fi.width = 0;
        fi.height = 0;
        fi.offset = 0;
        fi.intelByteOrder = true;
        fi.fileType = 2;
        fi.fileFormat = 6;
        int samplesPerPixel = 1;
        int planarConfiguration = 0;
        String photoInterpretation = "";
        if (this.inputStream != null) {
            this.f = this.inputStream;
            this.f.mark(400000);
        } else {
            this.f = new BufferedInputStream(new FileInputStream(this.directory + this.fileName));
        }
        if (IJ.debugMode) {
            IJ.log((String)"");
            IJ.log((String)("DicomDecoder: decoding " + this.fileName));
        }
        int[] bytes = new int[128];
        for (int i = 0; i < 128; ++i) {
            bytes[i] = this.getByte();
        }
        if (!this.getString(4).equals("DICM")) {
            if (bytes[0] != 8 && bytes[0] != 2 || bytes[1] != 0 || bytes[3] != 0) {
                throw new IOException("This is not a DICOM or ACR/NEMA file");
            }
            if (this.inputStream == null) {
                this.f.close();
            }
            if (this.inputStream != null) {
                this.f.reset();
            } else {
                this.f = new BufferedInputStream(new FileInputStream(this.directory + this.fileName));
            }
            this.location = 0;
            if (IJ.debugMode) {
                IJ.log((String)"DICM not found at offset 128; reseting to offset 0");
            }
        } else {
            this.dicmFound = true;
            if (IJ.debugMode) {
                IJ.log((String)"DICM found at offset 128");
            }
        }
        boolean decodingTags = true;
        boolean signed = false;
        block24 : while (decodingTags) {
            int tag = this.getNextTag();
            if ((this.location & 1) != 0) {
                this.oddLocations = true;
            }
            if (this.inSequence) {
                this.addInfo(tag, null);
                continue;
            }
            int index=0;
            String s;
            switch (tag) {
                
                case 131088: {
                    s = this.getString(this.elementLength);
                    this.addInfo(tag, s);
                    if (s.indexOf("1.2.4") > -1 || s.indexOf("1.2.5") > -1) {
                        this.f.close();
                        String msg = "ImageJ cannot open compressed DICOM images.\n \n";
                        msg = msg + "Transfer Syntax UID = " + s;
                        throw new IOException(msg);
                    }
                    if (s.indexOf("1.2.840.10008.1.2.2") < 0) continue block24;
                    this.bigEndianTransferSyntax = true;
                    continue block24;
                }
                case 524384: {
                    this.modality = this.getString(this.elementLength);
                    this.addInfo(tag, this.modality);
                    continue block24;
                }
                case 2621448: {
                    s = this.getString(this.elementLength);
                    this.addInfo(tag, s);
                    double frames = this.s2d(s);
                    if (frames <= 1.0) continue block24;
                    fi.nImages = (int)frames;
                    continue block24;
                }
                case 2621442: {
                    samplesPerPixel = this.getShort();
                    this.addInfo(tag, samplesPerPixel);
                    continue block24;
                }
                case 2621444: {
                    photoInterpretation = this.getString(this.elementLength);
                    this.addInfo(tag, photoInterpretation);
                    continue block24;
                }
                case 2621446: {
                    planarConfiguration = this.getShort();
                    this.addInfo(tag, planarConfiguration);
                    continue block24;
                }
                case 2621456: {
                    fi.height = this.getShort();
                    this.addInfo(tag, fi.height);
                    continue block24;
                }
                case 2621457: {
                    fi.width = this.getShort();
                    this.addInfo(tag, fi.width);
                    continue block24;
                }
                case 1577316: 
                case 2621488: {
                    String scale = this.getString(this.elementLength);
                    this.getSpatialScale(fi, scale);
                    this.addInfo(tag, scale);
                    continue block24;
                }
                case 1572944: 
                case 1573000: {
                    String spacing = this.getString(this.elementLength);
                    fi.pixelDepth = this.s2d(spacing);
                    this.addInfo(tag, spacing);
                    continue block24;
                }
                case 2621696: {
                    bitsAllocated = this.getShort();
                    if (bitsAllocated == 8) {
                        fi.fileType = 0;
                    } else if (bitsAllocated == 32) {
                        fi.fileType = 11;
                    }
                    this.addInfo(tag, bitsAllocated);
                    continue block24;
                }
                case 2621699: {
                    int pixelRepresentation = this.getShort();
                    if (pixelRepresentation == 1) {
                        fi.fileType = 1;
                        signed = true;
                    }
                    this.addInfo(tag, pixelRepresentation);
                    continue block24;
                }
                case 2625616: {
                    String center = this.getString(this.elementLength);
                    index = center.indexOf(92);
                    if (index != -1) {
                        center = center.substring(index + 1);
                    }
                    this.windowCenter = this.s2d(center);
                    this.addInfo(tag, center);
                    continue block24;
                }
                case 2625617: {
                    String width = this.getString(this.elementLength);
                    index = width.indexOf(92);
                    if (index != -1) {
                        width = width.substring(index + 1);
                    }
                    this.windowWidth = this.s2d(width);
                    this.addInfo(tag, width);
                    continue block24;
                }
                case 2625618: {
                    String intercept = this.getString(this.elementLength);
                    this.rescaleIntercept = this.s2d(intercept);
                    this.addInfo(tag, intercept);
                    continue block24;
                }
                case 2625619: {
                    String slop = this.getString(this.elementLength);
                    this.rescaleSlope = this.s2d(slop);
                    this.addInfo(tag, slop);
                    continue block24;
                }
                case 2626049: {
                    fi.reds = this.getLut(this.elementLength);
                    this.addInfo(tag, this.elementLength / 2);
                    continue block24;
                }
                case 2626050: {
                    fi.greens = this.getLut(this.elementLength);
                    this.addInfo(tag, this.elementLength / 2);
                    continue block24;
                }
                case 2626051: {
                    fi.blues = this.getLut(this.elementLength);
                    this.addInfo(tag, this.elementLength / 2);
                    continue block24;
                }
                case 2145386512: {
                    if (this.elementLength != 0) {
                        fi.offset = this.location;
                        this.addInfo(tag, this.location);
                        decodingTags = false;
                        continue block24;
                    }
                    this.addInfo(tag, null);
                    continue block24;
                }
                case 2139619344: {
                    if (this.elementLength == 0) continue block24;
                    fi.offset = this.location + 4;
                    decodingTags = false;
                    continue block24;
                }
                default:{
                    
                }
            }
            this.addInfo(tag, null);
        }
        if (fi.fileType == 0 && fi.reds != null && fi.greens != null && fi.blues != null && fi.reds.length == fi.greens.length && fi.reds.length == fi.blues.length) {
            fi.fileType = 5;
            fi.lutSize = fi.reds.length;
        }
        if (fi.fileType == 11 && signed) {
            fi.fileType = 3;
        }
        if (samplesPerPixel == 3 && photoInterpretation.startsWith("RGB")) {
            if (planarConfiguration == 0) {
                fi.fileType = 6;
            } else if (planarConfiguration == 1) {
                fi.fileType = 7;
            }
        } else if (photoInterpretation.endsWith("1 ")) {
            fi.whiteIsZero = true;
        }
        if (!this.littleEndian) {
            fi.intelByteOrder = false;
        }
        if (IJ.debugMode) {
            IJ.log((String)("width: " + fi.width));
            IJ.log((String)("height: " + fi.height));
            IJ.log((String)("images: " + fi.nImages));
            IJ.log((String)("bits allocated: " + bitsAllocated));
            IJ.log((String)("offset: " + fi.offset));
        }
        if (this.inputStream != null) {
            this.f.reset();
        } else {
            this.f.close();
        }
        return fi;
    }

    String getDicomInfo() {
        String s = new String(this.dicomInfo);
        char[] chars = new char[s.length()];
        s.getChars(0, s.length(), chars, 0);
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] >= ' ' || chars[i] == '\n') continue;
            chars[i] = 32;
        }
        return new String(chars);
    }

    void addInfo(int tag, String value) throws IOException {
        String info = this.getHeaderInfo(tag, value);
        if (this.inSequence && info != null && this.vr != 21329) {
            info = ">" + info;
        }
        if (info != null && tag != -73728) {
            int group;
            this.previousGroup = group = tag >>> 16;
            this.previousInfo = info;
            this.dicomInfo.append(this.tag2hex(tag) + info + "\n");
        }
        if (IJ.debugMode) {
            if (info == null) {
                info = "";
            }
            this.vrLetters[0] = (byte)(this.vr >> 8);
            this.vrLetters[1] = (byte)(this.vr & 255);
            String VR = new String(this.vrLetters);
            IJ.log((String)("(" + this.tag2hex(tag) + VR + " " + this.elementLength + " bytes from " + (this.location - this.elementLength) + ") " + info));
        }
    }

    void addInfo(int tag, int value) throws IOException {
        this.addInfo(tag, Integer.toString(value));
    }

    String getHeaderInfo(int tag, String value) throws IOException {
        String key;
        String id;
        if (tag == -73715 || tag == -73507) {
            this.inSequence = false;
            if (!IJ.debugMode) {
                return null;
            }
        }
        if ((id = (String)dictionary.get(key = this.i2hex(tag))) != null) {
            if (this.vr == 11565 && id != null) {
                this.vr = (id.charAt(0) << 8) + id.charAt(1);
            }
            id = id.substring(2);
        }
        if (tag == -73728) {
            return id != null ? id + ":" : null;
        }
        if (value != null) {
            return id + ": " + value;
        }
        switch (this.vr) {
            case 17988: {
                if (this.elementLength == 8) {
                    value = Double.toString(this.getDouble());
                    break;
                }
                for (int i = 0; i < this.elementLength; ++i) {
                    this.getByte();
                }
                break;
            }
            case 17996: {
                if (this.elementLength == 4) {
                    value = Float.toString(this.getFloat());
                    break;
                }
                for (int i = 0; i < this.elementLength; ++i) {
                    this.getByte();
                }
                break;
            }
            case 16709: 
            case 16723: 
            case 16724: 
            case 17235: 
            case 17473: 
            case 17491: 
            case 17492: 
            case 18771: 
            case 19535: 
            case 19540: 
            case 20558: 
            case 21320: 
            case 21332: 
            case 21581: 
            case 21833: {
                value = this.getString(this.elementLength);
                break;
            }
            case 21838: {
                value = this.getUNString(this.elementLength);
                break;
            }
            case 21843: {
                if (this.elementLength == 2) {
                    value = Integer.toString(this.getShort());
                    break;
                }
                int n = this.elementLength / 2;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < n; ++i) {
                    sb.append(Integer.toString(this.getShort()));
                    sb.append(" ");
                }
                value = sb.toString();
                break;
            }
            case 11565: {
                value = this.getString(this.elementLength);
                if (this.elementLength <= 44) break;
                value = null;
                break;
            }
            case 21329: {
                boolean privateTag;
                value = "";
                boolean bl = privateTag = (tag >> 16 & 1) != 0;
                if (tag != 8913408 && !privateTag) break;
            }
            default: {
                for (long skipCount = (long)this.elementLength; skipCount > 0; skipCount -= this.f.skip((long)skipCount)) {
                }
                this.location += this.elementLength;
                value = "";
            }
        }
        if (value != null && id == null && !value.equals("")) {
            return "---: " + value;
        }
        if (id == null) {
            return null;
        }
        return id + ": " + value;
    }

    String i2hex(int i) {
        for (int pos = 7; pos >= 0; --pos) {
            DicomDecoder.buf8[pos] = Tools.hexDigits[i & 15];
            i >>>= 4;
        }
        return new String(buf8);
    }

    String tag2hex(int tag) {
        if (this.buf10 == null) {
            this.buf10 = new char[11];
            this.buf10[4] = 44;
            this.buf10[9] = 32;
        }
        int pos = 8;
        while (pos >= 0) {
            this.buf10[pos] = Tools.hexDigits[tag & 15];
            tag >>>= 4;
            if (--pos != 4) continue;
            --pos;
        }
        return new String(this.buf10);
    }

    double s2d(String s) {
        Double d;
        if (s == null) {
            return 0.0;
        }
        if (s.startsWith("\\")) {
            s = s.substring(1);
        }
        try {
            d = new Double(s);
        }
        catch (NumberFormatException e) {
            d = null;
        }
        if (d != null) {
            return d;
        }
        return 0.0;
    }

    void getSpatialScale(FileInfo fi, String scale) {
        double xscale = 0.0;
        double yscale = 0.0;
        int i = scale.indexOf(92);
        if (i > 0) {
            yscale = this.s2d(scale.substring(0, i));
            xscale = this.s2d(scale.substring(i + 1));
        }
        if (xscale != 0.0 && yscale != 0.0) {
            fi.pixelWidth = xscale;
            fi.pixelHeight = yscale;
            fi.unit = "mm";
        }
    }

    boolean dicmFound() {
        return this.dicmFound;
    }

    static {
        buf8 = new char[8];
    }
}

