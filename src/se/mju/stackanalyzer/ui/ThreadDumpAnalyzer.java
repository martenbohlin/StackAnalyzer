package se.mju.stackanalyzer.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ThreadDumpAnalyzer extends Application {
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
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method22(Fake.java:2)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method22(Fake.java:2)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method22(Fake.java:2)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method22(Fake.java:2)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method23(Fake.java:3)\n" + 
			"	at se.mju.test.Fake.method22(Fake.java:2)\n" + 
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
			"	at se.mju.test.Fake.method3(Fake.java:4)\n" + 
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
    	FXMLLoader fxmlLoader = new FXMLLoader();
    	Parent root = (Parent) fxmlLoader.load(getClass().getResource("mainScreen.fxml").openStream());

    	ThreadDumpAnalyzerController controller = fxmlLoader.getController();
    	
    	List<String> unnamed = getParameters().getUnnamed();
    	if (unnamed.size() > 0) {
    		controller.open(traces);
//    		controller.open(new File(unnamed.get(0)));
    	}
    	
    	primaryStage.setTitle("Thread Dump Analyzer");
	    primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
	
	public static void main(String[] args) throws IOException {
		launch(args);
		//final List<ThreadState> threadStates = new ThreadDumpParser(new FileReader(traces)).parseAll();
		
	}
}
