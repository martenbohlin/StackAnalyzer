package se.mju.stackanalyzer.ui;

import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class ChildResizePane extends Pane implements EventHandler<GestureEvent> {
	private final Rectangle clip;
	private double childWidth = -1;
	private double childHeight = -1;
	private Region child;
	private Region zoomedInOn;
	private double zoomFactor = 1;
	

	public ChildResizePane(Region child) {
		this.child = child;
		zoomedInOn = child;
		setPrefSize(child.prefWidth(-1), child.prefHeight(-1));
		getChildren().add(child);	
		clip = new Rectangle(-10,-10,100000,100000); // Hack to show menu
		setClip(clip);
		
		setOnZoomStarted(this);
		setOnZoom(this);
		setOnScrollStarted(this);
		setOnScroll(this);
	}
	
	public void setChild(Region child) {
		this.child = child; 
		zoomedInOn = child;
		getChildren().clear();
		getChildren().add(child);
		setNeedsLayout(true);
	}
	
	public void setChildSize(double width, double height) {
		setNeedsLayout(true);
		this.childWidth = width;
		this.childHeight = height;
	}
	
	@Override
	public void resize(double width, double height) {
		super.resize(width, height);
		updateChildSize(width, height);
	}

	private void updateChildSize(double myWidth, double myHeight) {
		Dimension2D newSize = sizeForZoomInOn(myWidth, myHeight);
		setChildSize(newSize.getWidth(), newSize.getHeight());
		layout();
		
		Point2D translation = translationForZoomInOn();
		child.setTranslateX(translation.getX());
	}
	
	@Override
	protected void layoutChildren() {
		for (Node child: getChildren()) {
			child.resize(childWidth, childHeight);
		}
	}
	
	private Dimension2D sizeForZoomInOn(double newThisWidth, double newThisHeight) {
		if (zoomedInOn.getWidth() < .00000001) { // Avoid division by zero
			return new Dimension2D(newThisWidth, newThisHeight);
		}
		
		double distanceFromBottom = childHeight - zoomedInOn.getHeight();
		double newHeight =   getHeight()/zoomFactor + distanceFromBottom;
		double newWidth = child.getWidth() * (newThisWidth/zoomedInOn.getWidth());
		return new Dimension2D(newWidth / zoomFactor, newHeight);
	}


	private Point2D translationForZoomInOn() {
		Point2D currentInSceene = zoomedInOn.localToScene(0, zoomedInOn.getHeight());
		Point2D rootInSceene = child.localToScene(0, child.getHeight());
		
		double currentXInRoot = currentInSceene.getX() - rootInSceene.getX();
		
		return new Point2D(-currentXInRoot, 0);
	}

	public Transition transitionTo(Duration duration) {
		return new ZoomInOnTransition(duration);
	}
	
	public class ZoomInOnTransition extends Transition {
		private final double startDeltaX;
		private final double startDeltaY;
		private final double startWidth;
		private final double startHeight;
		
		public ZoomInOnTransition(Duration duration) {
			Point2D newTranslate = translationForZoomInOn();

			startWidth = ChildResizePane.this.childWidth;
			startHeight = ChildResizePane.this.childHeight;
			
			
			startDeltaX = child.getTranslateX() - newTranslate.getX();
			startDeltaY = child.getTranslateY() - newTranslate.getY();
			
			setCycleDuration(duration);
		}

		@Override
		protected void interpolate(double f) {
			Dimension2D size = sizeForZoomInOn(getWidth(), getHeight());
			setChildSize(startWidth + f * (size.getWidth() - startWidth), startHeight + f * (size.getHeight() - startHeight));
			layout();
			
			Point2D newTranslate = translationForZoomInOn();
			double deltaX = startDeltaX * (1-f);
			double deltaY = startDeltaY * (1-f);
			child.setTranslateX(newTranslate.getX() + deltaX);
			child.setTranslateY(newTranslate.getY() + deltaY);
			
		}

	}

	public void zoominOn(Region node, Duration duration) {
//		zoomedInOn.setEffect(null);
		zoomedInOn = node;
		transitionTo(duration).play();
//		DropShadow dropShadow = new DropShadow(40, Color.WHITE);
//		zoomedInOn.setEffect(dropShadow);
	}

	private double previousZoomFactor;
	@Override
	public void handle(GestureEvent event) {
		if (event instanceof ZoomEvent) {
			handle((ZoomEvent) event);
		} if (event instanceof ScrollEvent) {
			handle((ScrollEvent) event); 
		}
	}
	
	public void handle(ZoomEvent event) {
		double zoomBeforeThisCall = zoomFactor;
		if (event.getEventType() == ZoomEvent.ZOOM_STARTED) {
			previousZoomFactor = zoomFactor;
		}
		zoomFactor = previousZoomFactor * event.getTotalZoomFactor();
		zoomFactor = Math.max(zoomFactor, 0.1);
		zoomFactor = Math.min(zoomFactor, 10);
		
		if (zoomFactor != zoomBeforeThisCall) {
			child.getTransforms().setAll(Transform.scale(zoomFactor, zoomFactor));

			updateChildSize(getWidth(), getHeight());
		}
	}

	public void handle(ScrollEvent event) {
		double newDx = child.getTranslateX() + event.getDeltaX();
		double newDy = child.getTranslateY() + event.getDeltaY();
		if (newDx > 0) {
			newDx = 0;
		}
		if (newDy > 50*zoomFactor) {
			newDy = 50*zoomFactor;
		}
		if (getHeight() - child.getHeight() > newDy) {
			newDy = getHeight() - child.getHeight();
		}
		if (getWidth() - child.getWidth() > newDx) {
			newDx = getWidth() - child.getWidth();
		}
		System.err.println(newDx + "," + newDy + "              " + (-newDx + getWidth() - child.getWidth()) + "," 
		+ 'y' + (getHeight()+ newDy - child.getHeight())  + " " + zoomFactor);
		child.setTranslateX(newDx);
		child.setTranslateY(newDy);
	}
}
