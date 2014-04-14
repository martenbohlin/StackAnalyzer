package se.mju.stackanalyzer.ui;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import se.mju.stackanalyzer.StackTracesStatistics;
import se.mju.stackanalyzer.model.StackTrace;
import se.mju.stackanalyzer.util.Tuple;

public class StacksPane extends Pane {
	
	private static final double STROKE_WIDTH = 2;
	private static final double BOX_HEIGHT = 20;
	public static final double BOX_MARGIN = 0;
	private StackTrace stackTrace;
	private Rectangle rect;
	private float invokationsComparedToParent;
	private Label label;
	private Label invokations;
	private StackPane levelComponents = new StackPane();
	private Color color;

	public StacksPane(StackTrace stackTrace, float invakationsComparedToParent, final StackTracesStatistics stats) {
		this.stackTrace = stackTrace;
		setPrefSize(600, 400);
		
        rect = RectangleBuilder.create()
                .height(BOX_HEIGHT)
                //.arcHeight(15).arcWidth(15)
                .stroke(Color.DARKBLUE).strokeWidth(STROKE_WIDTH)
                .build();

        if (stackTrace != null) {
        	label = new Label(stackTrace.getMethod());
        	//label.setTooltip(new Tooltip(stackTrace.getFullyQualifiedName()));
        	
        	invokations = new Label("" + stats.getInvokations(stackTrace));
        } else {
        	label = new Label("");
        	invokations = new Label("");
        }
        
        getChildren().add(rect);
        getChildren().add(levelComponents);
        
        levelComponents.getChildren().add(invokations);
        StackPane.setAlignment(invokations, Pos.TOP_LEFT);
        invokations.setTextFill(Color.GRAY);
        //invokations.setStyle("-fx-font-size: 80%;"); // Gives out of mem when processing css
        
        levelComponents.getChildren().add(label);
        StackPane.setAlignment(label, Pos.BOTTOM_CENTER);
        //levelComponents.setStyle("-fx-border-color: green;");
        if (stackTrace != null) {
        	Tooltip.install(levelComponents, new Tooltip(stackTrace.getFullyQualifiedName()));
        }
        
        this.invokationsComparedToParent = invakationsComparedToParent;
        for (Tuple<StackTrace, Float> x: stats.getStatForChildrens(stackTrace)) {
        	StackTrace child = x.getFirst();
        	float childInvakationsComparedToMe = x.getSecond();
        	
        	StacksPane childPane = new StacksPane(child, childInvakationsComparedToMe, stats);
        	getChildren().add(childPane);
        }
        color = StackpaneController.getColor(stackTrace);
	}
	
	public void addMouseListener(StackpaneController controller) {
		//label.setOnMouseClicked(controller);
		levelComponents.setOnMouseClicked(controller);
		for (Node child : getChildren()) {
			if (child instanceof StacksPane) {
				StacksPane stackPane = (StacksPane) child;
				stackPane.addMouseListener(controller);
			}
		}
	}
	
	@Override
	public void resize(double width, double height) {
		resizeAndReturnRectHeight(width, height);
	}

	private double resizeAndReturnRectHeight(double width, double height) {
		super.resize(width, height);
		if (stackTrace == null) {
			height += BOX_HEIGHT; // Hide parts of the artificial root box
		}
		levelComponents.resize(width, BOX_HEIGHT);
		levelComponents.setLayoutY(height - BOX_HEIGHT);
		
		if (width < 5) {
			levelComponents.setVisible(false);
		} else {
			levelComponents.setVisible(true);
		}
		

		double x = 0;
		List<StacksPane> schildren = getChildStacksPanes();
		int childCount = schildren.size();
		double childHeight = height- BOX_HEIGHT - BOX_MARGIN;
		double maxChildRextHeight = 0;
		for (StacksPane childPane : schildren) {
			double childWidth = width * childPane.invokationsComparedToParent - BOX_MARGIN * (childCount - 1) / childCount;
			if (childWidth > 1 && childHeight > -BOX_HEIGHT) {
				childPane.setLayoutX(x);
				double childRectHeight = childPane.resizeAndReturnRectHeight(childWidth, childHeight);
				maxChildRextHeight = max(maxChildRextHeight, childRectHeight);
				
				x += childWidth + BOX_MARGIN;
				childPane.setVisible(true);
			} else {
				childPane.setVisible(false);
			}
		}
		
		double rectHeight = maxChildRextHeight + BOX_HEIGHT + BOX_MARGIN;
		rect.setWidth(width);
		rect.setHeight(rectHeight);
		rect.setLayoutY(height - rectHeight);
		
		Stop[] stops = new Stop[] {new Stop(0, color), new Stop(1, Color.WHITE)};
		LinearGradient lg1 = new LinearGradient(0, rectHeight, 0, rectHeight-4*BOX_HEIGHT, false, CycleMethod.NO_CYCLE, stops);
		rect.setFill(lg1);
		
		return rectHeight;
	}
	
	@Override
	protected void layoutChildren() {
		// The work is done in resize. Do not let default implementation mess that up
	}
	
	private List<StacksPane> getChildStacksPanes() {
		ObservableList<Node> children = getChildren();
		List<StacksPane> schildren = new ArrayList<>(children.size());
		for (Node child : children) {
			if (child instanceof StacksPane) {
				schildren.add((StacksPane) child);
			}
		}
		return schildren;
	}

	public StackTrace getStackTrace() {
		return stackTrace;
	}

	public Stream<StacksPane> asStream() {
		List<StacksPane> panes = new ArrayList<>();
		asStream(panes);
		return panes.stream();
	}

	private void asStream(List<StacksPane> panes) {
		panes.add(this);
		for (StacksPane child : getChildStacksPanes()) {
			panes.add(child);
		}
	}
}
