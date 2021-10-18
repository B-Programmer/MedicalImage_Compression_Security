/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  javafx.application.Application
 *  javafx.application.Platform
 *  javafx.stage.Stage
 */
package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import util.SQliteDbConnector;
import util.ViewLoader;

public class MedicalImage_Compression_Security
extends Application {
    public void start(Stage stage) throws Exception {
        ViewLoader viewLoader = new ViewLoader();
        viewLoader.LoadView("FXMLDocument", "Main Screen");
        Platform.setImplicitExit((boolean)false);
        SQliteDbConnector.connectToSQLDb();
    }

    public static void main(String[] args) {
        MedicalImage_Compression_Security.launch((String[])args);
    }
}

