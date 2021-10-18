/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  javafx.event.ActionEvent
 *  javafx.fxml.FXML
 *  javafx.fxml.Initializable
 *  javafx.scene.Node
 *  javafx.scene.Scene
 *  javafx.scene.control.CheckBox
 *  javafx.scene.control.PasswordField
 *  javafx.scene.control.TextField
 *  javafx.stage.Window
 */
package controller;

//import controller.EncryptionFXMLController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import util.EncryptionKeyGenrerator;

public class EncryptionKeyFXMLController
implements Initializable {
    @FXML
    private TextField txtEncryptionKey;
    @FXML
    private CheckBox chkShow;
    @FXML
    private PasswordField txtMaskedKey;
    @FXML
    private Button btnOk;
    @FXML
    private TextField txtGenerateEncryptionKey;
    @FXML
    private RadioButton optBlowfishKey;
    @FXML
    private RadioButton optAESKey;
    
    @FXML
    public void ToggleBlowfishEncryptionKeyAlgorithm(ActionEvent event) {
        if (this.optBlowfishKey.isSelected()) {
            this.optAESKey.setSelected(false);
        }
    }

    @FXML
    public void ToggleAESEncryptionKeyAlgorithm(ActionEvent event) {
        if (this.optAESKey.isSelected()) {
            this.optBlowfishKey.setSelected(false);
        }
    }

    @FXML
    public void SaveKey(ActionEvent event) {
        String key = this.txtMaskedKey.getText();
        if (!key.isEmpty()) {
            EncryptionFXMLController.suppliedKey = key;
            ((Node)event.getSource()).getScene().getWindow().hide();
        }
    }

    @FXML
    public void CloseView(ActionEvent event) throws IOException {
        ((Node)event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    public void ShowEncryptionKey(ActionEvent event) {
        String text = this.chkShow.getText();
        if (text.equalsIgnoreCase("show")) {
            this.chkShow.setText("Hide");
            this.txtEncryptionKey.setText(this.txtMaskedKey.getText());
            this.txtEncryptionKey.setVisible(true);
            this.txtMaskedKey.setVisible(false);
        } else {
            this.chkShow.setText("Show");
            this.txtMaskedKey.setText(this.txtEncryptionKey.getText());
            this.txtEncryptionKey.setVisible(false);
            this.txtMaskedKey.setVisible(true);
        }
    }
    
    @FXML
    public void GenerateKey(ActionEvent event) {
        EncryptionKeyGenrerator encryptionKeyGenrerator = new EncryptionKeyGenrerator();
        String key;
        if(optBlowfishKey.isSelected()){
            key = encryptionKeyGenrerator.nextEncryptionKey("Blowfish");
        }
        else
        {
            key = encryptionKeyGenrerator.nextEncryptionKey("AES");
        }
        
        
        if (!key.isEmpty()) {
            this.txtGenerateEncryptionKey.setText(key); 
            this.txtMaskedKey.setDisable(false);
            this.btnOk.setDisable(false);
        }
    }
    
    public void initialize(URL url, ResourceBundle rb) {
        this.txtEncryptionKey.setVisible(false);
        this.txtMaskedKey.setVisible(true);
        this.txtMaskedKey.setDisable(true);
        this.btnOk.setDisable(true);
    }    
    
}

