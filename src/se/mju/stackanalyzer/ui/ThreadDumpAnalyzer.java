package se.mju.stackanalyzer.ui;

import java.awt.BorderLayout;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import se.mju.stackanalyzer.StackTracesStatistics;
import se.mju.stackanalyzer.ThreadDumpParser;
import se.mju.stackanalyzer.model.ThreadState;

public class ThreadDumpAnalyzer extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final String traces = 
			"\"threadName1\" daemon prio=10 tid=0x01 nid=0x01 runnable [0x01]\n" + 
			"   java.lang.Thread.State: RUNNABLE\n" + 
			"	at se.mju.test.Fake.method1(Fake.java:2)\n" + 
			"	at se.mju.test.Fake.main(Fake.java:1)\n" +
			"\n" +
			"\"threadName1\" daemon prio=10 tid=0x01 nid=0x01 runnable [0x01]\n" + 
			"   java.lang.Thread.State: RUNNABLE\n" + 
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

	public ThreadDumpAnalyzer(StackTracesStatistics stackTracesStatistics) {
		super(new BorderLayout());
		add(new StacksPane(stackTracesStatistics), BorderLayout.CENTER); 
	}
	
	public static void main(String[] args) throws IOException {
		final List<ThreadState> threadStates = new ThreadDumpParser(new FileReader(args[0])).parseAll();
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(threadStates);
            }
        });
	}

	protected static void createAndShowGUI(List<ThreadState> threadStates) {
		StackTracesStatistics stackTracesStatistics = new StackTracesStatistics(threadStates);

		JFrame frame = new JFrame("Stack Analyzer");

		frame.getContentPane().add(new ThreadDumpAnalyzer(stackTracesStatistics));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.pack();
		frame.setVisible(true);
	}

}
