package se.mju.stackanalyzer;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;

import se.mju.stackanalyzer.model.ThreadState;

public class ThreadDumpParser {
	private final String whiteSpaces = " \t\n\r";

	private final PushbackReader reader;

	public ThreadDumpParser(Reader reader) {
		this.reader = new PushbackReader(reader, 1024);
	}

	public ThreadDumpParser(String trace) {
		this(new StringReader(trace));
	}

	public List<ThreadState> parseAll() throws IOException {
		List<ThreadState> ans = new ArrayList<>();
		while (true) {
			ThreadState s = parse();
			if (s == null) {
				if (reader.read() == -1) { // EOF? otherwise advance one byte in the stream
					break;
				}
			} else {
				ans.add(s);
			}
		}
		return ans;
	}

	public ThreadState parse() throws IOException {
		return pareseThreadState();
	}
	
	private ThreadState pareseThreadState() throws IOException {
		ThreadState stackTrace = new ThreadState();
		if (!parseStackTraceHeader(stackTrace)) {
			return null;
		}
		skipWhitespace();
		parseState(stackTrace);
		skipWhitespace();
		while (parseStackTraceElement(stackTrace)) {
			skipWhitespace();
		}
		return stackTrace;
	}

	private void parseState(ThreadState stackTrace) throws IOException {
		if (expect("java.lang.Thread.State:")) {
			skipWhitespace();
			String string = parseStringEndedBy(whiteSpaces);
			State state = Thread.State.valueOf(string);
			stackTrace.setState(state);
			
			if (expect(" (")) {
				@SuppressWarnings("unused")
				String comment = parseStringEndedBy("\r\n"); 
			}
		}
	}

	private void skipWhitespace() throws IOException {
		while (true) {
			int x = read();
			if (x < 0) {
				return;
			} else if (whiteSpaces.indexOf(x) < 0) {
				reader.unread(x);
				return;
			}
		}
	}

	private boolean parseStackTraceHeader(ThreadState stackTrace) throws IOException {
		String name = parseQuotedString();
		if (name == null) {
			return false;
		}
		@SuppressWarnings("unused")
		boolean daemon=expect(" daemon");

		if (!expect(" prio=")) {
			return false;
		}		
		Long prio = parseLong();
		if (prio == null) {
			return false;
		}
		
		if (!expect(" tid=")) {
			return false;
		}
		Long tid = parseLong();
		if (tid == null) {
			return false;
		}

		if (!expect(" nid=")) {
			return false;
		}
		Long nid = parseLong();
		if (nid == null) {
			return false;
		}

		if (expect(" waiting on condition") ||
				expect(" waiting for monitor entry")) {
			if (expect(" [")) {
				Long mutex = parseLong();
				if (mutex == null) {
					return false;
				}
				if (!expect("}")) {
					return false;
				}
			}
		} else if (expect(" sleeping")) {
			if (expect("[")) {
				Long mutex = parseLong();
				if (mutex == null) {
					return false;
				}
				if (!expect("}")) {
					return false;
				}
			}
		} else if (expect(" in ")) {
			String method = parseStringEndedBy(whiteSpaces);
			if (method == null) {
				return false;
			}
			if (expect(" [")) {
				parseLong();
				if (!expect("]")) {
					return false;
				}
			}
		} else if (expect(" runnable")) {
			if (expect(" [")) {
				@SuppressWarnings("unused")
				Long value = parseLong();
				if (!expect("]")) {
					return false;
				}
			}
		} else {
			throw new IOException("Unexpected end: Thred name:" + name + " Rest of line:" + parseStringEndedBy("\r\n"));
		}
		
		stackTrace.setTid(tid);
		stackTrace.setNid(nid);
		stackTrace.setThreadName(name);
		return true;
	}

