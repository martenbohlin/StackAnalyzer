package se.mju.stackanalyzer.ui;

import javafx.animation.Transition;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class ChildResizePane extends Pane {
	private double childWidth = -1;
	private double childHeight = -1;
	private final Region child;
	private Region zoomedInOn;

	public ChildResizePane(Region child) {
		this.child = child;
		zoomedInOn = child;
		setPrefSize(child.prefWidth(-1), child.prefHeight(-1));
		getChildren().add(child);		
	}
	
	public void setChildSize(double width, double height) {
		setNeedsLayout(true);
		this.childWidth = width;
		this.childHeight = height;
	}
	
	@Override
	public void resize(double width, double height) {
		super.resize(width, height);
		double childWidth = sizeForZoomInOn(width);
		setChildSize(childWidth, height);
		child.setTranslateX(translationForZoomInOn(childWidth));
	}
	
	@Override
	protected void layoutChildren() {
		for (Node child: getChildren()) {
			child.resize(childWidth, childHeight);
		}
	}
	
	private double sizeForZoomInOn(double newThisWidth) {
		if (zoomedInOn.getWidth() < .00000001) { // Avoid division by zero
			return newThisWidth;
		}
		return child.getWidth() * (newThisWidth/zoomedInOn.getWidth());
	}


	private double translationForZoomInOn(double width) {
		Point2D currentInSceene = zoomedInOn.localToScene(0, zoomedInOn.getHeight());
		Point2D rootInSceene = child.localToScene(0, child.getHeight());

		double currentInRoot = currentInSceene.getX() - rootInSceene.getX();
		if (child.getWidth() < 0.0000001) { // Avoid division by zero
			return 0;
		}
		return -(currentInRoot/child.getWidth())*width;
	}

	public Transition transitionTo(Duration duration) {
		return new ZoomInOnTransition(duration);
	}
	
	public class ZoomInOnTransition extends Transition {
		private final double aWidth;
		private final double newWidth;
		private final double aTranslateX;
		private final double aTranslateY;
		private final double aHeight;
		private final double newHeight;
		
		public ZoomInOnTransition(Duration duration) {
			double newWidth = sizeForZoomInOn(getWidth());

			aWidth = ChildResizePane.this.childWidth;
			aHeight = ChildResizePane.this.childHeight;
			
			this.newWidth = newWidth;
			this.newHeight = aHeight;
			
			aTranslateX = child.getTranslateX();
			aTranslateY = child.getTranslateY();
			
			setCycleDuration(duration);
		}

		@Override
		protected void interpolate(double f) {
			double newTranslateX = translationForZoomInOn(newWidth);
			setChildSize(aWidth + f * (newWidth - aWidth), aHeight + f * (newHeight - aHeight));
			child.setTranslateX(aTranslateX + f * (newTranslateX - aTranslateX));
			layout();
		}

	}

	public void zoominOn(Region node, Duration duration) {
		zoomedInOn = node;
		transitionTo(duration).play();
	}
}
