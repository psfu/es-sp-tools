package search.plugins.functions.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import search.plugins.functions.logger.ESLogThread.Buffer;

public class ESRecord implements Comparable<ESRecord> {

	public String statement;
	public String action;
	public String remoteIp;
	public long startTime;
	public int executeTime;
	
	boolean isAuthed;
	boolean isJavaClient;
	//
	public boolean isInTime;

	@Override
	public int compareTo(ESRecord o) {
		return (int) (executeTime - o.executeTime);
	}

	DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.US);
	

	public void write(Buffer bb) {
		this.write(bb.getStringBuilder());
	}

	public void write(StringBuilder sb) {
		if (this.isInTime) {
			sb.append("->");
		} else {
			sb.append("");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(startTime);
		sb.append(dateFormat.format(cal.getTime()));
		sb.append(" ");
		sb.append(executeTime);
		sb.append("ms ");
		sb.append(action);
		sb.append(" ");
		sb.append(remoteIp);
		sb.append(" ");
		sb.append(isAuthed ? "authed" : "notAuthed");
		sb.append(" ");
		sb.append(isJavaClient ? "java" : "http");
		sb.append("\r\n");
		sb.append(statement);
		sb.append("\r\n");

	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		write(sb);
		return sb.toString();
	}

}
