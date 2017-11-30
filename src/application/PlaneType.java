package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public enum PlaneType {

	AIRBUS(new Image("/application/source/Images/flugzeugrechts.png"),1), 
	BOEING(new Image("/application/source/Images/flugzeugYamaiunten.png"),0.8), 
	ECLIPSE_AVIATION(new Image("/application/source/Images/flugzeugYamai.png"),0.5);

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
	
//	public static PlaneType getRandomType() {
//		PlaneType[] types = PlaneType.values();
//		int randIndex = (int) Math.random()*types.length; 
//	}
	
	
}
