/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  com.pixelmed.dicom.DicomInputStream
 *  com.pixelmed.display.SourceImage
 *  javafx.application.Platform
 *  javafx.embed.swing.SwingFXUtils
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
 *  javafx.scene.control.ProgressBar
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
import controller.AcquisitionFXMLController;
import controller.EncryptionFXMLController;
import controller.RAcquisitionFXMLController;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
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
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import jfx.messagebox.MessageBox;
import model.MedicalImage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import util.SQliteDbConnector;
import util.ViewLoader;

public class DecryptionFXMLController
        implements Initializable {

    @FXML
    private TextField txtDecryptionTime;
    @FXML
    private TextField txtDecryptionRatio;
    @FXML
    private TextField txtImageStatus;
    @FXML
    private TextField txtAcquiredImagePath;
    @FXML
    private TextField txtImagePath;
    @FXML
    private ProgressBar prgDecrypting;
    @FXML
    private Label lblDecrypting;
    @FXML
    private ImageView imgPicture;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnDecrypt;
    @FXML
    private Button btnBack;

    @FXML
    private CheckBox chkEnhanced;
    @FXML
    private CheckBox chkNormal;
    private ViewLoader viewLoader;
    private static boolean isDecrypted = false;
    public static String suppliedKey = "";
    private static boolean isEnhanced = false;
    private static boolean keyNotCorrect = true;
    private static int countKey = 0;
    
    @FXML
    public void ToggleEncryptionAlgorithm(ActionEvent event) {
        if (this.chkNormal.isSelected()) {
            this.chkEnhanced.setSelected(false);
            isEnhanced = false;
        }
    }

    @FXML
    public void ToggleEncryptionAlgorithm2(ActionEvent event) {
        if (this.chkEnhanced.isSelected()) {
            this.chkNormal.setSelected(false);
            isEnhanced = true;
        }
    }

    @FXML
    public void DecryptImage(ActionEvent event) throws IOException {
       countKey = 0; 
     do{
         keyNotCorrect = false;
         if(!isDecryptImage()){
         countKey++; keyNotCorrect = true;
         Stage stage = new Stage();
         MessageBox.show((Window) stage, (String) "Decryption Key Supplied Is Not Valid!!! Try again with correct Key", (String) "ERROR", (int) 16973824);
         MessageBox.show((Window) stage, (String) "You have spent "+ countKey + " out of 3 times, try again any way", (String) "ERROR", (int) 16973824);
         }
      }while(keyNotCorrect &&(countKey < 3));
    }
    
    private boolean isDecryptImage() throws IOException {
        
        Parent root = (Parent) FXMLLoader.load((URL) this.getClass().getResource("/view/DecryptionKeyFXML.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Decryption Key Screen");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.showAndWait();
        if (suppliedKey.isEmpty()) {
            stage = new Stage();
            MessageBox.show((Window) stage, (String) "Decryption Key cannot be empty!!! Try again with correct Key", (String) "ERROR", (int) 16973824);
            return false;
        }
        if (!suppliedKey.isEmpty()) {
            this.lblDecrypting.setVisible(true);
            this.prgDecrypting.setVisible(true);
            this.btnBack.setDisable(true);
            this.btnDecrypt.setDisable(true);
            this.btnNext.setDisable(true);
            final long startDate = System.currentTimeMillis();
            final String filePath = RAcquisitionFXMLController.medicalImage.getFilePath();
            final String fileName = FilenameUtils.getName((String) filePath);
            final File file = new File(filePath);
            final String folderPath = filePath.replace(fileName, "");
            final String fileExtension = FilenameUtils.getExtension((String) filePath);
            final String formattedFileName = fileName.replace("." + fileExtension, "");
            final String decryptedFilePath = folderPath + formattedFileName + "-decrypted" + "." + fileExtension;
//            final String key = suppliedKey + "random201";
            final String key = suppliedKey;
            final byte[] keyBytes = key.getBytes();
            if (file.exists() && !suppliedKey.isEmpty()) {
                isDecrypted = SQliteDbConnector.isValidKey(filePath, key);
                Thread thread;
                thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        String statusMessage = "";
                        try {
                            SecretKeySpec secretKeySpec = null;

                            String algorithm = "Blowfish";
                            if (isEnhanced) {
                                algorithm = "AES";
                            }

                            if (isEnhanced) {
                                byte[] byteKey = (key).getBytes("UTF-8");
                                MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
                                byteKey = messageDigest.digest(byteKey);
                                byteKey = Arrays.copyOf(byteKey, 16);
                                secretKeySpec = new SecretKeySpec(byteKey, algorithm);
                            } else {
                                secretKeySpec = new SecretKeySpec(keyBytes, algorithm);
                            }

//                            Cipher cipher = Cipher.getInstance(algorithm);
//                            cipher.init(1, secretKeySpec);
//                            byte[] fileBytes = FileUtils.readFileToByteArray((File) file);
//                            byte[] encryptedFileBytes = cipher.doFinal(fileBytes);

//                            byte[] keyBytes = key.getBytes();
//                            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "Blowfish");
//                            Cipher cipher = Cipher.getInstance("Blowfish");
//                            cipher.init(2, secretKeySpec);

                            byte[] fileBytes = FileUtils.readFileToByteArray((File) file);
//                            byte[] decryptedFileBytes = cipher.doFinal(fileBytes);
//                            FileUtils.writeByteArrayToFile((File) new File(decryptedFilePath), (byte[]) decryptedFileBytes);
                            FileUtils.writeByteArrayToFile((File) new File(decryptedFilePath), (byte[]) fileBytes);
                            statusMessage = "Image Decrypted";
//                            Platform.runLater(new Runnable() {
//                                public void run() {
//                                    lambda$run$5(startDate, decryptedFilePath);
//                                    //keyNotCorrect = false; 
//                                    //System.out.println("Key Correct!!!1");
//                                }
//                            });

//                            isDecrypted = SQliteDbConnector.isValidKey(filePath, key);
                            //keyNotCorrect = false; System.out.println("Key Correct!!!2"); 
                            
                                    
                        } catch (IOException ex) {
                            Logger.getLogger(EncryptionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                            statusMessage = "Image Decryption Failed";
                           // MessageBox.show((Window) stage1, (String) "You have spent "+ countKey + " out of 3 times, try again any way", (String) "ERROR", (int) 16973824);
                        } catch (NoSuchAlgorithmException ex) {
                            Logger.getLogger(DecryptionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                        }
//                                catch (NoSuchPaddingException ex) {
//                            Platform.runLater(new Runnable() {
//                                public void run() {
//                                    Stage stage = new Stage();
//                                    MessageBox.show((Window) stage, (String) "Wrong Decryption Key!!! Try again with correct Key", (String) "ERROR", (int) 16973824);
//                            //        MessageBox.show((Window) stage, (String) "You have spent "+ countKey + " out of 3 times, try again any way", (String) "ERROR", (int) 16973824);
//                                }
//                            });
//                        } catch (InvalidKeyException ex) {
//                            Platform.runLater(new Runnable() {
//                                public void run() {
//                                    Stage stage = new Stage();
//                                    MessageBox.show((Window) stage, (String) "Decryption Key Supplied Is Not Valid!!! Try again with correct Key", (String) "ERROR", (int) 16973824);
//                              //      MessageBox.show((Window) stage, (String) "You have spent "+ countKey + " out of 3 times, try again any way", (String) "ERROR", (int) 16973824);
//                                }
//                            });
//                        } catch (IllegalBlockSizeException ex) {
//                            Platform.runLater(new Runnable() {
//                                public void run() {
//                                    Stage stage = new Stage();
//                                    MessageBox.show((Window) stage, (String) "Decryption Key Supplied Is InValid!!! Try again with correct Key", (String) "ERROR", (int) 16973824);
//                                //    MessageBox.show((Window) stage, (String) "You have spent "+ countKey + " out of 3 times, try again any way", (String) "ERROR", (int) 16973824);
//                                }
//                            });
//                        } catch (BadPaddingException ex) {
//                            Platform.runLater(new Runnable() {
//                                public void run() {
//                                    Stage stage = new Stage();
//                                    MessageBox.show((Window) stage, (String) "Decryption Key Supplied Is Not Valid!!! Try again with correct Key", (String) "ERROR", (int) 16973824);
//                                  //  MessageBox.show((Window) stage, (String) "You have spent "+ countKey + " out of 3 times, try again any way", (String) "ERROR", (int) 16973824);
//                                }
//                            });
//                        }

//                        if(isDecrypted){
//                            Platform.runLater(new Runnable() {
//                              public void run() {
//                                DecryptionFXMLController.this.btnBack.setDisable(false);
//                                DecryptionFXMLController.this.btnDecrypt.setDisable(false);
//                                DecryptionFXMLController.this.btnNext.setDisable(false);
//                                DecryptionFXMLController.this.lblDecrypting.setVisible(false);
//                                DecryptionFXMLController.this.prgDecrypting.setVisible(false);
//                                long endDate = System.currentTimeMillis();
//                                long difference = endDate - startDate;
//                                DecryptionFXMLController.this.txtImageStatus.setText("Image Decrypted");
//                                DecryptionFXMLController.this.txtImagePath.setText(decryptedFilePath);
//                                DecryptionFXMLController.this.AcquireImage(DecryptionFXMLController.this.txtImagePath.getText());
//                                
//                                DecryptionFXMLController.this.txtDecryptionTime.setText("" + (difference / 1000 + 1) + " Second(s)");
//                                
//                       // DecryptionFXMLController.this.AcquireImage(DecryptionFXMLController.this.txtImagePath.getText());
//                                Stage stage = new Stage();
//                                MessageBox.show((Window) stage, (String) "Image Decrypted Successfully", (String) "INFO", (int) 67305472);
//                                DecryptionFXMLController.this.btnNext.setVisible(true);
//                                //keyNotCorrect = false;
//                               // System.out.println("Key Correct!!!3");
//                             }
//                           });
//                        }
//                        else {
//                            //
//                        }

                    }

//                    private void lambda$run$5(long l, String string) { /* synthetic */
//                        long endDate = System.currentTimeMillis();
//                        long difference = endDate - l;
//                        DecryptionFXMLController.this.txtDecryptionTime.setText("" + (difference / 1000 + 1) + " Second(s)");
//                        DecryptionFXMLController.this.txtImageStatus.setText("Image Decrypted");
//                        DecryptionFXMLController.this.txtImagePath.setText(string);
//                       // DecryptionFXMLController.this.AcquireImage(DecryptionFXMLController.this.txtImagePath.getText());
//                        Stage stage = new Stage();
//                        MessageBox.show((Window) stage, (String) "Image Decrypted Successfully", (String) "INFO", (int) 67305472);
//                       DecryptionFXMLController.this.btnNext.setDisable(false);
//                       DecryptionFXMLController.this.btnNext.setVisible(true);
//                       //keyNotCorrect = false;
//                      // System.out.println("Key Correct!!!4");
//                    }
                });
                thread.start();
               if(isDecrypted){
//                            Platform.runLater(new Runnable() {
//                              public void run() {
                                DecryptionFXMLController.this.btnBack.setDisable(false);
                                DecryptionFXMLController.this.btnDecrypt.setDisable(false);
                                DecryptionFXMLController.this.btnNext.setDisable(false);
                                DecryptionFXMLController.this.lblDecrypting.setVisible(false);
                                DecryptionFXMLController.this.prgDecrypting.setVisible(false);
                                long endDate = System.currentTimeMillis();
                                long difference = endDate - startDate;
                                DecryptionFXMLController.this.txtImageStatus.setText("Image Decrypted");
                                DecryptionFXMLController.this.txtImagePath.setText(decryptedFilePath);
                                DecryptionFXMLController.this.AcquireImage(DecryptionFXMLController.this.txtImagePath.getText());
                                
                                DecryptionFXMLController.this.txtDecryptionTime.setText("" + (difference / 1000 + 1) + " Second(s)");
                                
                                // DecryptionFXMLController.this.AcquireImage(DecryptionFXMLController.this.txtImagePath.getText());
//                                Stage stage = new Stage();
                                MessageBox.show((Window) stage, (String) "Image Decrypted Successfully", (String) "INFO", (int) 67305472);
                                DecryptionFXMLController.this.btnNext.setVisible(true);
                                //keyNotCorrect = false;
                               // System.out.println("Key Correct!!!3");
//                              }
//                            });
                        } 
            }
        
        } else {
            Stage stage1 = new Stage();
            MessageBox.show((Window) stage1, (String) "Decryption Keys Supplied Do Not Match Or Are Empty", (String) "ERROR", (int) 16973824);
            //MessageBox.show((Window) stage1, (String) "You have spent "+ countKey + " out of 3 times, try again any way", (String) "ERROR", (int) 16973824);
        }
        
        return isDecrypted;

    }

    public void AcquireImage(String decryptedImagePath) {
        File file; String fileImage = "";
        if((decryptedImagePath.indexOf("-") != 0))
        {
            fileImage = decryptedImagePath.substring(0, decryptedImagePath.indexOf("-"));    
            System.out.println("Image File" +fileImage);
        }
        
        if (!decryptedImagePath.isEmpty() && (file = new File(decryptedImagePath)).exists()) {
            try {
                String extension = FilenameUtils.getExtension((String) decryptedImagePath);
                fileImage = fileImage + "-compressed" + "." + extension;
                Image image = null;
                if (extension.equalsIgnoreCase("dic") || extension.equalsIgnoreCase("dcm") | extension.equalsIgnoreCase("dicm") || extension.equalsIgnoreCase("dicom") || extension.equalsIgnoreCase("")) {
//                    DicomInputStream dicomInputStream = new DicomInputStream(file);
                    DicomInputStream dicomInputStream = new DicomInputStream(new File(fileImage));
                    SourceImage sourceImage = new SourceImage(dicomInputStream);
                    if (dicomInputStream.haveMetaHeader()) {
                        BufferedImage bufferedImage = sourceImage.getBufferedImage();
                        image = SwingFXUtils.toFXImage((BufferedImage) bufferedImage, (WritableImage) null);
                        this.imgPicture.setImage((Image) image);
                    }
                } else {
//                    FileInputStream fileInputStream = new FileInputStream(file);
                    FileInputStream fileInputStream = new FileInputStream(new File(fileImage));    
                    image = new Image((InputStream) fileInputStream);
                    this.imgPicture.setImage((Image) image);
                }
                this.btnNext.setVisible(false);
            } catch (Exception ex) {
                // empty catch block
            }
        }
    }

    @FXML
    public void LoadPreviousView(ActionEvent event) throws IOException {
        this.viewLoader = new ViewLoader();
        this.viewLoader.LoadView("RAcquisitionFXML", "Medical Image Acquisition For Reverse Processing Screen");
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    public void LoadDeCompressionView(ActionEvent event) throws IOException {
        this.viewLoader = new ViewLoader();
        RAcquisitionFXMLController.medicalImage.setFilePath(this.txtImagePath.getText());
        if (isDecrypted) {
            Stage stage = this.viewLoader.LoadView("DecompressionFXML", "Medical Image Decompression Screen");
            stage.setOnCloseRequest((EventHandler) new EventHandler<WindowEvent>() {

                public void handle(WindowEvent we) {
                    try {
                        DecryptionFXMLController.this.viewLoader.LoadView("FXMLDocument", "Main Screen");
                    } catch (IOException ex) {
                        Logger.getLogger(AcquisitionFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            ((Node) event.getSource()).getScene().getWindow().hide();
        }
    }

    public void initialize(URL url, ResourceBundle rb) {
        this.lblDecrypting.setVisible(false);
        this.prgDecrypting.setVisible(false);
        this.txtAcquiredImagePath.setText(RAcquisitionFXMLController.medicalImage.getFilePath());
    }

}
