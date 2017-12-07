package application;

import application.model.Plane;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Path;

public class ViewPlane {
	private ImageView imageview;
	private Path path; 
	private PlaneType type; 
	
public ViewPlane() {
	this.type = PlaneType.getRandomType();
	this.imageview = this.type.getImageView();
	this.path = new Path();
	
}
	public ImageView getImageview() {
		return imageview;
	}

	public Path getPath() {
		return path;
	}
	public void setPath(Path path) {
		this.path = path;
	}
	public PlaneType getType() {
		return type;
	}
	public void setType(PlaneType type) {
		this.type = type;
	}
	
	
	
	

}
