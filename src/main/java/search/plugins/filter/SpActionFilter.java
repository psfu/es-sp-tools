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
package search.plugins.filter;

import java.util.regex.Pattern;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.support.ActionFilter;
import org.elasticsearch.action.support.ActionFilterChain;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.tasks.Task;

import search.plugins.common.Common;
import search.plugins.functions.Functions;
import search.plugins.functions.auth.Auther;
import search.plugins.functions.logger.ActionLogger;
import search.plugins.functions.utils.SpInterface;

public class SpActionFilter extends AbstractComponent implements ActionFilter, SpInterface {
	public void log(int level, Object o) {
		if (level > Common.logLevel) {
			Common.log0(o);
		}
	}

	// TODO
	// BULK

	// @Inject
	// public SpActionFilter(Settings settings, Auther auther, ActionLogger actionLogger) {
	// super(settings);
	// this.auther = auther;
	// this.actionLogger = actionLogger;
	// log(1,"---> SpActionFilter");
	//
	// // TODO Auto-generated constructor stub
	// }

	@Inject
	public SpActionFilter(Settings settings) {
		super(settings);

		Functions fc = Functions.getFunctions(settings);

		auther = fc.getAuther();
		actionLogger = fc.getActionLogger();

		log(3, "---> SpActionFilter init");

		// TODO Auto-generated constructor stub
	}

	Auther auther;
	ActionLogger actionLogger;

	@Override
	public int order() {
		// TODO Auto-generated method stub
		// this.logger.log("");
		// Logger logger = this.logger;
		return -1;
	}

	// apply... internal:gateway/local/meta_state
	// apply... cluster:monitor/nodes/stats
	// apply... indices:monitor/stats

	Pattern systemAction = Pattern.compile("(inter.+?)|((cluster:monitor/nodes/stats)|(indices:monitor/stats))");

	@Override
	public <Request extends ActionRequest, Response extends ActionResponse> void apply(Task task, String action, Request request,
			ActionListener<Response> listener, ActionFilterChain<Request, Response> chain) {
		// TODO Auto-generated method stub
		log(1, "apply... " + action);

		boolean usingAuth = auther.isUsing();
		boolean usinglog = actionLogger.isUsing();

		if (usingAuth || usinglog) {
			boolean isJavaClient = isJavaClient(task, request);
			boolean isSystemAction = checkSystemAction(task, action);
			boolean isAuthed = false;
			if (!isSystemAction) {
				SpActionFilterUtil.checkRemoteAddress(request, listener);

				isAuthed = usingAuth ? auther.auth(task, action, request, listener, isJavaClient) : true;
				log(1, "remoteAddress:" + request.remoteAddress());

				if(usinglog) {
					actionLogger.log(task, action, request, isAuthed, isJavaClient);
				}
			}

			if (!isAuthed && !isSystemAction) {
				// listener.onResponse();
				// listener.onResponse(new BytesRestResponse(RestStatus.OK, helpString));
				if (!isJavaClient) {
					listener.onFailure(new Exception("the auth is must please do the auth, this message is from sp tools"));
				}

			} else {
				chain.proceed(task, action, request, listener);
			}
		} else {
			chain.proceed(task, action, request, listener);
		}

	}

	private <Request extends ActionRequest> boolean isJavaClient(Task task, Request request) {
		// transport
		boolean isJavaClient = false;
		//isJavaClient = request.remoteAddress() != null ? true : false;
		isJavaClient = !task.getType().equals("transport");
		return isJavaClient;
	}

	private boolean checkSystemAction(Task task, String action) {
		boolean isSystemAction = systemAction.matcher(action).find();

		// boolean isSystemAction = action.endsWith("stat");
		return isSystemAction;
	}

}
