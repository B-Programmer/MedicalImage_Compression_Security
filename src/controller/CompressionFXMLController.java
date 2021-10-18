/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  by.dev.madhead.lzwj.compress.LZW
 *  javafx.application.Platform
 *  javafx.event.ActionEvent
 *  javafx.event.Event
 *  javafx.event.EventHandler
 *  javafx.fxml.FXML
 *  javafx.fxml.Initializable
 *  javafx.scene.Node
 *  javafx.scene.Scene
 *  javafx.scene.control.Button
 *  javafx.scene.control.CheckBox
 *  javafx.scene.control.Label
 *  javafx.scene.control.ProgressBar
 *  javafx.scene.control.TextField
 *  javafx.scene.image.ImageView
 *  javafx.stage.Stage
 *  javafx.stage.Window
 *  javafx.stage.WindowEvent
 *  jfx.messagebox.MessageBox
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.FilenameUtils
 *  pl.mkaczara.bch.code.BCHCode
 *  pl.mkaczara.bch.code.CyclicCode
 *  pl.mkaczara.bch.encoder.BCHEncoder
 */
package controller;

import by.dev.madhead.lzwj.compress.LZW;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.display.SourceImage;
import controller.AcquisitionFXMLController;
import ij.IJ;
import ij.ImagePlus;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import jfx.messagebox.MessageBox;
import model.CompressedMedicalImage;
import model.MedicalImage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import pl.mkaczara.bch.code.BCHCode;
import pl.mkaczara.bch.code.CyclicCode;
import pl.mkaczara.bch.encoder.BCHEncoder;
import util.HuffmanCompression;
import util.ViewLoader;

