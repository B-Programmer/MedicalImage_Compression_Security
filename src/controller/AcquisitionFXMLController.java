/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.pixelmed.dicom.DicomInputStream
 *  com.pixelmed.display.SourceImage
 *  ij.IJ
 *  ij.ImagePlus
 *  javafx.embed.swing.SwingFXUtils
 *  javafx.event.ActionEvent
 *  javafx.event.Event
 *  javafx.event.EventHandler
 *  javafx.fxml.FXML
 *  javafx.fxml.Initializable
 *  javafx.scene.Node
 *  javafx.scene.Scene
 *  javafx.scene.control.Button
 *  javafx.scene.control.TextField
 *  javafx.scene.image.Image
 *  javafx.scene.image.ImageView
 *  javafx.scene.image.WritableImage
 *  javafx.stage.Stage
 *  javafx.stage.Window
 *  javafx.stage.WindowEvent
 *  jfx.messagebox.MessageBox
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.FilenameUtils
 */
package controller;

import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.display.SourceImage;
import controller.ServerSetupController;
import ij.IJ;
import ij.ImagePlus;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import jfx.messagebox.MessageBox;
import model.MedicalImage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import util.ViewLoader;

public class AcquisitionFXMLController
implements Initializable {
    public static MedicalImage medicalImage = new MedicalImage();
    @FXML
    private TextField txtFilePath;
    @FXML
    private TextField txtImageName;
    @FXML
    private TextField txtExtension;
    @FXML
    private TextField txtSize;
    @FXML
    private TextField txtAcquisitionTime;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnBrowse;
    @FXML
    private ImageView imgPicture;
    private ViewLoader viewLoader;

    @FXML
    public void LoadCompressionView(ActionEvent event) throws IOException {
        this.viewLoader = new ViewLoader();
        medicalImage.setFileName(this.txtImageName.getText());
        medicalImage.setFilePath(this.txtFilePath.getText());
        String size = this.txtSize.getText();
        if (!size.isEmpty()) {
            medicalImage.setFileSize(Long.parseLong(size.replace("KB", "")));
        }
        medicalImage.setFileExtension(this.txtExtension.getText());
        medicalImage.setAcquisitionTime(this.txtAcquisitionTime.getText());
        if (medicalImage.isValid()) {
            Stage stage = this.viewLoader.LoadView("CompressionFXML", "Medical Image Compression Screen");
            stage.setOnCloseRequest((EventHandler)new EventHandler<WindowEvent>(){

                public void handle(WindowEvent we) {
                    try {
                        AcquisitionFXMLController.this.viewLoader.LoadView("FXMLDocument", "Main Screen");
                    }
                    catch (IOException ex) {
                        Logger.getLogger(AcquisitionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            ((Node)event.getSource()).getScene().getWindow().hide();
        }else {
            Stage stage = new Stage();
            MessageBox.show((Window)stage, "Medical Image File format not supported!!", "INFO !!!", (int)67305472);
        }
    }

    @FXML
    public void LoadEncryptionView(ActionEvent event) throws IOException {
        this.viewLoader = new ViewLoader();
        medicalImage.setFileName(this.txtImageName.getText());
        medicalImage.setFilePath(this.txtFilePath.getText());
        String size = this.txtSize.getText();
        if (!size.isEmpty()) {
            medicalImage.setFileSize(Long.parseLong(size.replace("KB", "")));
        }
        medicalImage.setFileExtension(this.txtExtension.getText());
        medicalImage.setAcquisitionTime(this.txtAcquisitionTime.getText());
        if (medicalImage.isValid()) {
            Stage stage = this.viewLoader.LoadView("EncryptionFXML", "Medical Image Encryption Screen");
            stage.setOnCloseRequest((EventHandler)new EventHandler<WindowEvent>(){

                public void handle(WindowEvent we) {
                    try {
                        AcquisitionFXMLController.this.viewLoader.LoadView("FXMLDocument", "Main Screen");
                    }
                    catch (IOException ex) {
                        Logger.getLogger(AcquisitionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            ((Node)event.getSource()).getScene().getWindow().hide();
        }else {
            Stage stage = new Stage();
            MessageBox.show((Window)stage, "Medical Image File format not supported!!", "INFO !!!", (int)67305472);
        }
    }

    @FXML
    public void LoadMainView(ActionEvent event) throws IOException {
        this.viewLoader = new ViewLoader();
        this.viewLoader.LoadView("FXMLDocument", "Main Screen");
        ((Node)event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    private void BrowseImage(ActionEvent event) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Choose a file");
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setVisible(true);
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Medical Image File", "dcm", "dic", "dicm", "dicom");
//        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Medical Image File", "png", "jpeg", "jpg", "bmp", "dcm", "dic", "dicm", "dicom");
        jFileChooser.setFileFilter(fileFilter);
        jFileChooser.showOpenDialog(null);
        File selectedFile = jFileChooser.getSelectedFile();
        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            this.txtFilePath.clear();
            this.txtFilePath.setText(filePath);
        }
    }

    @FXML
    public void AcquireImage(ActionEvent event) throws IOException {
        File file;
        long startDate = System.currentTimeMillis();
        String fileName = this.txtFilePath.getText();
        if (!fileName.isEmpty() && (file = new File(fileName)).exists()) {
            try {
                this.txtImageName.setText(file.getName());
                long fileSize = FileUtils.sizeOf((File)file) / 1024;
                this.txtSize.setText(String.valueOf(fileSize) + "KB");
                String extension = FilenameUtils.getExtension((String)fileName);
                this.txtExtension.setText(extension);
                Image image = null;
//                if (extension.equalsIgnoreCase("dic") || extension.equalsIgnoreCase("dcm") | extension.equalsIgnoreCase("dicm") || extension.equalsIgnoreCase("dicom") || extension.equalsIgnoreCase("")) {
                  if (extension.equalsIgnoreCase("dic") || extension.equalsIgnoreCase("dcm") | extension.equalsIgnoreCase("dicm") || extension.equalsIgnoreCase("dicom")) {
                    DicomInputStream dicomInputStream = new DicomInputStream(file);
                    SourceImage sourceImage = new SourceImage(dicomInputStream);
                    if (dicomInputStream.haveMetaHeader()) {
                        BufferedImage bufferedImage = sourceImage.getBufferedImage();
                        image = SwingFXUtils.toFXImage((BufferedImage)bufferedImage, (WritableImage)null);
                        this.imgPicture.setImage((Image)image);
                        Stage stage = new Stage();
                        MessageBox.show((Window)stage, "Image Acquired Successfully", "INFO !!!", (int)67305472);
                    }
//                } else if (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpeg") | extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("bmp")) {
//                    FileInputStream fileInputStream = new FileInputStream(file);
//                    image = new Image((InputStream)fileInputStream);
//                    this.imgPicture.setImage((Image)image);
//                    Stage stage = new Stage();
//                    MessageBox.show((Window)stage, "Image Acquired Successfully", "INFO !!!", (int)67305472);
                }else {
                    Stage stage = new Stage();
                    MessageBox.show((Window)stage, "Image Acquisition not Successful, File format not supported!!", "INFO !!!", (int)67305472);
                }
                long endDate = System.currentTimeMillis();
                long difference = endDate - startDate;
                this.txtAcquisitionTime.setText("" + (difference / 1000 + 1) + " Second(s)");
                
            }
            catch (Exception ex) {
                long endDate = System.currentTimeMillis();
                long difference = endDate - startDate;
                this.txtAcquisitionTime.setText("" + (difference / 1000 + 1) + " Second(s)");
                Stage stage = new Stage();
                MessageBox.show((Window)stage, (String)"Could not load the image, it appears to have been compressed, \nOnly Uncompressed Dicom File is Allowed", (String)"INFO", (int)16973824);
                ((ImagePlus)IJ.runPlugIn((String)"util.DICOM", (String)fileName)).show();
            }
        }
    }

    public void initialize(URL url, ResourceBundle rb) {
        if (ServerSetupController.newFilePath != null && !ServerSetupController.newFilePath.isEmpty()) {
            this.txtFilePath.setText(ServerSetupController.newFilePath);
            ServerSetupController.newFilePath = "";
            this.btnBack.setVisible(false);
            this.btnBrowse.setDisable(true);
            Stage tempStage = new Stage();
            MessageBox.show((Window)tempStage, (String)"Incoming Image Request Received", (String)"INFO", (int)67305472);
        }
        if (medicalImage.getFilePath() != null) {
            this.txtAcquisitionTime.setText(medicalImage.getAcquisitionTime());
            this.txtExtension.setText(medicalImage.getFileExtension());
            this.txtFilePath.setText(medicalImage.getFilePath());
            this.txtImageName.setText(medicalImage.getFileName());
            this.txtSize.setText("" + medicalImage.getFileSize() + "KB");
        }
    }

}

