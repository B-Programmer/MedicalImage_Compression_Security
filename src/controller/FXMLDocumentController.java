/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  javafx.application.Platform
 *  javafx.event.ActionEvent
 *  javafx.event.Event
 *  javafx.event.EventHandler
 *  javafx.fxml.FXML
 *  javafx.fxml.FXMLLoader
 *  javafx.fxml.Initializable
 *  javafx.scene.Node
 *  javafx.scene.Parent
 *  javafx.scene.Scene
 *  javafx.scene.control.Button
 *  javafx.scene.control.Label
 *  javafx.stage.Stage
 *  javafx.stage.Window
 *  javafx.stage.WindowEvent
 */
package controller;

import controller.AcquisitionFXMLController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class FXMLDocumentController
implements Initializable {
    @FXML
    private Label label;
    @FXML
    private Button btnReverse;

    @FXML
    private void LoadAcquisitionView(ActionEvent event) throws IOException {
        Parent root = (Parent)FXMLLoader.load((URL)this.getClass().getResource("/view/AcquisitionFXML.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Medical Image Acquisition Screen");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest((EventHandler)new EventHandler<WindowEvent>(){

            public void handle(WindowEvent we) {
                try {
                    Parent parent = (Parent)FXMLLoader.load((URL)this.getClass().getResource("/view/FXMLDocument.fxml"));
                    Scene scene2 = new Scene(parent);
                    Stage stage2 = new Stage();
                    stage2.setTitle("Development of a Secure Medical Image for Efficient Transmission Using a Lossless Compression and Encryption Technique");
                    stage2.centerOnScreen();
                    stage2.setResizable(false);
                    stage2.setScene(scene2);
                    stage2.show();
                }
                catch (IOException ex) {
                    Logger.getLogger(AcquisitionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        ((Node)event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    private void LoadAcquisition2View(ActionEvent event) throws IOException {
        Parent root = (Parent)FXMLLoader.load((URL)this.getClass().getResource("/view/RAcquisitionFXML.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Medical Image Acquisition For Reverse Processing Screen");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest((EventHandler)new EventHandler<WindowEvent>(){

            public void handle(WindowEvent we) {
                try {
                    Parent parent = (Parent)FXMLLoader.load((URL)this.getClass().getResource("/view/FXMLDocument.fxml"));
                    Scene scene2 = new Scene(parent);
                    Stage stage2 = new Stage();
                    stage2.setTitle("Development of a Secure Medical Image for Efficient Transmission Using a Lossless Compression and Encryption Technique");
                    stage2.centerOnScreen();
                    stage2.setResizable(false);
                    stage2.setScene(scene2);
                    stage2.show();
                }
                catch (IOException ex) {
                    Logger.getLogger(AcquisitionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        ((Node)event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    private void LoadServerSetupView(ActionEvent event) throws IOException {
        Parent root = (Parent)FXMLLoader.load((URL)this.getClass().getResource("/view/ServerSetupFXML.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Dicom Server Setup Screen");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void LoadTransmissionView(ActionEvent event) throws IOException {
        Parent root = (Parent)FXMLLoader.load((URL)this.getClass().getResource("/view/TransmissionFXML.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Image Transmission Screen");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void CloseView(ActionEvent event) {
        Platform.exit();
    }

    public void initialize(URL url, ResourceBundle rb) {
    }

}

