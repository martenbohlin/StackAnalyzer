package se.mju.stackanalyzer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import se.mju.stackanalyzer.model.StackTrace;
import se.mju.stackanalyzer.model.ThreadState;

public class ThreadDumpParserTest {

	@Test
	public void parse() throws IOException {
		String trace = 
				"\"http-thread-pool-9090(13)\" daemon prio=10 tid=0x00007f1a4801e800 nid=0x6b56 runnable [0x00007f1b069e5000]\n" + 
				"   java.lang.Thread.State: RUNNABLE\n" + 
				"	at java.lang.Object.notifyAll(Native Method)\n" + 
				"	at net.sf.ehcache.store.FrontEndCacheTier$Fault.complete(FrontEndCacheTier.java:804)\n" + 
				"	- locked <0x00000007bf13fa40> (a net.sf.ehcache.store.FrontEndCacheTier$Fault)\n" + 
				"	at net.sf.ehcache.store.FrontEndCacheTier.get(FrontEndCacheTier.java:202)\n" + 
				"	at net.sf.ehcache.Cache.searchInStoreWithStats(Cache.java:1960)\n" + 
				"	at net.sf.ehcache.Cache.get(Cache.java:1588)\n" + 
				"	at org.hibernate.cache.ehcache.internal.strategy.TransactionalEhcacheEntityRegionAccessStrategy.get(TransactionalEhcacheEntityRegionAccessStrategy.java:79)\n" + 
				"	at org.hibernate.cache.ehcache.internal.nonstop.NonstopAwareEntityRegionAccessStrategy.get(NonstopAwareEntityRegionAccessStrategy.java:133)\n" + 
				"	at org.hibernate.event.internal.DefaultLoadEventListener.loadFromSecondLevelCache(DefaultLoadEventListener.java:546)\n" + 
				"	at org.hibernate.event.internal.DefaultLoadEventListener.doLoad(DefaultLoadEventListener.java:419)\n" + 
				"";
		ThreadState x = new ThreadDumpParser(trace).parse();
		assertEquals("http-thread-pool-9090(13)", x.getThreadName());
		assertEquals(0x00007f1a4801e800L, x.getTid());
		assertEquals(0x6b56L, x.getNid());
		assertEquals(9, x.getElements().size());
		
		StackTraceElement element1 = x.getElements().get(1);
		assertEquals("net.sf.ehcache.store.FrontEndCacheTier$Fault", element1.getClassName());
		assertEquals("complete", element1.getMethodName());
		assertEquals("FrontEndCacheTier.java", element1.getFileName());
		assertEquals(804, element1.getLineNumber());

		StackTraceElement element0 = x.getElements().get(0);
		assertEquals(null, element0.getFileName());
		assertEquals(-1, element0.getLineNumber());
	}

	@Test
	public void parseAll() throws IOException {
		final String traces = 
				"\"threadName1\" daemon prio=10 tid=0x01 nid=0x01 runnable [0x01]\n" + 
				"   java.lang.Thread.State: RUNNABLE\n" + 
				"	at se.mju.test.Fake.method1(Fake.java:2)\n" + 
				"	at se.mju.test.Fake.main(Fake.java:1)\n" +
				"\n" +
				"logg statement that should be skipped\n" +
				"\"threadName1\" daemon prio=10 tid=0x01 nid=0x01 runnable [0x01]\n" + 
				"   java.lang.Thread.State: RUNNABLE\n" + 
				"	at se.mju.test.Fake.method2(Fake.java:3)\n" + 
				"	at se.mju.test.Fake.main(Fake.java:1)\n" +
				"\n" +
				"\"VM Periodic Task Thread\" prio=10 tid=0x00007f8ff01ab800 nid=0x21b7 waiting on condition\n";

		List<ThreadState> allThredStates = new ThreadDumpParser(traces).parseAll();
		assertEquals(3, allThredStates.size());
		ThreadState s1 = allThredStates.get(0);
		assertEquals("threadName1", s1.getThreadName());
		ThreadState s2 = allThredStates.get(1);
		assertEquals("threadName1", s2.getThreadName());
		
		assertEquals(2, s1.getElements().get(0).getLineNumber());
		assertEquals(1, s1.getElements().get(1).getLineNumber());
		assertEquals(3, s2.getElements().get(0).getLineNumber());
		assertEquals(1, s2.getElements().get(1).getLineNumber());

		assertEquals(s1.getStackTrace().getParent().hashCode(), s2.getStackTrace().getParent().hashCode());
		assertEquals(s1.getStackTrace().getParent(), s2.getStackTrace().getParent());
	}

	@Test
	public void parseStacktrace() throws IOException {
		final String traces = 
				"\"threadName1\" daemon prio=10 tid=0x01 nid=0x01 runnable [0x01]\n" + 
				"   java.lang.Thread.State: RUNNABLE\n" + 
				"	at se.mju.test.Fake.method1(Fake.java:2)\n" + 
				"	at se.mju.test.Fake.main(Fake.java:1)\n";

		List<ThreadState> allThredStates = new ThreadDumpParser(traces).parseAll();

		StackTrace stackTrace = allThredStates.get(0).getStackTrace();
		assertEquals("method1", stackTrace.getMethod());
		assertEquals("main", stackTrace.getParent().getMethod());
	}

}
