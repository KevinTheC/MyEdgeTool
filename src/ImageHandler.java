
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
//TODO status updates with timer, color picker for vertexes, edges and faces
public class ImageHandler {
	private ResizableImage image;
	private double translateX;
	private double translateY;
	private ImageView imageView;
	private Pane parent;
	private boolean altState;
	private Text status;
	private List<Thread> timers;
	
	private Color vertexColor;
	//storing a list with the coordinates of each vertex and the original color that was there before we edited the image
	private List<Vertex> vertexes;
	
	
	private Face temp;
	private Map<Face,Polygon> faces;
	public ImageHandler(Pane parent, Text status) {
		this.translateX = 0;
		this.translateY = 0;
		this.parent = parent;
		this.status = status;
		this.timers = new ArrayList<>();
		vertexes = new ArrayList<>();
		faces = new HashMap<>(); 
		
		parent.setOnScroll((event)->{
			if (event.getDeltaY()>0&&image.getMagnification()<17)
				image.setMagnification(image.getMagnification()*2);
			else if (event.getDeltaY()<0&&image.getMagnification()>1)
				image.setMagnification(image.getMagnification()/2);
			remapPolygons();
			generateImageView();
		});
		
		parent.setOnMouseClicked((event)->{
			double x = floor((event.getSceneX()-translateX)/image.getMagnification());
			double y = floor(((event.getSceneY()-25)-translateY)/image.getMagnification());
			if (x>image.getWidthAdjusted()||y>image.getHeightAdjusted())
				return;
			Optional<Color> color = getColorAtPixel(x,y);
			if (!altState) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					
					//early return if point is already a vertex
					if (color.isPresent()) return;
					
					vertexes.add(new Vertex(x,
							y,
							image.readColorAt(x,y)));
					
					image.writeColorAt(x,y,getVertexColor(image.readColorAt(x,y)));
					updateStatus(String.format("Added a vertex at %d, %d", (int)x,(int)y));
					
					generateImageView();
					
				} else if (event.getButton().equals(MouseButton.SECONDARY)) {
					
					//early return if point is not already a vertex
					if (color.isEmpty()) return;
					
					//verify that no faces are using this vertex
					if (!verifyUnused(x,y)) {
						updateStatus(String.format("Vertex at %d %d cannot be removed, it is in use in a face.",(int)x,(int)y));
						return;
					}
					
					vertexes.remove(new Vertex(x,
							y,
							getVertexColor(image.readColorAt(x,y))));
					
					image.writeColorAt(x,y,color.get());
					updateStatus(String.format("Removed a vertex at %d, %d", (int)x,(int)y));
					generateImageView();
				}
			} else { //generate faces from multiple keypresses
				 if (event.getButton() == MouseButton.PRIMARY) {
					 if (temp == null) {
						 getVertexAt(x,y).ifPresent((v)->{
							 temp = new Face();
							 temp.verts[0] = v;
						 });
						 updateStatus("Vertex 1/3 assigned");
					 } else if (temp.verts[1] == null) {
						 getVertexAt(x,y).ifPresent((v)->{
							 if (temp.verify(v)) {
								 temp.verts[1] = v;
								 updateStatus("Vertex 2/3 assigned");
							 }
						 });
					 } else if (temp.verts[2] == null){
						 getVertexAt(x,y).ifPresent((v)->{
							 if (temp.verify(v)) {
								 temp.verts[2] = v;
							 	 updateStatus("Vertex 3/3 assigned, face drawn and saved");
							 }
						 });
					 }
					 if (temp.verts[2] != null) {
						 Polygon pg = new Polygon();
						 pg.getPoints().setAll(new Double[] {
								 (temp.verts[0].x+.5)*image.getMagnification()+translateX, (temp.verts[0].y+.5)*image.getMagnification()+translateY,
								 (temp.verts[1].x+.5)*image.getMagnification()+translateX, (temp.verts[1].y+.5)*image.getMagnification()+translateY,
								 (temp.verts[2].x+.5)*image.getMagnification()+translateX, (temp.verts[2].y+.5)*image.getMagnification()+translateY});
						 pg.setFill(vertexColor);
						 faces.put(temp,pg);
						 parent.getChildren().add(pg);
						 temp = null;
					 }
				 }
				 else if (event.getButton() == MouseButton.SECONDARY) {
					 Optional<Face> clicked = getFaceAt(x,y);
					 if (clicked.isEmpty())
						 return;
					 parent.getChildren().remove(faces.get(clicked.get()));
					 faces.remove(clicked.get());
				 }
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
	
	private boolean verifyUnused(double x, double y) {
		for (var pair : faces.entrySet()) {
			for (Vertex v : pair.getKey().verts) {
				if (floor(v.x)==floor(x)&&floor(v.y)==floor(y))
					return false;
			}
		}
		return true;
	}
	
	
	private Optional<Vertex> getVertexAt(double x, double y) {
		for (var curr : vertexes) {
			if (curr.x == x && curr.y == y)
				return Optional.of(curr);
		}
		return Optional.empty();
	}
	
	private void remapPolygons() {
		for (var v : faces.entrySet()) {
			Face f = v.getKey();
			faces.get(f).getPoints().setAll(new Double[] {
					 (f.verts[0].x+.5)*image.getMagnification()+translateX, (f.verts[0].y+.5)*image.getMagnification()+translateY,
					 (f.verts[1].x+.5)*image.getMagnification()+translateX, (f.verts[1].y+.5)*image.getMagnification()+translateY,
					 (f.verts[2].x+.5)*image.getMagnification()+translateX, (f.verts[2].y+.5)*image.getMagnification()+translateY});
		}
	}
	
	private Color getVertexColor(Color original) {
		if (vertexColor == null)
			return new Color(1-original.getRed(),1-original.getBlue(),1-original.getGreen(),1);
		else
			return vertexColor;
	}
	
	public void setColor(Color c) {
		this.vertexColor = c;
		for (Vertex v : vertexes) {
			image.writeColorAt(v.x,v.y,c);
		}
		for (var v : faces.entrySet()) {
			v.getValue().setFill(c);
		}
	}
	//Generates an imageview with the current magnification
	private void generateImageView() {
		parent.getChildren().remove(imageView);
		this.imageView = image.getView();
		imageView.setTranslateX(translateX);
		imageView.setTranslateY(translateY);
		parent.getChildren().add(0,imageView);
	}
	public void changeAltState(boolean b) {
		if (b)
			altState = true;
		else {
			altState = false;
			temp = null;
			updateStatus("Let go of ALT key, any unfinished faces have been destroyed.");
		}
	}
	private Optional<Face> getFaceAt(double x, double y){
		for (var v : faces.entrySet()) {
			if (v.getKey().isInside(x, y))
				return Optional.of(v.getKey());
		}
		return Optional.empty();
	}
	public void translate(double x, double y) {
		translateX+=x;
		translateY+=y;
		remapPolygons();
		imageView.setTranslateX(translateX);
		imageView.setTranslateY(translateY);
	}
	
	private double floor(double d) {
		return (double)((int)d);
	}
	public Image getImage(){
		return image.getImage();
	}
	public void setImage(Image image) {
		updateStatus("New image uploaded");
		faces.clear();
		vertexes.clear();
		if (imageView != null) {
			parent.getChildren().remove(imageView);
		}
	    image.progressProperty().addListener((observable, oldValue, newValue) -> {
	    	if (newValue.doubleValue() == 1.0) {
	    		Platform.runLater(() -> {
	    			this.image = new ResizableImage(new WritableImage(image.getPixelReader(), (int)image.getWidth(), (int)image.getHeight()));
	    			this.imageView = this.image.getView();
	    			parent.getChildren().add(imageView);
	            });
	        }
	    });
	}
	private void updateStatus(String str) {
		status.setText(str);
		Thread t = new Thread(()->{
			try {
				Thread.sleep(2000);
				if (timers.size()==1)
					status.setText("");
					timers.remove(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		timers.add(t);
		t.start();
	}
	public void export(File output) {
		Exporter.export(output, vertexes, faces.keySet().stream().toList());
	}
}
