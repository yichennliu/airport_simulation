package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public enum PlaneType {

	AIRBUS, BOEING, ECLIPSE_AVIATION;

	private static Image airbus = new Image("/application/source/Images/flugzeugrechts.png");
	private static Image boeing = new Image("/application/source/Images/flugzeugYamaiunten.png");
	private static Image eclipse_aviation = new Image("/application/source/Images/flugzeugYamai.png");
	
	public static ImageView getImageView(PlaneType plane) {
		ImageView iv1 = null;
		
		switch (plane) {
			case AIRBUS:
				iv1 = new ImageView(PlaneType.airbus);
				System.out.println("das ist ein Airbus");
				break;
		
			case BOEING:
				iv1 = new ImageView(PlaneType.boeing);
				System.out.println("das ist eine Boeing");
				break;
	
			case ECLIPSE_AVIATION:
				iv1 = new ImageView(PlaneType.eclipse_aviation);
				System.out.println("das ist eine Eclipse-Aviation");
				break;
				
			default:
				System.out.println("kein bild gefunden");
				break;
		}
		return iv1;
	}
}
