package se.mju.stackanalyzer.util;

public class StopWatch {
	long t0 = System.currentTimeMillis();
	long lastLap = t0;
	
	public void startNewLapAndPrintLapTime(String name) {
		long t1 = System.currentTimeMillis();
		System.out.format("Time %s: %d\n", name, (t1 - lastLap)/1000);
		lastLap = t1;
	}

}
