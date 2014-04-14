package se.mju.stackanalyzer.ui;

import se.mju.stackanalyzer.model.StackTrace;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class StackpaneController implements EventHandler<MouseEvent> {
	private static final Color JAVA_COLOR = Color.web("e1b3ff");
	private static final Color SUN_COLOR = Color.web("e1b3ff").darker();
	private static final Color GRADLE_COLOR = Color.web("acd180");
	private static final Color AVINODE_COLOR = Color.rgb(152, 191, 12);
	private static final Color SELENIUM_COLOR = Color.LIGHTGREY;
	private static final Color DEFAULT_COLOR = Color.CYAN;
	
	private final ChildResizePane resizePane;
	private StacksPane currentRoot;
	private StacksPane root;
	private final Duration duration = Duration.millis(500);

	public StackpaneController(ChildResizePane resizePane, StacksPane root) {
		this.resizePane = resizePane;
		this.root = root;
		currentRoot = root;
		
		root.addMouseListener(this);
	}

	public void handle(MouseEvent mouseEvent) {
		if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
			currentRoot = getStacksPane(mouseEvent);
			resizePane.zoominOn(currentRoot, duration);
		}
	}

	private StacksPane getStacksPane(MouseEvent mouseEvent) {
		Node n = (Node) mouseEvent.getSource();
		while (!(n instanceof StacksPane) && n!=null) {
			n = n.getParent();
		}
		return (StacksPane) n;
	}

	public void zoomOut() {
		resizePane.zoominOn(root, duration);
	}

	public static Color getColor(StackTrace stackTrace) {
		if (stackTrace == null) {
			return Color.GRAY;
		}
		String className = stackTrace.getClassName();
		if (className.startsWith("java")) {
			return JAVA_COLOR;
		} else if (className.startsWith("com.sun.") || className.startsWith("sun.")) {
			return SUN_COLOR;
		} else if (className.startsWith("org.gradle.")) {
			return GRADLE_COLOR;
		} else if (className.startsWith("com.avinode.")) {
			return AVINODE_COLOR;
		} else if (className.startsWith("org.openqa.selenium.")) {
			return SELENIUM_COLOR;
		}
		return DEFAULT_COLOR;
	}
}
