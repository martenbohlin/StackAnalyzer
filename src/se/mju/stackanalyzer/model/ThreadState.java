package se.mju.stackanalyzer.model;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class ThreadState {

	private long tid;
	private long nid;
	private String threadName;
	private State state;
	private List<StackTraceElement> elements = new ArrayList<>();
	private StackTrace stackTrace;
	
	public ThreadState() {
	}
	
	private ThreadState(ThreadState template, List<StackTraceElement> elements) {
		this.tid = template.tid;
		this.nid = template.nid;
		this.state = template.state;
		this.elements = elements;
	}

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
			elements = null; // We are not allowed to update this any more
		}
		return stackTrace;
	}
	
	@Override
	public String toString() {
		return "'" + getThreadName() + " Tid:" + getTid() +"\n" + getStackTrace();
	}
	public ThreadState filtered(Predicate<StackTraceElement> predicate) {
		List<StackTraceElement> stacktrace = getElements().stream().filter(predicate).collect(Collectors.toList());
		return new ThreadState(this, stacktrace);
	}
}
