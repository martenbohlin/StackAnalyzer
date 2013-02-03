package se.mju.stackanalyzer.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import se.mju.stackanalyzer.StackTracesStatistics;
import se.mju.stackanalyzer.model.StackTrace;
import se.mju.stackanalyzer.util.Tuple;

public class StacksPaneFx extends Pane {
	
	private static final double STROKE_WIDTH = 3;
	private static final double BOX_HEIGHT = 40;
	public static final double BOX_MARGIN = 5;
	private StackTrace stackTrace;
	private Rectangle rect;
	private float invakationsComparedToParent;
	private Label label;
	private StackPane levelComponents = new StackPane();

	public StacksPaneFx(StackTrace stackTrace, float invakationsComparedToParent, final StackTracesStatistics stats) {
		this.stackTrace = stackTrace;
		this.invakationsComparedToParent = invakationsComparedToParent;
		for (Tuple<StackTrace, Float> x: stats.getStatForChildrens(stackTrace)) {
			StackTrace child = x.getFirst();
			float childInvakationsComparedToMe = x.getSecond();
			
			StacksPaneFx childPane = new StacksPaneFx(child, childInvakationsComparedToMe, stats);
			getChildren().add(childPane);
		}
		setPrefSize(600, 400);
		
        rect = RectangleBuilder.create()
                .x(0).y(0)
                .height(BOX_HEIGHT)
                .arcHeight(6).arcWidth(6)
                .fill(Color.CYAN).stroke(Color.DARKBLUE).strokeWidth(STROKE_WIDTH)
                .build();

        
        if (stackTrace != null) {
        	label = new Label(stackTrace.getMethod());
        	label.setAlignment(Pos.BASELINE_CENTER);
        	label.setTooltip(new Tooltip(stackTrace.toString()));
        } else {
        	label = new Label("");
        }
        
        levelComponents.getChildren().add(rect);
        levelComponents.getChildren().add(label);
        getChildren().add(levelComponents);
        levelComponents.setStyle("-fx-border-color: red;");
        
	}
	
	public void addMouseListener(StackpaneController controller) {
		label.setOnMouseClicked(controller);
		rect.setOnMouseClicked(controller);
		for (Node child : getChildren()) {
			if (child instanceof StacksPaneFx) {
				StacksPaneFx stackPane = (StacksPaneFx) child;
				stackPane.addMouseListener(controller);
			}
		}
	}
	
	@Override
	public void resize(double width, double height) {
		super.resize(width, height);
	}
	
	@Override
	protected void layoutChildren() {
		double width = getWidth();
		double height = getHeight();
		if (stackTrace == null) {
			height += BOX_HEIGHT	; // Hide two thirds of the artificial root box
		}
		levelComponents.resize(width, BOX_HEIGHT);
		levelComponents.setLayoutY(height - BOX_HEIGHT);
		levelComponents.setLayoutY(height - BOX_HEIGHT);
		rect.setWidth(width);
//		rect.setLayoutY(height - BOX_HEIGHT);
//		label.resize(width, BOX_HEIGHT);
//		label.setLayoutY(height - BOX_HEIGHT);
		double x = 0;
		for (Node child : getChildren()) {
			if (child instanceof StacksPaneFx) {
				StacksPaneFx childPane = (StacksPaneFx) child;
				double childWidth = width * childPane.invakationsComparedToParent;
				childPane.resizeRelocate(x, 0, childWidth, height- BOX_HEIGHT - BOX_MARGIN);
				x += childWidth + BOX_MARGIN;
			}
		}
	}

	public StackTrace getStackTrace() {
		return stackTrace;
	}
}
