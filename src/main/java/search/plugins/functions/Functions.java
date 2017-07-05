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
