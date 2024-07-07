
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ImageHandler {
	private WritableImage image;
	private double magnification;
	private ImageView imageView;
	private Pane parent;
	private boolean altState;
	private Text status;
	private List<Runnable> timers;
	
	//storing a list with the coordinates of each vertex and the original color that was there before we edited the image
	private List<Vertex> vertexes;
	
	//list with 3 ints that make up a triangle face
	private Face temp;
	private List<Face> faces;
	public ImageHandler(Pane parent, Text status) {
		this.parent = parent;
		this.magnification = 1;
		this.status = status;
		this.timers = new ArrayList<>();
		vertexes = new ArrayList<>();
		faces = new ArrayList<>(); 
		
		parent.setOnScroll((event)->{
			if (event.getDeltaY()>0)
				magnification++;
			else
				magnification--;
			generateImageView();
		});
		parent.setOnKeyPressed((e)->{
			if (e.getCode()==KeyCode.ALT)
				altState = true;
		});
		parent.setOnKeyReleased((e)->{
			if (e.getCode()==KeyCode.ALT) {
				altState = false;
				temp = null;
			}
		});
		
		parent.setOnMouseClicked((event)->{
			double x = floor(event.getSceneX()/magnification);
			double y = floor((event.getSceneY()-imageView.getLayoutY())/magnification);
			if (x>image.getWidth()||y>image.getHeight())
				return;

			if (!altState)
				if (event.getButton().equals(MouseButton.PRIMARY))
				{
					Optional<Color> color = getColorAtPixel(x,y);
					//early return if point is already a vertex
					if (color.isPresent()) return;
					
					vertexes.add(new Vertex(x,
							y,
							readColorAt(x,y)));
					
					writeColorAt(x,y,getVertexColor(readColorAt(x,y)));
					generateImageView();
				} else if (event.getButton().equals(MouseButton.SECONDARY)) {
					Optional<Color> color = getColorAtPixel(x,y);
					//early return if point is already a vertex
					if (color.isEmpty()) return;
					
					vertexes.remove(new Vertex(x,
							y,
							getVertexColor(readColorAt(x,y))));
					
					writeColorAt(x,y,color.get());
					generateImageView();
					
			} else { //generate faces from multiple keypresses
				 
			}
		});
	}
	//iterates over the list to find the original color at this vertex. returns empty optional if this is not a vertex
	private Optional<Color> getColorAtPixel(double x, double y) {
		for (var curr : vertexes) {
			if (curr.x == x && curr.y == y)
				return Optional.of(curr.color);
		}
		return Optional.empty();
	}
	private Color getVertexColor(Color original) {
		return new Color(1-original.getRed(),1-original.getBlue(),1-original.getGreen(),1);
	}
	//Generates an imageview with the current magnification
	private void generateImageView() {
		parent.getChildren().remove(imageView);
		this.imageView = new ImageView(image);
		this.imageView.setFitWidth(image.getWidth()*magnification);
		this.imageView.setFitHeight(image.getHeight()*magnification);
		parent.getChildren().add(imageView);
	}
	
	
	private double floor(double d) {
		return (double)((int)d);
	}
	public Image getImage(){
		return image;
	}
	public void setImage(Image image) {
		if (imageView != null) {
			parent.getChildren().remove(imageView);
		}
	    image.progressProperty().addListener((observable, oldValue, newValue) -> {
	    	if (newValue.doubleValue() == 1.0) {
	    		Platform.runLater(() -> {
	    			this.image = new WritableImage(image.getPixelReader(), (int)image.getWidth(), (int)image.getHeight());
	    			this.imageView = new ImageView(image);
	    			parent.getChildren().add(imageView);
	            });
	        }
	    });
	    magnification = 1;
	}
	private Color readColorAt(double x, double y) {
		return image.getPixelReader().getColor((int)x, (int)y);
	}
	private void writeColorAt(double x, double y, Color c) {
		image.getPixelWriter().setColor((int)x, (int)y, c);
	}
	//im having trouble thinking of a solution to have text that will decay shortly after so status updates dont linger.
	private void updateStatus(String str) {
		status.setText(str);
		timers.add(()->{
			
		});
	}
}
