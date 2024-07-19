import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ResizableImage {
	private WritableImage image;
	private int magnification;
	public ResizableImage(WritableImage image) {
		this.image = image;
		this.magnification = 1;
	}
	public int getMagnification() {
		return magnification;
	}
	public double getWidthActual() {
		return image.getWidth();
	}
	public double getWidthAdjusted() {
		return image.getWidth()/magnification;
	}
	public double getHeightActual() {
		return image.getHeight();
	}
	public double getHeightAdjusted() {
		return image.getHeight()/magnification;
	}
	public void setMagnification(int newmag) {
		WritableImage image = new WritableImage((int)getWidthAdjusted()*newmag,(int)getHeightAdjusted()*newmag);
		for (int i=0;i<getWidthAdjusted();i++)
			for (int j=0;j<getHeightAdjusted();j++)
				writeColorAt(i,j,readColorAt(i,j),newmag,image);
		this.magnification = newmag;
		this.image = image;
	}
	public ImageView getView() {
		return new ImageView(image);
	}
	public WritableImage getImage() {
		return image;
	}
	public Color readColorAt(double x, double y) {
		return readColorAt(x,y,magnification,image);
	}
	public void writeColorAt(double x, double y, Color c) {
		writeColorAt(x,y,c,magnification,image);
	}
	private static Color readColorAt(double x, double y, int magnification, WritableImage image) {
		return image.getPixelReader().getColor((int)x*magnification, (int)y*magnification);
	}
	private static void writeColorAt(double x, double y, Color c, int magnification, WritableImage image) {
		int xstart = (int)x*magnification;
		int ystart = (int)y*magnification;
		for (int i=0;i<magnification;i++)
			for (int j=0; j<magnification;j++)
				image.getPixelWriter().setColor((int)xstart+i, (int)ystart+j, c);
		
	}
}
