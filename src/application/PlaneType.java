package application;

import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public enum PlaneType {

	AIRBUS(new Image("/application/source/Images/flugzeugYamai.png"),1), 
	BELLX(new Image("/application/source/Images/bellX.PNG"),0.8), 
	ECLIPSE_AVIATION(new Image("/application/source/Images/eclipseaviation2.png"),0.5),
	XFUNFZEHN(new Image("/application/source/Images/x15.png"),0.5);

	private final Image img;
	private final double size;
	
	private PlaneType(Image img, double size) {
		this.img = img;
		this.size = size;
	}
	
	public double getSize(){
		return this.size;
	}
	
	public ImageView getImageView() {
		return new ImageView(this.img);
	}
	
	public static PlaneType getRandomType() {
		Random generator = new Random();
		PlaneType[] types = PlaneType.values();
		return types[generator.nextInt(types.length)];
	}
	
	
}
