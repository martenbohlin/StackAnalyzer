package se.mju.stackanalyzer.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ParallelTransitionBuilder;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.util.Duration;
import se.mju.stackanalyzer.StackTracesStatistics;
import se.mju.stackanalyzer.model.StackTrace;
import se.mju.stackanalyzer.util.Tuple;

public class StacksPaneFx extends Pane implements EventHandler<MouseEvent>{
	
	private static final double STROKE_WIDTH = 3;
	private static final double BOX_HEIGHT = 40;
	private static final double BOX_MARGIN = 5;
	private final StackTracesStatistics stats;
	private StackTrace stackTrace;
	private Rectangle rect;
	private float invakationsComparedToParent;
	private Label label;
	private Group levelComponents = new Group();

	public StacksPaneFx(StackTrace stackTrace, float invakationsComparedToParent, final StackTracesStatistics stats) {
		this.stackTrace = stackTrace;
		this.invakationsComparedToParent = invakationsComparedToParent;
		this.stats = stats;
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
        getChildren().add(label);
        getChildren().add(levelComponents);
        
        label.setOnMouseClicked(this);
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
			height += BOX_HEIGHT	; // Hide two thirds of the artifial root box
		}
		levelComponents.resize(width, height);
		rect.setWidth(width);
		rect.setLayoutY(height - BOX_HEIGHT);
		label.resize(width, BOX_HEIGHT);
		label.setLayoutY(height - BOX_HEIGHT);
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

    public void handle(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
        	System.out.println("clicked " + StacksPaneFx.this.stackTrace.getMethod());
        	Parent p = getParent();
        	Duration duration = Duration.millis(3000);
        	int levels = 0;
        	ParallelTransitionBuilder ptb = ParallelTransitionBuilder.create();
        	while (p instanceof StacksPaneFx) {
        		StacksPaneFx parent = (StacksPaneFx) p;
            	System.out.println("   parent " + p);

				FadeTransition ft = new FadeTransition(duration, parent.levelComponents);
        		ft.setFromValue(1.0);
        		ft.setToValue(0.1);
        		ft.play();
//        		ptb.children(ft);
        		
        		p = p.getParent();
        		levels++;
        	}
        	
        	TranslateTransition tt = new TranslateTransition(duration, p);
        	tt.setByY((BOX_HEIGHT + BOX_MARGIN) * (levels - 1));
        	ptb.children(tt);
        	ptb.build().play();
        }
    }

//	private static class StackFrame extends java.awt.Component {
//		private final StackTrace stackTrace;
//		
//		public StackFrame(StackTrace stackTrace, int x, int y, int height) {
//			this.stackTrace = stackTrace;
//			setBounds(x, y, FRAME_WIDTH, height);
//			setBackground(Color.red);
//		}
//		@Override
//		public void paint(Graphics g) {
//			super.paint(g);
//			Graphics2D g2d = (Graphics2D) g;
//			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//			g2d.setColor(Color.red);
//			g2d.fillRect(0, 0, getSize().width, getSize().height);
//			
//			g2d.rotate(Math.PI/2);
//			g2d.setColor(Color.black);
//			g2d.drawString(stackTrace.getMethod(), 3, -3);
//		}
//	}
}
