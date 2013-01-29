package se.mju.stackanalyzer.ui;

import java.io.IOException;
import java.util.List;

import se.mju.stackanalyzer.StackTracesStatistics;
import se.mju.stackanalyzer.ThreadDumpParser;
import se.mju.stackanalyzer.model.ThreadState;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class ThreadDumpAnalyzerFX extends Application {
	private static final String traces = 
			"\"threadName1\" daemon prio=10 tid=0x01 nid=0x01 runnable [0x01]\n" + 
			"   java.lang.Thread.State: RUNNABLE\n" + 
			"	at se.mju.test.Fake.rRRRRRRRRRRRRRRRRRRRRRRR(Fake.java:2)\n" + 
			"	at se.mju.test.Fake.main(Fake.java:1)\n" +
			"\n" +
			"\"threadName1\" daemon prio=10 tid=0x01 nid=0x01 runnable [0x01]\n" + 
			"   java.lang.Thread.State: RUNNABLE\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method22(Fake.java:2)\n" + 
			"	at se.mju.test.Fake.main(Fake.java:1)\n" +
			"\n" +
			"\"threadName1\" daemon prio=10 tid=0x01 nid=0x01 runnable [0x01]\n" + 
			"   java.lang.Thread.State: RUNNABLE\n" + 
			"	at se.mju.test.Fake.method1(Fake.java:2)\n" + 
			"	at se.mju.test.Fake.main(Fake.java:1)\n" +
			"\n" +
			"\"threadName1\" daemon prio=10 tid=0x01 nid=0x01 runnable [0x01]\n" + 
			"   java.lang.Thread.State: RUNNABLE\n" + 
			"	at se.mju.test.Fake.method4(Fake.java:4)\n" + 
			"	at se.mju.test.Fake.main(Fake.java:1)\n" +
			"\n" +
			"\"threadName1\" daemon prio=10 tid=0x01 nid=0x01 runnable [0x01]\n" + 
			"   java.lang.Thread.State: RUNNABLE\n" + 
			"	at se.mju.test.Fake.main(Fake.java:1)\n" +
			"\n" +
			"\"threadName1\" daemon prio=10 tid=0x01 nid=0x01 runnable [0x01]\n" + 
			"   java.lang.Thread.State: RUNNABLE\n" + 
			"	at se.mju.test.Fake.method4(Fake.java:4)\n" + 
			"	at se.mju.test.Fake.method1(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.main(Fake.java:1)\n";

    @Override
    public void start(Stage primaryStage) throws IOException {
    	Parent root = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
        primaryStage.setTitle("Thread Dump Analyzer");
        BorderPane pane = (BorderPane) root.lookup("#stackPane");
        
        List<ThreadState> stacks = new ThreadDumpParser(traces).parseAll();
		StackTracesStatistics stats = new StackTracesStatistics(stacks);
		StacksPaneFx stacksPane = new StacksPaneFx(null, 1, stats);
		
		BorderPane.setAlignment(stacksPane, Pos.TOP_LEFT);
		pane.setCenter(stacksPane);
        
	    primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
	
	public static void main(String[] args) throws IOException {
		launch(args);
		//final List<ThreadState> threadStates = new ThreadDumpParser(new FileReader(traces)).parseAll();
		
	}
}
