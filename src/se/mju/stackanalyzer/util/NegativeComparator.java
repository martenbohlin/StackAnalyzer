package se.mju.stackanalyzer.util;

import java.util.Comparator;

public class NegativeComparator<T> implements Comparator<T>{

	private Comparator<T> baseComparator;

	public NegativeComparator(Comparator<T> baseComparator) {
		this.baseComparator = baseComparator;
	}

	@Override
	public int compare(T o1, T o2) {
		return -baseComparator.compare(o1, o2);
	}
}
