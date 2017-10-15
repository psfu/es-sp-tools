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
package search.plugins.action;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestRequest.Method;
import org.elasticsearch.rest.RestStatus;

import search.plugins.common.Common;
import search.plugins.filter.SpActionFilterUtil;
import search.plugins.functions.Functions;
import search.plugins.functions.auth.Auther;
import search.plugins.functions.auth.Permissions;
import search.plugins.functions.logger.ActionLogger;

public class RestSpToolsAction extends BaseRestHandler {
	public void log(int level, Object o) {
		if (level > Common.logLevel) {
			Common.log0(o);
		}
	}

	String helpString = "please input: \r\n /_sp/help \r\n /_sp/auther \r\n /_sp/logger \r\n";
	String helpAutherString = "please input: \r\n /_sp/auther/start \r\n /_sp/auther/end \r\n /_sp/auther/stat\r\n /_sp/auther/auth\r\n /_sp/auther/settings\r\n";
	String helpLoggerString = "please input: \r\n /_sp/logger/start \r\n /_sp/logger/end \r\n /_sp/logger/stat\r\n  and with param:\r\n remoteInfo=true/false\r\n";
	String withPermission = "you do not have the permission to do this, please with the key. \r\n";
	
	
	@Override
	protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
		// TODO Auto-generated method stub

		final String func = request.hasParam("func") ? request.param("func") : "help";
		final String funcAction = request.hasParam("action") ? request.param("action") : "help";

		log(1, "auther:" + auther);

		RestChannelConsumer rr = null;

		switch (func) {
		case "auther":
			rr = dealAuth(request, client, funcAction);
			break;
		case "logger":
			rr = dealLogger(request, client, funcAction);
			break;
		case "help":
			rr = dealHelp(request, client);
			break;
		// case "console":
		// rr = dealConsole(request, client, action);
		// break;
		default:
			rr = dealHelp(request, client);
		}

