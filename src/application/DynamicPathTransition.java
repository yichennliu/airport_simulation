package application;
import application.model.Node;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class DynamicPathTransition extends javafx.animation.Transition {
	private Node start;
	private Node end;
	private javafx.scene.Node node;
	private double offsetX;
	private double offsetY;
	private double zoomFactor;
	
	private double x1,x2,y1,y2;
	
	public void interpolate(double frac) {
		double vTX = this.x2 - this.x1;
		double vTY = this.y2 - this.y1;
		double posX = x1 + (frac*vTX);
		double posY = y1 + (frac*vTY);
		double nodeWidth = ((ImageView) this.node).getFitWidth();
		double nodeHeight = ((ImageView) this.node).getFitHeight();
		
		this.node.setTranslateX(posX-nodeWidth/2);
		this.node.setTranslateY(posY-nodeHeight/2);
		calcAngle();
	}
	
	private void calcAngle() {
		 double angle =  Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
				 
		    if(angle < 0){
		        angle += 360;
		    }
		    
		   this.node.setRotate(angle);
	}
	
	public void updateZoomAndOffset(double offsetX, double offsetY, double zoomFactor) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.zoomFactor = zoomFactor;
		updateCoords();
	}
	
	public void setStartEndNodes(Node start, Node end) {
		this.start = start;
		this.end = end;
		updateCoords();
	}
	
	private void updateCoords() {
		if(start!=null && end!=null) {
			this.x1 = start.getX()*this.zoomFactor + this.offsetX;
			this.y1 = start.getY()*this.zoomFactor + this.offsetY;
			this.x2 = end.getX()*this.zoomFactor + this.offsetX;
			this.y2 = end.getY()*this.zoomFactor + this.offsetY;
		}

	}
	
	public DynamicPathTransition(javafx.scene.Node node, Node start, Node end,double offsetX, double offsetY, double zoomFactor) {
		this.node = node;
		this.end = end;
		this.start = start;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.zoomFactor = zoomFactor;
		if(this.start!=null && this.end!=null) {
			updateCoords();
		}
	}
	
	public void setDuration(Duration duration) {
		this.setCycleDuration(duration);
	}
		
}
