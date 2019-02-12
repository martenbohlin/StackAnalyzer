package se.mju.stackanalyzer.ui;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import se.mju.stackanalyzer.StackTracesStatistics;
import se.mju.stackanalyzer.model.StackTrace;
import se.mju.stackanalyzer.model.ThreadState;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
	private ContextMenu contextMenu;
	private StacksPane contextMenuElement;
	// Start with filter that fixes reflection optimizations
	private Predicate<StackTraceElement> methodFilter = e -> !e.getClassName().startsWith("sun.reflect");
	private final List<ThreadState> unfilteredStacks;
	private Predicate<ThreadState> threadFilter = ts -> true;


	public StackpaneController(List<ThreadState> stacks) {
        StackTracesStatistics stats = new StackTracesStatistics(filter(stacks));
        StacksPane rootStack = new StacksPane(stats.getStatForRoot(), stats);
        this.resizePane = new ChildResizePane(rootStack);

        unfilteredStacks = stacks;
		root = rootStack;
		currentRoot = root;
		
		root.addMouseListener(this);
		
		contextMenu = new ContextMenu();
		MenuItem cmItem1 = new MenuItem("Exclude this class");
		cmItem1.setOnAction(e -> filterClass(contextMenuElement.getStackTrace().getClassName()));
		contextMenu.getItems().add(cmItem1);

		cmItem1 = new MenuItem("Exclude < 30 deep");
		cmItem1.setOnAction(e -> filterDepth());
		contextMenu.getItems().add(cmItem1);
	}

	public void handle(MouseEvent mouseEvent) {
		if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
			currentRoot = getStacksPane(mouseEvent);
			resizePane.zoominOn(currentRoot, duration);
		} else if(mouseEvent.getButton().equals(MouseButton.SECONDARY)){
			contextMenuElement = getStacksPane(mouseEvent);
			contextMenu.show(root ,mouseEvent.getScreenX(),mouseEvent.getScreenY());
		}
	}
	
	private void filterClass(String className) {
		Predicate<StackTraceElement> classFilter = e -> !e.getClassName().equals(className);
		methodFilter = methodFilter.and(classFilter);

		updateFiltered();
	}

	private void filterDepth() {
		threadFilter = threadFilter.and(it -> it.getElements().size() > 30);

		updateFiltered();
	}

	private void updateFiltered() {
		StackTracesStatistics stats = new StackTracesStatistics(filter(unfilteredStacks));
		root = new StacksPane(stats.getStatForRoot(), stats);
		this.resizePane.setChild(root);
		root.addMouseListener(this);
	}

	private List<ThreadState> filter(List<ThreadState> stacks) {
		return stacks.stream()
				.filter(threadFilter)
				.map(threadState -> threadState.filtered(methodFilter))
				.collect(Collectors.toList());
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

	public Node getNode() {
		return resizePane;
	}
}
