/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.pixelmed.database.DatabaseApplicationProperties
 *  com.pixelmed.database.DatabaseInformationModel
 *  com.pixelmed.database.PatientStudySeriesConcatenationInstanceModel
 *  com.pixelmed.dicom.DicomException
 *  com.pixelmed.dicom.StoredFilePathStrategy
 *  com.pixelmed.network.DicomNetworkException
 *  com.pixelmed.network.NetworkApplicationInformation
 *  com.pixelmed.network.NetworkApplicationInformationFederated
 *  com.pixelmed.network.NetworkApplicationProperties
 *  com.pixelmed.network.ReceivedObjectHandler
 *  com.pixelmed.network.StorageSOPClassSCPDispatcher
 *  com.pixelmed.query.QueryResponseGeneratorFactory
 *  com.pixelmed.query.RetrieveResponseGeneratorFactory
 *  com.pixelmed.web.RequestTypeServer
 *  com.pixelmed.web.WebServerApplicationProperties
 *  javafx.application.Platform
 *  javafx.event.ActionEvent
 *  javafx.fxml.FXML
 *  javafx.fxml.FXMLLoader
 *  javafx.fxml.Initializable
 *  javafx.scene.Parent
 *  javafx.scene.Scene
 *  javafx.scene.control.Button
 *  javafx.scene.control.Label
 *  javafx.scene.control.ProgressBar
 *  javafx.scene.control.TextField
 *  javafx.stage.Stage
 *  javafx.stage.Window
 *  jfx.messagebox.MessageBox
 *  org.apache.commons.io.FileUtils
 */
package controller;

import com.pixelmed.database.DatabaseApplicationProperties;
import com.pixelmed.database.DatabaseInformationModel;
import com.pixelmed.database.PatientStudySeriesConcatenationInstanceModel;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.StoredFilePathStrategy;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.NetworkApplicationInformation;
import com.pixelmed.network.NetworkApplicationInformationFederated;
import com.pixelmed.network.NetworkApplicationProperties;
import com.pixelmed.network.ReceivedObjectHandler;
import com.pixelmed.network.StorageSOPClassSCPDispatcher;
import com.pixelmed.query.QueryResponseGeneratorFactory;
import com.pixelmed.query.RetrieveResponseGeneratorFactory;
import com.pixelmed.web.RequestTypeServer;
import com.pixelmed.web.WebServerApplicationProperties;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import jfx.messagebox.MessageBox;
import org.apache.commons.io.FileUtils;

