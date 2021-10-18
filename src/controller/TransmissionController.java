/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.pixelmed.network.NetworkMediaImporter
 *  com.pixelmed.utils.MessageLogger
 *  com.pixelmed.utils.PrintStreamMessageLogger
 *  javafx.application.Platform
 *  javafx.event.ActionEvent
 *  javafx.fxml.FXML
 *  javafx.fxml.Initializable
 *  javafx.scene.Scene
 *  javafx.scene.control.Button
 *  javafx.scene.control.Label
 *  javafx.scene.control.ProgressBar
 *  javafx.scene.control.TextField
 *  javafx.stage.Stage
 *  javafx.stage.Window
 *  jfx.messagebox.MessageBox
 */
package controller;

import com.pixelmed.network.NetworkMediaImporter;
import com.pixelmed.utils.MessageLogger;
import com.pixelmed.utils.PrintStreamMessageLogger;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import jfx.messagebox.MessageBox;

public class TransmissionController
implements Initializable {
    @FXML
    private TextField txtImage;
    @FXML
    private TextField txtAlias;
    @FXML
    private TextField txtIpAddress;
    @FXML
    private TextField txtPort;
    @FXML
    private ProgressBar prgSending;
    @FXML
    private Label lblSending;
    @FXML
    private Label lblTime;
    @FXML
    private Button btnSend;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnBrowse;

    @FXML
    private void SendImage(ActionEvent event) throws IOException {
        try {
            final long startDate = System.currentTimeMillis();
            final String imageAlias = this.txtAlias.getText();
            final String ipAddress = this.txtIpAddress.getText();
            final String port = this.txtPort.getText();
            final String path = this.txtImage.getText();
            if (imageAlias.isEmpty() || ipAddress.isEmpty() || port.isEmpty() || path.isEmpty()) {
                return;
            }
            this.prgSending.setVisible(true);
            this.lblSending.setVisible(true);
            this.btnBrowse.setDisable(true);
            this.btnCancel.setDisable(true);
            this.btnSend.setDisable(true);
            Thread thread = new Thread(new Runnable(){

                @Override
                public void run() {
                    PrintStreamMessageLogger logger = new PrintStreamMessageLogger(System.err);
                    new NetworkMediaImporter(ipAddress, Integer.parseInt(port), imageAlias, imageAlias, path, (MessageLogger)logger, 0);
                    long endDate = System.currentTimeMillis();
                    final long difference = endDate - startDate;
                    Platform.runLater(new Runnable() {public void run() {
                        TransmissionController.this.prgSending.setVisible(false);
                        TransmissionController.this.lblSending.setVisible(false);
                        TransmissionController.this.btnBrowse.setDisable(false);
                        TransmissionController.this.btnCancel.setDisable(false);
                        TransmissionController.this.btnSend.setDisable(false);
                        TransmissionController.this.lblTime.setVisible(true);
                        String text = "Image Sent - Total Time Taken : " + (difference / 1000 + 1) + " Second(s)";
                        TransmissionController.this.lblTime.setText(text);
                        Stage stage = new Stage();
                        MessageBox.show((Window)stage, (String)"Image Sent Successfully", (String)"INFO", (int)67305472);
                     }});
                }
            });
            thread.start();
        }
        catch (Exception e) {
            Platform.runLater(new Runnable() {public void run() {
                prgSending.setVisible(false);
                lblSending.setVisible(false);
                btnBrowse.setDisable(false);
                btnCancel.setDisable(false);
                btnSend.setDisable(false);
                Stage stage = new Stage();
                MessageBox.show((Window)stage, (String)"Image Not Sent", (String)"ERROR", (int)16973824);
             }});
        }
    }

    @FXML
    private void BrowseImage(ActionEvent event) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Choose a file");
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setVisible(true);
//        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Medical Image File", "dcm", "dic", "dicm", "dicom");
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Medical Image File", "png", "jpeg", "jpg", "bmp", "dcm", "dic", "dicm", "dicom");
        jFileChooser.setFileFilter(fileFilter);
        jFileChooser.showOpenDialog(null);
        File selectedFile = jFileChooser.getSelectedFile();
        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            this.txtImage.clear();
            this.txtImage.setText(filePath);
        }
    }

    @FXML
    private void CloseView(ActionEvent event) {
        Stage stage = (Stage)this.txtImage.getScene().getWindow();
        stage.close();
    }

    public void initialize(URL url, ResourceBundle rb) {
        this.prgSending.setVisible(false);
        this.lblSending.setVisible(false);
        this.lblTime.setVisible(false);
    }

}

