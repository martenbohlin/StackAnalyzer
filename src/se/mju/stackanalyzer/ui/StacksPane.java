package se.mju.stackanalyzer.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import se.mju.stackanalyzer.StackTracesStatistics;
import se.mju.stackanalyzer.model.StackTrace;
import se.mju.stackanalyzer.util.Tuple;

public class StacksPane extends JPanel {
	
	private static final int FRAME_WIDTH = 40;
	private static final int FRAME_MARGIN = 2;
	private final StackTracesStatistics stats;

	public StacksPane(StackTracesStatistics stats) {
		this.stats = stats;
		setPreferredSize(new Dimension(500, 600));
		setLayout(null);
		
		createStackFrames(null, 10, 10, 500);
		revalidate();
	}

	private void createStackFrames(StackTrace parent, int xOffset, int yOffset, int height) {
		int y = 0;
		for (Tuple<StackTrace, Float> x: stats.getStatForChildrens(parent)) {
			StackTrace stackTrace = x.getFirst();
			
			int childHeight = (int) (height * x.getSecond() - FRAME_MARGIN);
			StackFrame stackElement = new StackFrame(stackTrace, xOffset, yOffset + y, childHeight);
			add(stackElement);
			
			createStackFrames(stackTrace, xOffset + FRAME_WIDTH + FRAME_MARGIN, yOffset + y, childHeight + FRAME_MARGIN);
			
			y += childHeight + FRAME_MARGIN;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
	
	private static class StackFrame extends java.awt.Component {
		private final StackTrace stackTrace;
		
		public StackFrame(StackTrace stackTrace, int x, int y, int height) {
			this.stackTrace = stackTrace;
			setBounds(x, y, FRAME_WIDTH, height);
			setBackground(Color.red);
		}
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2d.setColor(Color.red);
			g2d.fillRect(0, 0, getSize().width, getSize().height);
			
			g2d.rotate(Math.PI/2);
			g2d.setColor(Color.black);
			g2d.drawString(stackTrace.getMethod(), 3, -3);
		}
	}
}
