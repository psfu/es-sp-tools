package search.plugins.functions;

import org.elasticsearch.common.settings.Settings;

import search.plugins.filter.SpActionFilterUtil;
import search.plugins.functions.auth.Auther;
import search.plugins.functions.auth.SimpleAuther;
import search.plugins.functions.logger.ActionLogger;
import search.plugins.functions.logger.CachedFileLogger;
import search.plugins.functions.logger.LogConfiguration;

/**
 * for single thread using
 * 
 * @author vv
 *
 */
public class Functions {

	static Functions fc = null;

	public static Functions getFunctions(Settings settings) {
		if (fc == null) {
			//{client.type=node, cluster.name=my-local, http.cors.allow-credentials=true, http.cors.allow-origin=/.*/, http.cors.enabled=true, http.type.default=netty4, network.host=0.0.0.0, node.name=Ve_y1qO, path.home=D:\_workspace\ws005\es5test\target\classes, path.logs=D:\_workspace\ws005\es5test\target\classes\logs, transport.type.default=netty4}
			
			
			LogConfiguration logConf = new LogConfiguration(settings.get("cluster.name"), settings.get("node.name"), settings.get("path.logs"), settings.get("path.home"));
			fc = new Functions(settings, logConf);
			
			SpActionFilterUtil.init();
		}
		return fc;
	}

	final Settings settings;
	final LogConfiguration logConf;

	private Functions(Settings settings, LogConfiguration logConf) {
		super();
		this.settings = settings;
		this.logConf = logConf;

	}

	Auther auther;
	ActionLogger actionLogger;

	public Auther getAuther() {
		if (this.auther == null) {
			auther = new SimpleAuther(settings);
		}
		return auther;
	}

	public ActionLogger getActionLogger() {
		if (this.actionLogger == null) {
			actionLogger = new CachedFileLogger(settings, logConf);
		}
		return actionLogger;
	}
}
