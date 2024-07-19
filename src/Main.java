import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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
		Pane pane = new Pane();
		Scene scene = new Scene(vbox);
		stage.setScene(scene);
		stage.show();
		HBox hbox = new HBox();
		vbox.getChildren().addAll(hbox,pane);
		Button load = new Button("Load");
		Button export = new Button("Export");
		Button paste = new Button("Paste");
		Text info = new Text("");
		ColorPicker colorPicker = new ColorPicker();
		Button left = new Button("<");
		Button right = new Button(">");
		Button down = new Button("v");
		Button up = new Button("^");
		
		
		hbox.getChildren().addAll(load,paste,export,colorPicker,left,right,up,down,info);
		ImageHandler ih = new ImageHandler(pane,info);
		
		up.setOnAction((e)->{
			ih.translate(0, -20);
		});
		down.setOnAction((e)->{
			ih.translate(0, 20);
		});
		left.setOnAction((e)->{
			ih.translate(-20, 0);
		});
		right.setOnAction((e)->{
			ih.translate(20, 0);
		});
		
		colorPicker.setOnAction((e)->{
			ih.setColor(colorPicker.getValue());
		});
		load.setOnAction((e)->{
			FileChooser fc = new FileChooser();
			fc.setTitle("Select Image");
			fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg"));
			File f = fc.showOpenDialog(stage);
			if (f == null) return;
			Image img = new Image(f.toURI().toString(),true);
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
		
		vbox.setOnKeyPressed((e)->{
			if (e.getCode() == KeyCode.ALT)
				ih.changeAltState(true);
		});
		vbox.setOnKeyReleased((e)->{
			if (e.getCode() == KeyCode.ALT)
				ih.changeAltState(false);
		});
		
	}

}
