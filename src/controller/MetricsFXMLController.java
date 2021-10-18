package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class MetricsFXMLController
        implements Initializable {

    @FXML
    private TextField txtEncryptionTime;
    @FXML
    private TextField txtAlgorithm;
    @FXML
    private TextField txtKeyLength;
    @FXML
    private TextField txtEncryptionQuality;
    @FXML
    private TextField txtNoofRounds;
     @FXML
    private TextField txtStrength;
    @FXML
    private TextField txtType;
    @FXML
    private TextField txtComplexity;
    @FXML
    private Button btnBack;
    
    
 

    @FXML
    public void LoadPreviousView(ActionEvent event) throws IOException {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.txtNoofRounds.setText(EncryptionFXMLController.NoofRounds);
        this.txtEncryptionQuality.setText(EncryptionFXMLController.EncryptionQuality);
        this.txtKeyLength.setText(EncryptionFXMLController.KeyLength);
        this.txtAlgorithm.setText(EncryptionFXMLController.AlgorithmUsed);
        this.txtEncryptionTime.setText(EncryptionFXMLController.EncryptionTime);
         this.txtStrength.setText(EncryptionFXMLController.Strength);
        this.txtType.setText(EncryptionFXMLController.Type);
        this.txtComplexity.setText(EncryptionFXMLController.Complexity);
        this.btnBack.setVisible(true);
    }

}
