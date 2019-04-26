package view;
import controller.*;
import generated.Itinerary;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.StringReader;



@SuppressWarnings("ALL")
public class Display extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox vbox = new VBox();
        TextArea textArea = new TextArea();
        vbox.getChildren().add(textArea);

        //New xml
        Button nouveau = new Button("Nouveau");
        vbox.getChildren().add(nouveau);

        //
        Button modifier = new Button("Modifier");
        vbox.getChildren().add(modifier);

        //
        Button delete = new Button("Supprimer");
        vbox.getChildren().add(delete);

        nouveau.setOnAction(action -> {
            Stage s = new Stage();
            try {
                Nouveau nouveauView = new Nouveau();
                //nouveauView.itinerary = null;
                nouveauView.start(s);
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        });

        modifier.setOnAction(action -> {
            Stage s = new Stage();
            try {
                Nouveau nouveauView = new Nouveau();
                nouveauView.itinerary = JAXB.unmarshal(new File("plan.xml"), Itinerary.class);
                nouveauView.start(s);
            } catch (JAXBException e) {
                e.printStackTrace();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        });

        delete.setOnAction(action -> {
            textArea.setText("");
            Itinerary i = JAXB.unmarshal(new StringReader(textArea.getText()), Itinerary.class);
            try {
                VisitPlanController.persiste(i);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        });

        String res=VisitPlanController.storeXmlInFile("iti1.xml");
        textArea.setText(res);

        Scene scene = new Scene(vbox, 500, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Projet xml");
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(Display.class, args);
    }
}
