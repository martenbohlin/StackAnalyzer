package se.mju.stackanalyzer.model;

import java.util.List;
import java.util.Map;

import se.mju.stackanalyzer.util.HashMapWithDefaultValue;

public class StackTrace implements Comparable<StackTrace> {
	private final StackTrace parent;
	private final String className;
	private final String method;
	
	private final int hashCode;

	public StackTrace(StackTrace parent, String className, String method) {
		this.parent = parent;
		this.className = className;
		this.method = method;
		
		hashCode = className.hashCode() + method.hashCode() * 13 + (parent == null ? 0 : parent.hashCode() * 47); 
	}
	
	public String getClassName() {
		return className;
	}
	public String getMethod() {
		return method;
	}
	public StackTrace getParent() {
		return parent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof StackTrace)) {
			return false;
		}
		StackTrace s2 = (StackTrace) obj;
		return method.equals(s2.method) && method.equals(s2.method) && 
				(parent == null ? s2.parent == null : parent.equals(s2.parent));
	};
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public String toString() {
		return getClassName() + "." + getMethod() + "\n" + (parent != null ? parent.toString() : "");
	}
	
	public int getDepth() {
		if (parent == null) {
			return 1;
		}
		return 1 + parent.getDepth();
	}

	public static StackTrace create(List<StackTraceElement> elements) {
		StackTrace trace = null;
		for (int i=elements.size() - 1; i>=0; i--) {
			StackTraceElement stackTraceElement = elements.get(i);
			trace = cache.get(new StackTrace(trace, stackTraceElement.getClassName(), stackTraceElement.getMethodName()));
		}
		return trace;
	}

	private static Map<StackTrace, StackTrace> cache = new HashMapWithDefaultValue<StackTrace, StackTrace>() {
		private static final long serialVersionUID = 1L;

		@Override public StackTrace getDefault(StackTrace key) {
			return key;
		}
	};

	@Override
	public int compareTo(StackTrace o) {
		if (o == null) {
			return 1;
		}
		int c = className.compareTo(o.className);
		if (c != 0) {
			return c;
		}
		int m = method.compareTo(o.method);
		if (m != 0) {
			return m;
		}
		if (parent == null) {
			return o.parent == null ? 0 : -1;
		}
		return parent.compareTo(o.parent);
	}

	public String getFullyQualifiedName() {
		return getClassName() + "." + getMethod();
	}
}
