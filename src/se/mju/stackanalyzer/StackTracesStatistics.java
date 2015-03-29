package se.mju.stackanalyzer;

import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import se.mju.stackanalyzer.model.StackTrace;
import se.mju.stackanalyzer.model.ThreadState;
import se.mju.stackanalyzer.util.HashMapWithDefaultValue;

public class StackTracesStatistics {
	private static Object rootKey = new Object();
	private List<ThreadState> threadStates;
	// Maps a parent to the child trace and count for that trace
	private MapMapInt<StackTrace,StackTrace> stackTraces = new MapMapInt<>();
	private IntMap<StackTrace> invocations = new IntMap<>();

	public StackTracesStatistics(List<ThreadState> threadStates) {
		this.threadStates = threadStates;
		for (ThreadState ts: this.threadStates) {
			StackTrace st = ts.getStackTrace();
			if (st != null) {
				invocations.get(rootKey).incrementAndGet();
			}
			while (st != null) {
				invocations.get(st).incrementAndGet();
				stackTraces.get(st.getParent()).get(st).incrementAndGet();
				st = st.getParent();
			}
		}
	}

	public StackTraceStatistics getStatForRoot() {
		return new StackTraceStatistics(null, 1, 1);
	}
	
	public SortedSet<StackTraceStatistics> getStatForChildrens(StackTrace parent) {
		SortedSet<StackTraceStatistics> ans = new TreeSet<>();
		IntMap<StackTrace> childStats = stackTraces.get(parent);
		
		int rootInvokations = getInvokations(null);
		int parentInvokations = getInvokations(parent);
		for (Entry<StackTrace, AtomicInteger> x: childStats.entrySet()) {
			int invokations = x.getValue().get();
			ans.add(new StackTraceStatistics(x.getKey(), 
					invokations / (float)parentInvokations,
					invokations / (float)rootInvokations)
					);
		}
		
		return ans;
	}
	public int getInvokations(StackTrace trace) {
		return invocations.get(trace == null ? rootKey : trace).get();
	}

	private static class MapMapInt<K1,K2> extends HashMapWithDefaultValue<K1,IntMap<K2>> {
		@Override	public IntMap<K2> getDefault(K1 key) {
			return new IntMap<>(); 
		}
		
		@Override
		public IntMap<K2> get(Object key) {
			if (key == null) {
				return super.get(rootKey);
			} else {
				return super.get(key);
			}
		}
	}

	private static class IntMap<K> extends HashMapWithDefaultValue<K,AtomicInteger> {
		@Override	public AtomicInteger getDefault(K key) {
			return new AtomicInteger(); 
		}
	}

}
