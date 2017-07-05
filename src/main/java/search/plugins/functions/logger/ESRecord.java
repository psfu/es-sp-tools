/*
 * Licensed to es-sp-tools under one or more contributor
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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
