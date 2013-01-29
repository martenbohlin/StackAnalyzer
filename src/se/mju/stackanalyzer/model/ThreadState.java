package se.mju.stackanalyzer.model;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;


public class ThreadState {

	private long tid;
	private long nid;
	private String threadName;
	private State state;
	private List<StackTraceElement> elements = new ArrayList<>();
	private StackTrace stackTrace;

	public void setTid(long tid2) {
		this.tid = tid2;
	}
	public long getTid() {
		return tid;
	}
	public long getNid() {
		return nid;
	}
	public void setNid(long nid) {
		this.nid = nid;
	}
	
	public String getThreadName() {
		return threadName;
	}
	public void setThreadName(String thradName) {
		this.threadName = thradName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ThreadState) {
			return tid == ((ThreadState) obj).tid;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int)tid;
	}
	
	public void setState(State state) {
		this.state = state;
	}
	public State getState() {
		return state;
	}

	public void addElement(StackTraceElement element) {
		elements.add(element);
	}
	public List<StackTraceElement> getElements() {
		return elements;
	}

	public StackTrace getStackTrace() {
		if (stackTrace == null) {
			stackTrace = StackTrace.create(getElements());
		}
		return stackTrace;
	}
	
	@Override
	public String toString() {
		return "'" + getThreadName() + " Tid:" + getTid() +"\n" + getStackTrace();
	}
}
