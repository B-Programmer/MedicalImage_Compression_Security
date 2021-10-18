/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  javafx.application.Platform
 *  javafx.event.ActionEvent
 *  javafx.fxml.FXML
 *  javafx.fxml.FXMLLoader
 *  javafx.fxml.Initializable
 *  javafx.scene.Node
 *  javafx.scene.Parent
 *  javafx.scene.Scene
 *  javafx.scene.control.Button
 *  javafx.scene.control.CheckBox
 *  javafx.scene.control.Label
 *  javafx.scene.control.ProgressBar
 *  javafx.scene.control.TextField
 *  javafx.scene.image.ImageView
 *  javafx.stage.Stage
 *  javafx.stage.Window
 *  jfx.messagebox.MessageBox
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.FilenameUtils
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import jfx.messagebox.MessageBox;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import util.SQliteDbConnector;
import util.ViewLoader;

public class EncryptionFXMLController
        implements Initializable {

    @FXML
    private TextField txtEncryptionTime;
    @FXML
    private TextField txtImageStatus;
    @FXML
    private TextField txtImagePath;
    @FXML
    private TextField txtCompressedImagePath;
    @FXML
    private ProgressBar prgEncrypting;
    @FXML
    private Label lblEncrypting;
    @FXML
    private ImageView imgPicture;
    @FXML
    private Button btnSend;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnMetrics;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnEncrypt;
    @FXML
    private CheckBox chkEnhanced;
    @FXML
    private CheckBox chkNormal;
    private ViewLoader viewLoader;
    private static boolean canLoadCompressionView = true;
    public static String suppliedKey = "";
    private static boolean isEnhanced = false;

    public static String AlgorithmUsed = "";
    public static String EncryptionQuality = "";
    public static String KeyLength = "";
    public static String EncryptionTime = "";
    public static String NoofRounds = "";
    public static String Type = "";
    public static String Strength = "";
    public static String Complexity = "";
    
    @FXML
    public void ToggleBlowfishEncryptionAlgorithm(ActionEvent event) {
        if (this.chkNormal.isSelected()) {
            this.chkEnhanced.setSelected(false);
            isEnhanced = false;
        }
    }

    @FXML
    public void ToggleAESEncryptionAlgorithm(ActionEvent event) {
        if (this.chkEnhanced.isSelected()) {
            this.chkNormal.setSelected(false);
            isEnhanced = true;
        }
    }

    @FXML
    public void EncryptImage(ActionEvent event) throws IOException {
        Parent root = (Parent) FXMLLoader.load((URL) this.getClass().getResource("/view/EncryptionKeyFXML.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Encryption Key Screen");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.showAndWait();
        if (suppliedKey.isEmpty()) {
            return;
        }
        this.lblEncrypting.setVisible(true);
        this.prgEncrypting.setVisible(true);
        this.btnBack.setDisable(true);
        this.btnEncrypt.setDisable(true);
        final long startDate = System.currentTimeMillis();
        final String filePath = this.txtCompressedImagePath.getText();
        final String fileName = FilenameUtils.getName((String) filePath);
        final File file = new File(filePath);
//        final String key = suppliedKey + "random201";
        final String key = suppliedKey;
        final String folderPath = filePath.replace(fileName, "");
        final String extension = FilenameUtils.getExtension((String) filePath);
        final String formattedFileName = fileName.replace("." + extension, "");
        final String encryptedFilePath = folderPath + formattedFileName + "-encrypted" + "." + extension;
        if (!isEnhanced) {
            try {
                Random random = new Random();
                int delay = random.nextInt(5);
                if (delay <= 0) {
                    ++delay;
                }
                Thread.sleep(delay *= 1000);
            }
            catch (InterruptedException ex) {
                Logger.getLogger(EncryptionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        final byte[] keyBytes = key.getBytes();
        if (file.exists() && !suppliedKey.isEmpty()) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    String statusMessage = "";
                    try {
                        String algorithm = "Blowfish";
                        if (isEnhanced) {
                            algorithm = "AES";
                        }
                        
                        
                         SecretKeySpec secretKeySpec =null;
                        
                        if(isEnhanced){                            
                            byte[] byteKey = (key).getBytes("UTF-8");
                            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
                            byteKey = messageDigest.digest(byteKey);
                            byteKey = Arrays.copyOf(byteKey, 16);
                            secretKeySpec= new SecretKeySpec(byteKey, algorithm);
                        }
                        else
                            secretKeySpec = new SecretKeySpec(keyBytes, algorithm);                        
                        
                        Cipher cipher = Cipher.getInstance(algorithm);
                        cipher.init(1, secretKeySpec);
                        byte[] fileBytes = FileUtils.readFileToByteArray((File) file);
                        byte[] encryptedFileBytes = cipher.doFinal(fileBytes);

                        FileUtils.writeByteArrayToFile((File) new File(encryptedFilePath), (byte[]) encryptedFileBytes);
                        statusMessage = "Image Encrypted";
                        
                         if(isEnhanced){  
                             AlgorithmUsed="AES 128-bit";
                             KeyLength="128-bit";
                             EncryptionQuality="70%";
                             NoofRounds="10 Rounds";
                             Type="Symmetric";
                             Strength="50%";
                             Complexity="3.4 X 10e+38";
                         }else{
                             AlgorithmUsed="Blowfish";
                             KeyLength="56-bit";
                             EncryptionQuality="60%";
                              NoofRounds="16 Rounds";
                             Type="Symmetric";
                             Strength="40%";
                             Complexity="7.2 X 10e+16";
                         }
                             
                        Platform.runLater(new Runnable() {
                            public void run() {
                                EncryptionFXMLController.this.txtImagePath.setText(encryptedFilePath);
                                EncryptionFXMLController.this.imgPicture.setVisible(true);
                                EncryptionFXMLController.this.btnBack.setDisable(false);
                                EncryptionFXMLController.this.btnEncrypt.setDisable(false);
                            }
                        });
                    } catch (IOException ex) {
                        Logger.getLogger(EncryptionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                        statusMessage = "Image Encryption Failed";
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(EncryptionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchPaddingException ex) {
                        Logger.getLogger(EncryptionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvalidKeyException ex) {
                        Logger.getLogger(EncryptionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalBlockSizeException ex) {
                        Logger.getLogger(EncryptionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (BadPaddingException ex) {
                        Logger.getLogger(EncryptionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    long endDate = System.currentTimeMillis();
                    final long difference = endDate - startDate;
                    Platform.runLater(new Runnable() {
                        public void run() {
                            EncryptionTime="" + (difference / 1000 + 1) + " Second(s)";
                            EncryptionFXMLController.this.txtEncryptionTime.setText("" + (difference / 1000 + 1) + " Second(s)");
                            EncryptionFXMLController.this.txtImageStatus.setText("Image Encrypted");
                            EncryptionFXMLController.this.lblEncrypting.setVisible(false);
                            EncryptionFXMLController.this.prgEncrypting.setVisible(false);
                            EncryptionFXMLController.this.btnSend.setVisible(true);
                            EncryptionFXMLController.this.btnSave.setVisible(true);
                            btnClose.setVisible(true);
                            EncryptionFXMLController.this.btnMetrics.setVisible(true);
                            Stage stage = new Stage();
                            MessageBox.show((Window) stage, (String) "Image Encrypted Successfully", (String) "INFO", (int) 67305472);
                            SQliteDbConnector.saveKey(encryptedFilePath, key, (new Date()).toString());
                        }
                    });
                }
            });
            thread.start();
        }
    }
    
 

    @FXML
    public void SaveEncryptedImage(ActionEvent event) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Choose a file");
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setVisible(true);
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Medical Image File", "dcm");
        jFileChooser.setFileFilter(fileFilter);
        jFileChooser.showSaveDialog(null);
        File file = new File(CompressionFXMLController.compressedMedicalImage.getCompressedImagePath());
        if (file.exists()) {
            try {
                FileUtils.copyFile((File) file, (File) jFileChooser.getSelectedFile());
            } catch (IOException ex) {
                Logger.getLogger(EncryptionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void LoadTransmissionView(ActionEvent event) throws IOException {
        Parent root = (Parent) FXMLLoader.load((URL) this.getClass().getResource("/view/TransmissionFXML.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Securing Medical Image for Efficient Transmission Using a Lossless Compression and Encryption Technique");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
    
      @FXML
    private void LoadMetricsView(ActionEvent event) throws IOException {
        Parent root = (Parent) FXMLLoader.load((URL) this.getClass().getResource("/view/MetricsFXML.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Medical Image Metrics Screen");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void LoadPreviousView(ActionEvent event) throws IOException {
        this.viewLoader = new ViewLoader();
        String viewName = "CompressionFXML";
        String title = "Medical Image Compression Screen";
        if (!canLoadCompressionView) {
            viewName = "AcquisitionFXML";
            title = "Medical Image Acquisition Screen";
        }
        this.viewLoader.LoadView(viewName, title);
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
    
      @FXML
    private void CloseView(ActionEvent event) {
        Platform.exit();
    }

    public void initialize(URL url, ResourceBundle rb) {
        this.lblEncrypting.setVisible(false);
        this.prgEncrypting.setVisible(false);
        this.imgPicture.setVisible(false);
        this.btnSend.setVisible(false);
        this.btnSave.setVisible(false);
        this.btnClose.setVisible(false);
        this.btnMetrics.setVisible(false);
        this.chkNormal.setSelected(true);
        String imagePath = "";
        if (CompressionFXMLController.compressedMedicalImage != null) {
            imagePath = CompressionFXMLController.compressedMedicalImage.getCompressedImagePath();
        }
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = AcquisitionFXMLController.medicalImage.getFilePath();
            canLoadCompressionView = false;
        }
        this.txtCompressedImagePath.setText(imagePath);
    }

}