public class ServerSetupController
implements Initializable {
    @FXML
    private TextField txtAeTitle;
    @FXML
    private TextField txtAlias;
    @FXML
    private TextField txtIpAddress;
    @FXML
    private TextField txtPort;
    @FXML
    private ProgressBar prgStarting;
    @FXML
    private Label lblStarting;
    @FXML
    private Label lblStarted;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnCancel;
    public static String newFilePath = "";
    protected DatabaseInformationModel databaseInformationModel;

    @FXML
    private void SaveServerSettings(ActionEvent event) throws IOException {
        final String imageAlias = this.txtAlias.getText();
        String ipAddress = this.txtIpAddress.getText();
        final String port = this.txtPort.getText();
        final String aeTitle = this.txtAeTitle.getText();
        if (imageAlias.isEmpty() || ipAddress.isEmpty() || port.isEmpty() || aeTitle.isEmpty()) {
            return;
        }
        this.prgStarting.setVisible(true);
        this.lblStarting.setVisible(true);
        this.btnUpdate.setDisable(true);
        this.btnCancel.setDisable(true);
        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    Properties localProperties = new Properties();
                    localProperties.put("Dicom.ListeningPort", port);
                    localProperties.put("Dicom.CalledAETitle", aeTitle);
                    localProperties.put("Dicom.CallingAETitle", imageAlias);
                    localProperties.put("Dicom.PrimaryDeviceType", "ARCHIVE");
                    localProperties.put("Dicom.StorageSCPDebugLevel", "0");
                    localProperties.put("Network.DynamicConfigurationDebugLevel", "0");
                    localProperties.put("Application.SavedImagesFolderName", "c:\\PACS\\Temp");
                    ServerSetupController.this.startDicomServer(localProperties, null);
                    Platform.runLater(new Runnable() {public void run() {
                        ServerSetupController.this.prgStarting.setVisible(false);
                        ServerSetupController.this.lblStarting.setVisible(false);
                        ServerSetupController.this.lblStarted.setVisible(true);
                        ServerSetupController.this.btnUpdate.setDisable(false);
                        ServerSetupController.this.btnCancel.setDisable(false);
                        Stage stage = new Stage();
                        MessageBox.show((Window)stage, (String)"Dicom Server Successfully Started, You Can Close This Window", (String)"INFO", (int)67305472);
                    }});
                }
                catch (IOException ex) {
                    Logger.getLogger(ServerSetupController.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (DicomException ex) {
                    Logger.getLogger(ServerSetupController.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
                }
                catch (DicomNetworkException ex) {
                    Logger.getLogger(ServerSetupController.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
                }
            }
        });
        thread.start();
    }

    protected void startDicomServer(Properties paramProperties, DatabaseInformationModel paramDatabaseInformationModel) throws IOException, DicomException, DicomNetworkException {
        DatabaseApplicationProperties localDatabaseApplicationProperties = new DatabaseApplicationProperties(paramProperties);
        File localFile = localDatabaseApplicationProperties.getSavedImagesFolderCreatingItIfNecessary();
        this.databaseInformationModel = paramDatabaseInformationModel == null ? new PatientStudySeriesConcatenationInstanceModel(localDatabaseApplicationProperties.getDatabaseFileName(), localDatabaseApplicationProperties.getDatabaseServerName()) : paramDatabaseInformationModel;
        NetworkApplicationProperties localNetworkApplicationProperties = new NetworkApplicationProperties(paramProperties);
        NetworkApplicationInformationFederated localNetworkApplicationInformationFederated = new NetworkApplicationInformationFederated();
        WebServerApplicationProperties localWebServerApplicationProperties = new WebServerApplicationProperties(paramProperties);
        localNetworkApplicationInformationFederated.startupAllKnownSourcesAndRegister(localNetworkApplicationProperties, localWebServerApplicationProperties);
        int i = localNetworkApplicationProperties.getListeningPort();
        String str = localNetworkApplicationProperties.getCalledAETitle();
        int j = localNetworkApplicationProperties.getStorageSCPDebugLevel();
        int k = localNetworkApplicationProperties.getQueryDebugLevel();
        new Thread((Runnable)new StorageSOPClassSCPDispatcher(i, str, localFile, StoredFilePathStrategy.BYSOPINSTANCEUIDHASHSUBFOLDERS, (ReceivedObjectHandler)new OurReceivedObjectHandler(null), this.databaseInformationModel.getQueryResponseGeneratorFactory(k), this.databaseInformationModel.getRetrieveResponseGeneratorFactory(k), (NetworkApplicationInformation)localNetworkApplicationInformationFederated, false, j)).start();
        new Thread((Runnable)new RequestTypeServer(this.databaseInformationModel, localWebServerApplicationProperties)).start();
    }

    @FXML
    private void CloseView(ActionEvent event) {
        Stage stage = (Stage)this.txtAeTitle.getScene().getWindow();
        stage.close();
    }

    public void initialize(URL url, ResourceBundle rb) {
        this.prgStarting.setVisible(false);
        this.lblStarting.setVisible(false);
        this.lblStarted.setVisible(false);
    }

    private class OurReceivedObjectHandler
    extends ReceivedObjectHandler {
        public OurReceivedObjectHandler(String dummy) {
        }

        public void sendReceivedObjectIndication(String paramString1, String paramString2, String paramString3) throws DicomNetworkException, DicomException, IOException {
            if (paramString1 != null) {
                try {
                    File file = new File(paramString1);
                    ServerSetupController.newFilePath = "c:\\pacs\\temp\\" + UUID.randomUUID() + ".dcm";
                    File newFile = new File(ServerSetupController.newFilePath);
                    if (file.exists()) {
                        FileUtils.copyFile((File)file, (File)newFile);
                        Platform.runLater(new Runnable() {public void run() {
                            try {
                                Parent root = (Parent)FXMLLoader.load((URL)this.getClass().getResource("/view/AcquisitionFXML.fxml"));
                                Scene scene = new Scene(root);
                                Stage stage = new Stage();
                                stage.setTitle("Medical Image Acquisition Screen");
                                stage.centerOnScreen();
                                stage.setResizable(false);
                                stage.setScene(scene);
                                stage.show();
                            }
                            catch (IOException e) {
                                e.printStackTrace(System.err);
                            }
                         }});
                    }
                }
                catch (Exception localException) {
                    localException.printStackTrace(System.err);
                }
            }
        }
    }

}

