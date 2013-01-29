package se.mju.stackanalyzer.util;

import java.util.Comparator;

public class Tuple<T1, T2> {
	private T1 first;
	private T2 second;
	
	public Tuple(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}
	
	public T1 getFirst() {
		return first;
	}
	public void setFirst(T1 first) {
		this.first = first;
	}
	
	public T2 getSecond() {
		return second;
	}
	public void setSecond(T2 second) {
		this.second = second;
	}

	@Override
	public String toString() {
		return "<" + getFirst() + "," + getSecond() + ">";
	}

	public static <T1 extends Comparable<T1>,T2 extends Comparable<T2>> Comparator<Tuple<T1, T2>>getComparatorForSecondFirst() {
		return new Comparator<Tuple<T1,T2>>() {
			@Override public int compare(Tuple<T1, T2> o1, Tuple<T1, T2> o2) {
				int compareSecond = -o1.getSecond().compareTo(o2.getSecond());
				if (compareSecond != 0) {
					return compareSecond;
				}
				return o1.getFirst().compareTo(o2.getFirst());
			}
		};
	}
}
