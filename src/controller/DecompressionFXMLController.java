/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  by.dev.madhead.lzwj.compress.LZW
 *  com.pixelmed.dicom.DicomInputStream
 *  com.pixelmed.display.SourceImage
 *  ij.IJ
 *  ij.ImagePlus
 *  javafx.application.Platform
 *  javafx.embed.swing.SwingFXUtils
 *  javafx.event.ActionEvent
 *  javafx.fxml.FXML
 *  javafx.fxml.Initializable
 *  javafx.scene.control.CheckBox
 *  javafx.scene.control.Label
 *  javafx.scene.control.ProgressBar
 *  javafx.scene.control.TextField
 *  javafx.scene.image.Image
 *  javafx.scene.image.ImageView
 *  javafx.scene.image.WritableImage
 *  javafx.stage.Stage
 *  javafx.stage.Window
 *  jfx.messagebox.MessageBox
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.FilenameUtils
 */
package controller;

import by.dev.madhead.lzwj.compress.LZW;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.display.SourceImage;
import controller.CompressionFXMLController;
import controller.RAcquisitionFXMLController;
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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.stage.Window;
import jfx.messagebox.MessageBox;
import model.MedicalImage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class DecompressionFXMLController
implements Initializable {
    @FXML
    private CheckBox chkLZW;
    @FXML
    private CheckBox chkBCH;
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
    private TextField txtDecryptedImagePath;
    @FXML
    private TextField txtFileSize1;
    @FXML
    private TextField txtImagePath;
    @FXML
    private ProgressBar prgCompressing;
    @FXML
    private Label lblCompressing;
    @FXML
    private Label lblBCH;
    @FXML
    private Label lblDRatio;
    @FXML
    private Label lblDTime;
    @FXML
    private Label lblDSize;
    @FXML
    private ImageView imgPicture;

    @FXML
    public void DecompressImage(ActionEvent event) {
        this.prgCompressing.setVisible(true);
        this.lblCompressing.setVisible(true);
        try {
            final String filePath = RAcquisitionFXMLController.medicalImage.getFilePath();
            String fileName = FilenameUtils.getName((String)filePath);
            String folderPath = filePath.replace(fileName, "");
            String extension = FilenameUtils.getExtension((String)filePath);
            String formattedFileName = fileName.replace("." + extension, "");
            final String compressedFilePathLZW = folderPath + formattedFileName + "-decompressed" + "." + extension;
            final String compressedFilePathLZW2 = folderPath + formattedFileName + "." + extension;
            File file = new File(filePath);
            if (file.exists()) {
                Thread thread = new Thread(new Runnable(){

                    @Override
                    public void run() {
                        try {
                            final long startDate2 = System.currentTimeMillis();
                            FileInputStream fileInputStream = new FileInputStream(filePath);
                            FileOutputStream fileOutputStream = new FileOutputStream(compressedFilePathLZW);
//                            LZW lZW = new LZW();
////                            lZW.decompress((InputStream)fileInputStream, (OutputStream)fileOutputStream);
                            fileInputStream.close();
                            fileOutputStream.close();
                            Platform.runLater(new Runnable() {
                                public void run() {
//                                    System.out.println("decompressed1");
                                    lambda$run$3(startDate2, compressedFilePathLZW2);
                                   }
                            });
                        }
                        catch (IOException ex) {
                            Logger.getLogger(CompressionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }

                    private /* synthetic */ void lambda$run$3(long l, String string) {
//                        System.out.println("decompressed2");
                        long endDate = System.currentTimeMillis();
                        long difference = endDate - l;
                        DecompressionFXMLController.this.txtCompressionTime.setText("" + (difference / 1000 + 1) + " Second(s)");
                        long fileSize = FileUtils.sizeOf((File)new File(string)) / 1024;
                        DecompressionFXMLController.this.txtFileSize.setText(String.valueOf(fileSize) + "KB");
                        DecompressionFXMLController.this.txtImagePath.setText(string);
                        String lZWCompressionRatio = "" + 100.0f * ((float)RAcquisitionFXMLController.medicalImage.getFileSize() / ((float)(FileUtils.sizeOf((File)new File(string)) / 1024) * 100.0f)) + "%";
                        DecompressionFXMLController.this.txtCompressionRatio.setText(lZWCompressionRatio.replace("-", ""));
                        DecompressionFXMLController.this.prgCompressing.setVisible(false);
                        DecompressionFXMLController.this.lblCompressing.setVisible(false);
                        DecompressionFXMLController.this.imgPicture.setVisible(true);
                        DecompressionFXMLController.this.AcquireImage();
                        Stage stage = new Stage();
                        MessageBox.show((Window)stage, (String)"Image Decompressed Successfully", (String)"INFO", (int)67305472);
                    }
                });
                thread.start();
            }
        }
        catch (Exception e) {
            // empty catch block
            Logger.getLogger(DecompressionFXMLController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void AcquireImage() {
        Platform.runLater(new Runnable() {public void run() {
            File file;
            String fileName = txtImagePath.getText();
            String fileImage = "";
            if((fileName.indexOf("-") != 0))
            {
                fileImage = fileName.substring(0, fileName.indexOf("-"));    
//                System.out.println("Image File" +fileImage);
            }
            if (!fileName.isEmpty() && (file = new File(fileName)).exists()) {
                try {
                    String extension = FilenameUtils.getExtension((String)fileName);
                    fileImage = fileImage + "." + extension;
                    Image image = null;
                    if (extension.equalsIgnoreCase("dic") || extension.equalsIgnoreCase("dcm") | extension.equalsIgnoreCase("dicm") || extension.equalsIgnoreCase("dicom") || extension.equalsIgnoreCase("")) {
//                        DicomInputStream dicomInputStream = new DicomInputStream(file);
                        DicomInputStream dicomInputStream = new DicomInputStream(new File(fileImage));
                        SourceImage sourceImage = new SourceImage(dicomInputStream);
                        if (dicomInputStream.haveMetaHeader()) {
                            BufferedImage bufferedImage = sourceImage.getBufferedImage();
                            image = SwingFXUtils.toFXImage((BufferedImage)bufferedImage, (WritableImage)null);
                            imgPicture.setImage((Image)image);
                        }
                    } else {
//                        FileInputStream fileInputStream = new FileInputStream(file);
                        FileInputStream fileInputStream = new FileInputStream(new File(fileImage));
                        image = new Image((InputStream)fileInputStream);
                        imgPicture.setImage((Image)image);
                    }
                }
                catch (Exception ex) {
                    Stage stage = new Stage();
                    MessageBox.show((Window)stage, (String)"Could not load the image, it appears to have been compressed, \nOnly Uncompressed Dicom File is Allowed", (String)"INFO", (int)16973824);
                    ((ImagePlus)IJ.runPlugIn((String)"util.DICOM", (String)fileName)).show();
                }
            }
        
            }});
    }

    public void initialize(URL url, ResourceBundle rb) {
        this.prgCompressing.setVisible(false);
        this.lblCompressing.setVisible(false);
        this.imgPicture.setVisible(false);
        this.lblBCH.setVisible(false);
        this.lblDRatio.setVisible(false);
        this.lblDTime.setVisible(false);
        this.lblDSize.setVisible(false);
        this.chkBCH.setVisible(false);
        this.txtCompressionRatio1.setVisible(false);
        this.txtCompressionTime1.setVisible(false);
        this.txtFileSize1.setVisible(false);
        this.txtDecryptedImagePath.setText(RAcquisitionFXMLController.medicalImage.getFilePath());
    }

}

