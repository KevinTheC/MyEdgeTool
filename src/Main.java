import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Main extends Application{

	public static void main(String[] args) {
		Application.launch(args);
	}
	@Override
	public void start(Stage stage) throws Exception {
		VBox vbox = new VBox();
		Scene scene = new Scene(vbox);
		stage.setScene(scene);
		stage.show();
		HBox hbox = new HBox();
		vbox.getChildren().add(hbox);
		Button load = new Button("Load");
		Button export = new Button("Export");
		Button paste = new Button("Paste");
		Text info = new Text("");
		hbox.getChildren().addAll(load,paste,export);
		ImageHandler ih = new ImageHandler(vbox,info);
		load.setOnAction((e)->{
			FileChooser fc = new FileChooser();
			fc.setTitle("Select Image");
			fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg"));
			File f = fc.showOpenDialog(stage);
			Image img = new Image(f.toURI().toString(),true);
			if (f != null)
				ih.setImage(img);
		});
		paste.setOnAction((e)->{
			Clipboard clipboard = Clipboard.getSystemClipboard();
			if (clipboard.getImage() == null)
			{
				Alert alert = new Alert(AlertType.WARNING, "Copied type is not of image format", ButtonType.OK);
				alert.showAndWait();
				return;
			}
			Alert alert = new Alert(AlertType.CONFIRMATION, "Paste picture from clipboard?", ButtonType.YES, ButtonType.CANCEL);
			alert.showAndWait();

			if (alert.getResult() == ButtonType.YES && clipboard.getImage() != null) {
			    ih.setImage(clipboard.getImage());
			}
		});
		export.setOnAction((e)->{
			if (ih.getImage() == null) {
				Alert alert = new Alert(AlertType.WARNING, "Copied type is not of image format", ButtonType.OK);
				alert.showAndWait();
				return;
			}
		});
	}

}
