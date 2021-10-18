/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  javafx.event.ActionEvent
 *  javafx.event.Event
 *  javafx.event.EventHandler
 *  javafx.fxml.FXML
 *  javafx.fxml.Initializable
 *  javafx.scene.Node
 *  javafx.scene.Scene
 *  javafx.scene.control.Button
 *  javafx.scene.control.TextField
 *  javafx.scene.image.ImageView
 *  javafx.stage.Stage
 *  javafx.stage.Window
 *  javafx.stage.WindowEvent
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.FilenameUtils
 */
package controller;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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

public class RAcquisitionFXMLController
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
    private ImageView imgPicture;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnBack;
    private ViewLoader viewLoader;

    @FXML
    public void LoadDecryptionView(ActionEvent event) throws IOException {
        this.viewLoader = new ViewLoader();
        medicalImage.setFileName(this.txtImageName.getText());
        medicalImage.setFilePath(this.txtFilePath.getText());
        long fileSize = FileUtils.sizeOf((File)new File(medicalImage.getFilePath())) / 1024;
        medicalImage.setFileSize(fileSize);
        medicalImage.setFileExtension(this.txtExtension.getText());
        medicalImage.setAcquisitionTime(this.txtAcquisitionTime.getText());
        if (medicalImage.isValid()) {
            Stage stage = this.viewLoader.LoadView("DecryptionFXML", "Medical Image Decryption Screen");
            stage.setOnCloseRequest((EventHandler)new EventHandler<WindowEvent>(){

                public void handle(WindowEvent we) {
                    try {
                        RAcquisitionFXMLController.this.viewLoader.LoadView("FXMLDocument", "Main Screen");
                    }
                    catch (IOException ex) {
                        Logger.getLogger(RAcquisitionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
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
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Medical Image File","dcm", "dic", "dicm", "dicom");
//        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Medical Image File", "png", "jpeg", "jpg", "bmp", "dcm", "dic", "dicm", "dicom");
        jFileChooser.setFileFilter(fileFilter);
        jFileChooser.showOpenDialog(null);
        File selectedFile = jFileChooser.getSelectedFile();
        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            if(filePath.contains("-encrypted.")){
                this.txtFilePath.clear();
                this.txtFilePath.setText(filePath);
            }
            else{
                this.txtFilePath.clear();
                Stage stage = new Stage();
                MessageBox.show((Window) stage, (String) "The medical image selected is not an encrypted image!!! Try again with encrypted image", (String) "ERROR", (int) 16973824);
            }
            
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
            }
            catch (Exception ex) {
                Logger.getLogger(RAcquisitionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
            long endDate = System.currentTimeMillis();
            long difference = endDate - startDate;
            this.txtAcquisitionTime.setText("" + (difference / 1000 + 1) + " Second(s)");
        }
    }

    public void initialize(URL url, ResourceBundle rb) {
        if (medicalImage.getFilePath() != null) {
            this.txtAcquisitionTime.setText(medicalImage.getAcquisitionTime());
            this.txtExtension.setText(medicalImage.getFileExtension());
            this.txtFilePath.setText(medicalImage.getFilePath());
            this.txtImageName.setText(medicalImage.getFileName());
            this.txtSize.setText("" + medicalImage.getFileSize() + "KB");
        }
    }

}

