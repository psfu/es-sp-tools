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

import java.util.Properties;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.tasks.Task;

public class CachedFileLogger implements ActionLogger {
	public boolean using = true;
	public boolean useFastLog = false;

	LogConfiguration conf;

	// @Inject
	public CachedFileLogger(Settings settings, LogConfiguration conf) {
		this.conf = conf;
		CacheLoggerWriter.init(conf);
	}
	
	@Override
	public void updateSettings(Properties pp) {
		// TODO Auto-generated method stub
		String usingLog = pp.getProperty("logger.using","true");
		this.using = Boolean.parseBoolean(usingLog);
	}

	@Override
	public void log(Task task, String action, ActionRequest request, boolean isAuthed, boolean isJavaClient) {
		// request.toString();
		if (using == true) {
			ESRecord r = parseRecord(task, action, request, isAuthed, isJavaClient);
			writeCache(r);
		}
	}

	private void writeCache(ESRecord r) {
		if (using == true) {
			if (useFastLog) {
				// CacheLoggerWriter.addRecordHighSpeed(r);
				CacheLoggerWriter.addRecordMixed(r);
			} else {
				CacheLoggerWriter.addRecordInTime(r);
			}
		}
	}

	private ESRecord parseRecord(Task task, String action, ActionRequest request, boolean isAuthed, boolean isJavaClient) {
		ESRecord r = new ESRecord();
		if (!action.endsWith("bulk")) {
			r.statement = request.toString();
		} else {
			r.statement = "bulk..:";
		}

		if (request.remoteAddress() != null) {
			r.remoteIp = request.remoteAddress().toString();
		}

		r.action = action;
		r.isAuthed = isAuthed;
		r.isJavaClient = isJavaClient;
		r.startTime = System.currentTimeMillis();

		// TODO
		// search.plugins.common.Common.log0("log-->: " + r.toString());

		return r;
	}

	@Override
	public void start() {
		this.using = true;

	}

	@Override
	public void end() {
		this.using = false;

	}

	@Override
	public boolean isRunning() {
		return using;
	}

	@Override
	public boolean isUsing() {
		return using;
	}

}
