package se.mju.stackanalyzer.ui;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class ClipPane extends Pane {
	private final Rectangle clip;
	
	public ClipPane() {
		clip = new Rectangle();
		setClip(clip);
	}

	public void resizeRelocate(double x, double y, double width, double height) {
		clip.resizeRelocate(x, y, width, height);
	}
	
	@Override
	protected void layoutChildren() {
		for (Node child : getChildren()) {
			child.resize(getWidth(), getHeight());
		}
	}
}
