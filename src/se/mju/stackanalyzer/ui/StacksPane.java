package se.mju.stackanalyzer.ui;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.paint.LinearGradientBuilder;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import se.mju.stackanalyzer.StackTracesStatistics;
import se.mju.stackanalyzer.model.StackTrace;
import se.mju.stackanalyzer.util.Tuple;

public class StacksPane extends Pane {
	
	private static final double STROKE_WIDTH = 2;
	private static final double BOX_HEIGHT = 40;
	public static final double BOX_MARGIN = 5;
	private StackTrace stackTrace;
	private Rectangle rect;
	private float invakationsComparedToParent;
	private Label label;
	private StackPane levelComponents = new StackPane();

	public StacksPane(StackTrace stackTrace, float invakationsComparedToParent, final StackTracesStatistics stats) {
		this.stackTrace = stackTrace;
		setPrefSize(600, 400);
		
        rect = RectangleBuilder.create()
                .height(BOX_HEIGHT)
                .arcHeight(15).arcWidth(15)
                .fill(Color.TRANSPARENT).stroke(Color.DARKBLUE).strokeWidth(STROKE_WIDTH)
                .build();

        if (stackTrace != null) {
        	label = new Label(stackTrace.getMethod());
        	label.setAlignment(Pos.BASELINE_CENTER);
        	label.setTooltip(new Tooltip(stackTrace.toString()));
        } else {
        	label = new Label("");
        }
        
        getChildren().add(rect);
        levelComponents.getChildren().add(label);
        getChildren().add(levelComponents);
//        label.setStyle("-fx-border-color: red;");
        
        this.invakationsComparedToParent = invakationsComparedToParent;
        for (Tuple<StackTrace, Float> x: stats.getStatForChildrens(stackTrace)) {
        	StackTrace child = x.getFirst();
        	float childInvakationsComparedToMe = x.getSecond();
        	
        	StacksPane childPane = new StacksPane(child, childInvakationsComparedToMe, stats);
        	getChildren().add(childPane);
        }
	}
	
	public void addMouseListener(StackpaneController controller) {
		label.setOnMouseClicked(controller);
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
		

		double x = 0;
		List<StacksPane> schildren = getChildStacksPanes();
		int childCount = schildren.size();
		double childHeight = height- BOX_HEIGHT - BOX_MARGIN;
		double maxChildRextHeight = 0;
		for (StacksPane childPane : schildren) {
			double childWidth = width * childPane.invakationsComparedToParent - BOX_MARGIN * (childCount - 1) / childCount;
			if (childWidth > 5 && childHeight > -BOX_HEIGHT) {
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
		
		Stop[] stops = new Stop[] {new Stop(0, Color.CYAN), new Stop(1, Color.WHITE)};
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
}
