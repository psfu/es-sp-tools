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