	private boolean parseStackTraceElement(ThreadState stackTrace) throws IOException {
		if (expect("at ")) {
			String fullMentodName = parseStringEndedBy("(\r\n");
			int index = fullMentodName.lastIndexOf('.');
			String declaringClass = fullMentodName.substring(0, index);
			String methodName = fullMentodName.substring(index + 1);

			String fileName = null;
			int lineNumber = -1;
			if (expect("(")) {
				String fileNameAndLineNumber = parseStringEndedBy(")");
				if (fileNameAndLineNumber.startsWith("Native Method")) {
					fileName = null;
				} else {
					int lastIndexOfColon = fileNameAndLineNumber.lastIndexOf(':');
					if (lastIndexOfColon >= 0) {
						try {
							lineNumber = Integer.parseInt(fileNameAndLineNumber.substring(lastIndexOfColon+1));
							fileName = fileNameAndLineNumber.substring(0, lastIndexOfColon);
						} catch (NumberFormatException e) {
							fileName = fileNameAndLineNumber;
						}
					} else {
						fileName = fileNameAndLineNumber;
					}
				}
				if (!expect(")")) {
					return false;
				}
			}
			StackTraceElement element = new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
			stackTrace.addElement(element);
			return true;
		} else if (expect("- locked <")) {
			Long mutex = parseLong();
			if (mutex == null) {
				return false;
			}
			if (!expect(">")) {
				return false;
			}
			if (expect(" (")) {
				@SuppressWarnings("unused")
				String comment = parseStringEndedBy(")");
				if (!expect(")")) {
					return false;
				}
			}
			return true;
		} else if (expect("- waiting on <")) {
			Long mutex = parseLong();
			if (mutex == null) {
				return false;
			}
			if (!expect(">")) {
				return false;
			}
			if (expect(" (")) {
				@SuppressWarnings("unused")
				String comment = parseStringEndedBy(")");
				if (!expect(")")) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
		
	}

	private String parseQuotedString() throws IOException {
		int maxLength = 1000;
		if (!expect("\"")) {
			return null;
		}
		StringBuilder ans = new StringBuilder();
	loop:
		while (maxLength-- > 0) {
			int x = read();
			switch (x) {
			case '\n':
			case -1:
				break loop;
			case '"':
				// TODO support escaped " ?
				return ans.toString();
			default:
				ans.append((char) x);
				break;
			}
		}
		reset();
		return null;
	}
	
	private String parseStringEndedBy(String endChars) throws IOException {
		int maxLength = 1000;
		mark(maxLength);
		StringBuilder ans = new StringBuilder();
	loop:
		while (maxLength-- > 0) {
			int x = read();
			if (x == -1) {
				break loop;
			} else if (endChars.indexOf(x) >=0) {
				reader.unread(x);
				return ans.toString();
			} else {
				ans.append((char) x);
			}
		}
		reset();
		return null;
	}

	private Long parseLong() throws IOException {
		int maxLength = 1000;
		mark(maxLength);
		StringBuilder ans = new StringBuilder();
		int radix = expect("0x") ? 16 : 10;
		
		while (maxLength-- > 0) {
			int x = read();
			switch (x) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				ans.append((char) x);
				break;
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
				if (radix < 16) {
					reader.unread(x);
					if (ans.length() == 0) {
						return null;
					}
					return Long.parseLong(ans.toString(), radix);					
				} else {
					ans.append((char) x);
				}
				break;
			default:
				reader.unread(x);
				if (ans.length() == 0) {
					return null;
				}
				return Long.parseLong(ans.toString(), radix);
			}
		}
		reset();
		return null;
	}
	
	private boolean expect(String string) throws IOException {
		mark(string.length());
		for (char x: string.toCharArray()) {
			if (x != read()) {
				reset();
				return false;
			}
		}
		return true;
	}
	
	private StringBuilder consumed = null;
	private int consumedMaxLength = -1;
	
	private int read() throws IOException {
		int x = reader.read();
		if (x >= 0 && consumed != null) {
			if (consumed.length() == consumedMaxLength) {
				consumed = null;
			} else {
				consumed.append((char) x);
			}
		}
		return x;
	}

	private void mark(int maxLength) {
		consumed = new StringBuilder(maxLength);
		consumedMaxLength = maxLength;
	}

	private void reset() throws IOException {
		reader.unread(consumed.toString().toCharArray());
		consumed = null;
	}
}
