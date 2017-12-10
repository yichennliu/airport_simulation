package application;

import application.model.Plane;
import javafx.animation.PathTransition;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Path;

public class ViewPlane {
	private ImageView imageview;
	private DynamicPathTransition pathTransition;
	private PlaneType type; 
	
public ViewPlane() {
	this.type = PlaneType.getRandomType();
	this.imageview = this.type.getImageView();
}
	public ImageView getImageview() {
		return imageview;
	}

	public DynamicPathTransition getDynamicPathTransition() {
		return this.pathTransition;
	}
	public void setDynamicPathTransition(DynamicPathTransition pt) {
		this.pathTransition = pt;
	}
	public PlaneType getType() {
		return type;
	}
	public void setType(PlaneType type) {
		this.type = type;
	}
	
	
	
	

}
