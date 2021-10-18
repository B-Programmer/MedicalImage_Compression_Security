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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class DecryptionKeyFXMLController
implements Initializable {
    @FXML
    private TextField txtEncryptionKey;
    @FXML
    private CheckBox chkShow;
    @FXML
    private PasswordField txtMaskedKey;

    @FXML
    public void SaveKey(ActionEvent event) {
        String key = this.txtMaskedKey.getText();
        if (!key.isEmpty()) {
            DecryptionFXMLController.suppliedKey = key;
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

    public void initialize(URL url, ResourceBundle rb) {
        this.txtEncryptionKey.setVisible(false);
        this.txtMaskedKey.setVisible(true);
    }
}