		return rr;
	}

	// private RestChannelConsumer dealConsole(RestRequest request, NodeClient client, String action) {
	// // TODO Auto-generated method stub
	// return null;
	// }

	RestChannelConsumer dealAuth(RestRequest request, NodeClient client, String action) {
		String key = request.param("key");
		String user = request.param("user");
		String pwd = request.param("pwd");

		String remoteInfo = request.param("remoteInfo");
		String restIpMap = request.param("restIpMap");
		String clientIpMap = request.param("clientIpMap");

		RestChannelConsumer rr = channel -> {
			// RestRequest r = channel.request();
			boolean isAdmin = false;
			boolean isUser = false;
			if (key != null) {
				if (adminKey.equals(key)) {
					isAdmin = true;
				}
				if (userKey.equals(key)) {
					isUser = true;
				}
			}

			log(1, "--->@accept channel");
			boolean stat = auther.isRunning();

			String rs = "";
			switch (action) {
			case "help":
			default:
			case "stat":
				rs = "the auther is running:" + stat + " \r\n" + helpAutherString;
				break;
			case "start":

				if (isAdmin) {
					this.auther.start();
					rs = "the auther is starting.. \r\n";
				} else {
					rs = withPermission;
				}
				break;
			case "end":
				if (isAdmin) {
					this.auther.end();
					rs = "the auther is ending.. \r\n";
				} else {
					rs = withPermission;
				}
				break;
			case "auth":
				if (isUser | isAdmin) {
					boolean isAuth = this.auther.doRestAuth(request, client, user, pwd);
					rs = "the auth:" + isAuth + " \r\n";
					if (isAuth) {
						BytesRestResponse br = new BytesRestResponse(RestStatus.OK, rs);
						// header("Set-Cookie: cookiename=cookieValue; expires=" . gmstrftime("%A, %d-%b-%Y %H:%M:%S GMT", time() + (86400 * 365)) . '; path=/;
						// domain=netingcn.com');

						// Set-Cookie：customer=huangxp; path=/foo; domain=.ibm.com;
						// expires= Wednesday, 19-OCT-05 23:12:40 GMT; [secure]
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DATE, 20);

						br.addHeader("Set-Cookie", "spkey=" + auther.genKey(cal, "userName") + "; path=/; expires=" + dateFormat.format(cal.getTime()));

						channel.sendResponse(br);

						return;
					} else {
						rs += "you may input your identification info to continue \r\n";
					}

				} else {
					rs = withPermission;
				}
				break;
			case "settings":

				rs += "you can input _sp/auther/settings?restIpMap=127.0.0.1:true; to set settings";
				rs += "remoteInfo:" + SpActionFilterUtil.using + "\r\n restIpMap:" + auther.getIpListRest() + "\r\n clientIpMap:" + auther.getIpListClient()
						+ "\r\n";
				if (isAdmin) {
					if (remoteInfo != null) {
						if (remoteInfo.equals("false")) {
							SpActionFilterUtil.using = false;
							rs += " remoteInfo switch to off\r\n";
						} else if (remoteInfo.equals("true")) {
							SpActionFilterUtil.using = true;
							rs += " remoteInfo switch to on\r\n";
						} else {
							rs += " to set remoteInfo like that: remoteInfo=true/false\r\n";
						}
					}
					if (restIpMap != null) {
						//
						Map<String, Permissions> restIpMap0;
						try {
							restIpMap0 = Permissions.parserMap(restIpMap);
						} catch (Exception e) {
							e.printStackTrace();
							rs += "error:\r\n" + e.getMessage();
							break;
						}
						Map<String, Permissions> ipListRest = auther.getIpListRest();
						rs += "old restIpMap map:\r\n";
						rs += ipListRest.toString();
						ipListRest.clear();
						ipListRest.putAll(restIpMap0);
						rs += "new restIpMap map:\r\n";
						rs += ipListRest.toString();
					}
					if (clientIpMap != null) {
						//
						Map<String, Permissions> clientIpMap0;
						try {
							clientIpMap0 = Permissions.parserMap(clientIpMap);
						} catch (Exception e) {
							e.printStackTrace();
							rs += "error:\r\n" + e.getMessage();
							break;
						}
						Map<String, Permissions> ipListClient = auther.getIpListClient();
						rs += "old clientIpMap map:\r\n";
						rs += ipListClient.toString();
						ipListClient.clear();
						ipListClient.putAll(clientIpMap0);
						rs += "new clientIpMap map:\r\n";
						rs += ipListClient.toString();
					}
				} else {
					rs = withPermission;
				}
			}
			channel.sendResponse(new BytesRestResponse(RestStatus.OK, rs));
		};
		return rr;
	}

	RestChannelConsumer dealLogger(RestRequest request, NodeClient client, String action) {
		String key = request.param("key");

		RestChannelConsumer rr = channel -> {
			// RestRequest r = channel.request();

			boolean stat = this.actionLogger.isRunning();

			boolean isAdmin = false;
			// boolean isUser = false;
			if (key != null) {
				if (adminKey.equals(key)) {
					isAdmin = true;
				}
				if (userKey.equals(key)) {
					// isUser = true;
				}
			}

			String rs = "";

			switch (action) {
			case "help":
			default:
			case "stat":
				rs = "the logger is running:" + stat + " \r\n" + helpLoggerString;
				break;
			case "start":
				if (isAdmin) {
					this.actionLogger.start();
					rs = "the logger is starting: \r\n";
				} else {
					rs = withPermission;
				}
				break;
			case "end":
				if (isAdmin) {
					this.actionLogger.end();
					rs = "the logger is ending: \r\n";
				} else {
					rs = withPermission;
				}
				break;

			}

			channel.sendResponse(new BytesRestResponse(RestStatus.OK, rs));
		};
		return rr;
	}

	RestChannelConsumer dealHelp(RestRequest request, NodeClient client) {

		RestChannelConsumer rr = channel -> {
			// RestRequest r = channel.request();

			log(1, "--->@accept channel");

			// try (XContentBuilder builder = channel.newBuilder()) {
			//
			// builder.startObject();
			// // builder.field("task", localNodeId + ":" + task.getId());
			//
			// builder.field("message", "hello " + name);
			// // String content = "{\"success\":true, \"message\":\"hello " + name + "\"}";
			// // RestResponse response = new BytesRestResponse(RestStatus.OK, BytesRestResponse.TEXT_CONTENT_TYPE, content);
			//
			// builder.endObject();
			// channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));

			// }

			channel.sendResponse(new BytesRestResponse(RestStatus.OK, helpString));
		};
		return rr;
	}

	Auther auther;

	ActionLogger actionLogger;

	String adminKey;
	String userKey;

	DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
	String pluginPath;

	public RestSpToolsAction(Settings settings, RestController controller) {
		super(settings);

		log(1, "--->@Inject RestSpToolsAction");

		Functions fc = Functions.getFunctions(settings);

		auther = fc.getAuther();
		actionLogger = fc.getActionLogger();

		controller.registerHandler(Method.GET, "/_sp", this);
		controller.registerHandler(Method.GET, "/_sp/{func}", this);
		controller.registerHandler(Method.GET, "/_sp/{func}/{action}", this);

		loadConfig(settings);
	}

	private void loadConfig(Settings settings) {
		pluginPath = Common.getPath(settings);

		Properties pp = Common.loadPropertiesfile(pluginPath + "config.properties");
		adminKey = pp.getProperty("admin.adminKey", "admin");
		userKey = pp.getProperty("admin.userKey", "go");
		
		
		auther.updateSettings(pp);
		actionLogger.updateSettings(pp);
		
//		String seed = pp.getProperty("auth.seed");
//		if (seed != null) {
//			auther.setSeed(Integer.parseInt(seed));
//		}
//		
//		String usingAuth = pp.getProperty("auther.using","true");
//		String usinglog = pp.getProperty("logger.using","true");
		
		
	}

}