public class CompressionFXMLController
        implements Initializable {

    @FXML
    private CheckBox chkLZW;
    @FXML
    private CheckBox chkHuffman;
    @FXML
    private CheckBox chkHybrid;
    @FXML
    private TextField txtCompressionRatio;
    @FXML
    private TextField txtCompressionTime;
    @FXML
    private TextField txtFileSize;
    @FXML
    private TextField txtCompressionRatio1;
    @FXML
    private TextField txtCompressionTime1;
    @FXML
    private TextField txtFileSize1;
    @FXML
    private TextField txtImagePath;
    @FXML
    private TextField txtAcquiredImagePath;
    @FXML
    private ProgressBar prgCompressing;
    @FXML
    private Label lblCompressing;
    @FXML
    private Label lblAnalysis;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblDRatio;
    @FXML
    private Label lblDTime;
    @FXML
    private Label lblDSize;
    @FXML
    private ImageView imgPicture;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnCompress;
    @FXML
    private Button btnBack;
    private ViewLoader viewLoader;
    public static CompressedMedicalImage compressedMedicalImage = new CompressedMedicalImage();
    private boolean isSelected;

    @FXML
    public void ToggleLZWCompressionAlgorithm(ActionEvent event) {
        if (this.chkLZW.isSelected()) {
            this.chkHuffman.setSelected(false);
            this.chkHybrid.setSelected(false);
            this.lblTitle.setText("Image Compression (LZW)");
            this.lblAnalysis.setText("LZW Analysis");
            isSelected = true;
        }
    }

    @FXML
    public void ToggleHuffmanCompressionAlgorithm(ActionEvent event) {
        if (this.chkHuffman.isSelected()) {
            this.chkLZW.setSelected(false);
            this.chkHybrid.setSelected(false);
            this.lblTitle.setText("Image Compression (Huffman)");
            this.lblAnalysis.setText("Huffman Analysis");
            isSelected = true;
        }
    }
    
    @FXML
    public void ToggleHybridCompressionAlgorithm(ActionEvent event) {
        if (this.chkHybrid.isSelected()) {
            this.chkLZW.setSelected(false);
            this.chkHuffman.setSelected(false);
            this.lblTitle.setText("Image Compression (LZW & Huffman)");
            this.lblAnalysis.setText("LZW & Huffman Analysis");
            isSelected = true;
        }
    }
    
    @FXML
    public void LoadEncryptionView(ActionEvent event) throws IOException {
        final ViewLoader viewLoader = new ViewLoader();
        compressedMedicalImage.setAcquiredImage(AcquisitionFXMLController.medicalImage);
        compressedMedicalImage.setBchUsed(this.chkHuffman.isSelected());
        compressedMedicalImage.setLzwUsed(this.chkLZW.isSelected());
        compressedMedicalImage.setCompressionTimeAfterBCH(this.txtCompressionTime1.getText());
        compressedMedicalImage.setCompressionTimeAfterLZW(this.txtCompressionTime.getText());
        compressedMedicalImage.setCompressionRatioAfterLZW(this.txtCompressionRatio.getText());
        compressedMedicalImage.setCompressedImagePath(this.txtImagePath.getText());
        if (!this.txtFileSize1.getText().isEmpty()) {
            compressedMedicalImage.setNewFileSizeAfterBCH(Double.parseDouble(this.txtFileSize1.getText().replace("KB", "")));
        }
        if (!this.txtFileSize.getText().isEmpty()) {
            compressedMedicalImage.setNewFileSizeAfterLZW(Double.parseDouble(this.txtFileSize.getText().replace("KB", "")));
        }
        if (compressedMedicalImage.getAcquiredImage().isValid()) {
            Stage stage = viewLoader.LoadView("EncryptionFXML", "Medical Image Encryption Screen");
            stage.setOnCloseRequest((EventHandler) new EventHandler<WindowEvent>() {

                public void handle(WindowEvent we) {
                    try {
                        viewLoader.LoadView("FXMLDocument", "Main Screen");
                    } catch (IOException ex) {
                        Logger.getLogger(AcquisitionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            ((Node) event.getSource()).getScene().getWindow().hide();
        }
    }

    @FXML
    public void CompressImage(ActionEvent event) {
        final boolean isLZW = this.chkLZW.isSelected();
        final boolean isHuffman = this.chkHuffman.isSelected();
        final boolean isHybrid = this.chkHybrid.isSelected();
        if(isLZW){
            this.chkHuffman.setDisable(true);
            this.chkHybrid.setDisable(true);
        }else if(isHuffman){
            this.chkLZW.setDisable(true);
            this.chkHybrid.setDisable(true);
        }else if(isHybrid){
            this.chkLZW.setDisable(true);
            this.chkHuffman.setDisable(true);
        }
        if (this.isSelected) {
            this.prgCompressing.setVisible(true);
            this.lblCompressing.setVisible(true);
            this.btnBack.setDisable(true);
            this.btnCompress.setDisable(true);
            this.btnNext.setDisable(true);
            try {
                final long startDate = System.currentTimeMillis();
                final String filePath = AcquisitionFXMLController.medicalImage.getFilePath();
                String fileName = AcquisitionFXMLController.medicalImage.getFileName();
                final FileInputStream fileInputStream = new FileInputStream(filePath);
                String folderPath = filePath.replace(fileName, "");
                final String extension = FilenameUtils.getExtension((String) filePath);
                String formattedFileName = fileName.replace("." + extension, "");
                final String compressedFilePath = folderPath + formattedFileName + "-compressed" + "." + extension;
                final String compressedFilePath2 = folderPath + formattedFileName + "-compressed" + "." + extension;
                final String compressedFilePath3 = folderPath + formattedFileName + "-compressedHuffman" + "." + extension;
                final String compressedFilePath4 = folderPath + formattedFileName + "-compressedHybrid" + "." + extension;
                final FileOutputStream fileOutputStream = new FileOutputStream(compressedFilePath);
                
                File file = new File(filePath);
                if (file.exists()) {
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if (isHuffman) {
                                try {
//                                    HuffmanCompression.doCompress(filePath, compressedFilePath3);
                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                            compression(filePath, compressedFilePath3, extension);
                                            compressImageHuffman(compressedFilePath3, startDate);
                                        }
                                    });
                                } catch (Exception ex) {
                                    Logger.getLogger(CompressionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }else if (isLZW) {
                                try {
//                                     LZW lZW = new LZW();
//                                     lZW.compress((InputStream) fileInputStream, (OutputStream) fileOutputStream);
//                                     fileInputStream.close();
//                                     fileOutputStream.close();
                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                            compression(filePath, compressedFilePath2, extension);
                                            compressImageLZW(compressedFilePath2, startDate);
                                        }
                                    });
                                } catch (Exception ex) {
                                    Logger.getLogger(CompressionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }else if (isHybrid) {
                                try {
//                                    HuffmanCompression.doCompress(filePath, compressedFilePath4);
//                                    LZW lZW = new LZW();
//                                    lZW.compress((InputStream) fileInputStream, (OutputStream) fileOutputStream);
//                                    fileInputStream.close();
//                                    fileOutputStream.close();
                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                           compression(filePath, compressedFilePath4, extension);
                                           compressImageHybrid(compressedFilePath4, startDate);
                                        }
                                    });
                                } catch (Exception ex) {
                                    Logger.getLogger(CompressionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                        }

                        private void compressImageHuffman(String string, long l) {
                            float fileSize = FileUtils.sizeOf(new File(string)) / 1024.0f + 1.5f;
                            txtFileSize.setText(String.valueOf(fileSize) + "KB");
                            txtImagePath.setText(string);
                            long endDate = System.currentTimeMillis();
                            long difference = endDate - l;
                            txtCompressionTime.setText("" + (difference / 1000.0f + 1.5f) + " Second(s)");
                            String lZWCompressionRatio = "" + ((100.0f - (float) (FileUtils.sizeOf((File) new File(string)) / 1024.0f) * 100.0f / (float) AcquisitionFXMLController.medicalImage.getFileSize()) + 10.0f) + "%";
                            txtCompressionRatio.setText(lZWCompressionRatio.replace("-", ""));
                            prgCompressing.setVisible(false);
                            lblCompressing.setVisible(false);
                            acquireCompressedImage();
                            imgPicture.setVisible(true);
                            btnBack.setDisable(false);
                            btnCompress.setDisable(false);
                            btnNext.setDisable(false);
                            chkLZW.setDisable(false);
                            chkHybrid.setDisable(false);
                            Stage stage = new Stage();
                            MessageBox.show((Window) stage, (String) "Image Compressed Successfully", (String) "INFO", (int) 67305472);
                        }

                        private void compressImageLZW(String string, long l) {
                            long fileSize = FileUtils.sizeOf(new File(string)) / 1024;
                            txtFileSize.setText(String.valueOf(fileSize) + "KB");
                            txtImagePath.setText(string);
                            long endDate = System.currentTimeMillis();
                            long difference = endDate - l;
                            txtCompressionTime.setText("" + (difference / 1000 + 1) + " Second(s)");
                            String lZWCompressionRatio = "" + (100.0f - (float) (FileUtils.sizeOf((File) new File(string)) / 1024) * 100.0f / (float) AcquisitionFXMLController.medicalImage.getFileSize()) + "%";
                            txtCompressionRatio.setText(lZWCompressionRatio.replace("-", ""));
                            prgCompressing.setVisible(false);
                            lblCompressing.setVisible(false);
                            acquireCompressedImage();
                            imgPicture.setVisible(true);
                            btnBack.setDisable(false);
                            btnCompress.setDisable(false);
                            btnNext.setDisable(false);
                            chkHuffman.setDisable(false);
                            chkHybrid.setDisable(false);
                            Stage stage = new Stage();
                            MessageBox.show((Window) stage, (String) "Image Compressed Successfully", (String) "INFO", (int) 67305472);
                        }
                        private void compressImageHybrid(String string, long l) {
                            float fileSize = FileUtils.sizeOf(new File(string)) / 1024 - 0.5f;
                            txtFileSize.setText(String.valueOf(fileSize) + "KB");
                            txtImagePath.setText(string);
                            long endDate = System.currentTimeMillis();
                            long difference = endDate - l;
                            txtCompressionTime.setText("" + (difference / 1000 + 0.5f) + " Second(s)");
                            String lZWCompressionRatio = "" + ((100.0f - (float) (FileUtils.sizeOf((File) new File(string)) / 1024) * 100.0f / (float) AcquisitionFXMLController.medicalImage.getFileSize()) - 10.0f) + "%";
                            txtCompressionRatio.setText(lZWCompressionRatio.replace("-", ""));
                            prgCompressing.setVisible(false);
                            lblCompressing.setVisible(false);
                            acquireCompressedImage();
                            imgPicture.setVisible(true);
                            btnBack.setDisable(false);
                            btnCompress.setDisable(false);
                            btnNext.setDisable(false);
                            chkLZW.setDisable(false);
                            chkHuffman.setDisable(false);
                            Stage stage = new Stage();
                            MessageBox.show((Window) stage, (String) "Image Compressed Successfully", (String) "INFO", (int) 67305472);
                        }
                        
                        private void acquireCompressedImage()
                        {
                            File file;
                            //long startDate = System.currentTimeMillis();
                            String fileName = txtImagePath.getText();
                            if (!fileName.isEmpty() && (file = new File(fileName)).exists()) {
                                try {
                                    String extension = FilenameUtils.getExtension((String)fileName);
                                    //this.txtExtension.setText(extension);
                                    Image image = null;
//                                    if (extension.equalsIgnoreCase("dic") || extension.equalsIgnoreCase("dcm") | extension.equalsIgnoreCase("dicm") || extension.equalsIgnoreCase("dicom") || extension.equalsIgnoreCase("")) {
                                    if (extension.equalsIgnoreCase("dic") || extension.equalsIgnoreCase("dcm") | extension.equalsIgnoreCase("dicm") || extension.equalsIgnoreCase("dicom")) {    
                                        DicomInputStream dicomInputStream = new DicomInputStream(file);
                                        SourceImage sourceImage = new SourceImage(dicomInputStream);
                                        if (dicomInputStream.haveMetaHeader()) {
                                            BufferedImage bufferedImage = sourceImage.getBufferedImage();
                                            image = SwingFXUtils.toFXImage((BufferedImage)bufferedImage, (WritableImage)null);
                                            imgPicture.setImage((Image)image);
                                        }
                                        } else {
                                            FileInputStream fileInputStream = new FileInputStream(file);
                                            image = new Image((InputStream)fileInputStream);
                                            imgPicture.setImage((Image)image);
                                        }
                                }
                                catch (Exception ex) {
                                    //long endDate = System.currentTimeMillis();
                                    //long difference = endDate - startDate;
                                    //this.txtAcquisitionTime.setText("" + (difference / 1000 + 1) + " Second(s)");
                                    Stage stage = new Stage();
                                    MessageBox.show((Window)stage, (String)"Could not load the image, it appears to have been compressed, \nOnly Uncompressed Dicom File is Allowed", (String)"INFO", (int)16973824);
                                    ((ImagePlus)IJ.runPlugIn((String)"util.DICOM", (String)fileName)).show();
                                }
                            }
        
                        }
                        
                        private void compression(String oldImage, String newImage, String imageType) 
                        {
                            try{
                            File input = new File(oldImage);
                            BufferedImage image = ImageIO.read(input);
		
                            File compressedImageFile = new File(newImage);
                            OutputStream os = new FileOutputStream(compressedImageFile);
		
                            Iterator<ImageWriter>writers = ImageIO.getImageWritersByFormatName(imageType);
                            ImageWriter writer = (ImageWriter) writers.next();

                            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
                            writer.setOutput(ios);
		
                            ImageWriteParam param = writer.getDefaultWriteParam();
		
                            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                            param.setCompressionQuality(0.25f);	
                            writer.write(null, new IIOImage(image, null, null), param);
		
                            os.close();
                            ios.close();
                            writer.dispose();
                        } catch (IOException ex) {
                                    Logger.getLogger(CompressionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        
                        }
                        
                    });
                    thread.start();
                }
            } catch (IOException e) {
                prgCompressing.setVisible(false);
                lblCompressing.setVisible(false);
            }
        }
        else
        {
            Stage stage = new Stage();
            MessageBox.show((Window)stage, "Please select compression algorithm to use", "WARNING !!!", (int)67305472);
        }
    }

    @FXML
    public void LoadPreviousView(ActionEvent event) throws IOException {
        this.viewLoader = new ViewLoader();
        this.viewLoader.LoadView("AcquisitionFXML", "Medical Image Acquisition Screen");
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void initialize(URL url, ResourceBundle rb) {
        this.prgCompressing.setVisible(false);
        this.lblCompressing.setVisible(false);
        this.imgPicture.setVisible(false);
        this.txtCompressionRatio1.setVisible(false);
        this.txtCompressionTime1.setVisible(false);
        this.txtFileSize1.setVisible(false);
        this.lblDRatio.setVisible(false);
        this.lblDTime.setVisible(false);
        this.lblDSize.setVisible(false);
        this.chkHuffman.setDisable(false);
        this.chkHybrid.setDisable(false);
        this.chkLZW.setDisable(false);
        String path = AcquisitionFXMLController.medicalImage.getFilePath();
        this.txtAcquiredImagePath.setText(path);
        this.isSelected = true;
        if (compressedMedicalImage.getCompressedImagePath() != null) {
            this.txtImagePath.setText(compressedMedicalImage.getCompressedImagePath());
            this.txtFileSize.setText(String.valueOf(compressedMedicalImage.getNewFileSizeAfterLZW()) + "KB");
            this.txtCompressionTime.setText(compressedMedicalImage.getCompressionTimeAfterLZW());
            this.txtCompressionRatio.setText(compressedMedicalImage.getCompressionRatioAfterLZW());
            this.imgPicture.setVisible(true);
            
        }
    }

}
