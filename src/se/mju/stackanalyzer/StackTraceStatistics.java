package se.mju.stackanalyzer;

import se.mju.stackanalyzer.model.StackTrace;

public class StackTraceStatistics implements Comparable<StackTraceStatistics>{

	private StackTrace stacktrace;
	private float invakationsComparedToParent;
	private float invakationsComparedToRoot;

	public StackTraceStatistics(StackTrace stacktrace, float childInvakationsComparedToParent, float childInvakationsComparedToRoot) {
		this.stacktrace = stacktrace;
		this.invakationsComparedToParent = childInvakationsComparedToParent;
		this.invakationsComparedToRoot = childInvakationsComparedToRoot;
	}

	public StackTrace geftStacktace() {
		return stacktrace;
	}
	
	public float getInvakationsComparedToParent() {
		return invakationsComparedToParent;
	}
	
	public float getInvakationsComparedToRoot() {
		return invakationsComparedToRoot;
	}
	
	@Override
	public int compareTo(StackTraceStatistics o) {
		int compareInvokations = -Float.compare(invakationsComparedToParent, o.invakationsComparedToParent);
		if (compareInvokations != 0) {
			return compareInvokations;
		}
		return stacktrace.compareTo(o.stacktrace);
	}
	
}
