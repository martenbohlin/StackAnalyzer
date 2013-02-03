package se.mju.stackanalyzer.ui;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class StackpaneController implements EventHandler<MouseEvent> {
	private final ChildResizePane resizePane;
	private final StacksPaneFx root;
	private StacksPaneFx currentRoot;

	public StackpaneController(ChildResizePane resizePane, StacksPaneFx root) {
		this.resizePane = resizePane;
		this.root = root;
		currentRoot = root;
		
		root.addMouseListener(this);
	}

	public void handle(MouseEvent mouseEvent) {
		if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
			currentRoot = getStacksPane(mouseEvent);
			if (currentRoot.getStackTrace() == null) {
				return;
			}
			
			Duration duration = Duration.millis(500);
			resizePane.zoominOn(currentRoot, duration);
		}
	}

	private StacksPaneFx getStacksPane(MouseEvent mouseEvent) {
		Node n = (Node) mouseEvent.getSource();
		while (!(n instanceof StacksPaneFx) && n!=null) {
			n = n.getParent();
		}
		return (StacksPaneFx) n;
	}



}
