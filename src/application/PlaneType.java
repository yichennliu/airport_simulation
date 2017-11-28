package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public enum PlaneType {

	AIRBUS, BOEING, ECLIPSE_AVIATION;

	public static ImageView choosePlane(PlaneType plane) {

		ImageView iv1 = null;

		switch (plane) {

		case AIRBUS:
			Image image = new Image("/application/source/Images/flugzeugrechts.png");
			iv1 = new ImageView(image);

			System.out.println("das ist ein Airbus");

			break;
			
			
			
		case BOEING:
			Image image1 = new Image("/application/source/Images/flugzeugYamaiunten.png");
			iv1 = new ImageView(image1);

			System.out.println("das ist ein Airbus");

			break;

		case ECLIPSE_AVIATION:
			Image image2 = new Image("/application/source/Images/flugzeugYamai.png");
			iv1 = new ImageView(image2);

			System.out.println("das ist ein Airbus");

			break;


		default:
			System.out.println("kein bild gefunden");
			break;

		}
		return iv1;

	}

}
