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
package search.plugins.functions.auth;

import java.util.List;
import java.util.Map;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.tasks.Task;

import search.plugins.functions.utils.RestRequestUtil;

public class SimpleAutherUtil {

	public static <Response extends ActionResponse> AuthInfo getAuthInfo(Task task, String action, ActionRequest request, ActionListener<Response> listener) {
		request.getClass();
		RestRequest rq = null;

		rq = RestRequestUtil.getRestRequest(listener);

		Map<String, List<String>> headers = rq.getHeaders();

		List<String> spkeys = headers.get("Cookie");

		if (spkeys != null && spkeys.size() > 0) {

			for (String spkey : spkeys) {
				String[] keyPaire = spkey.split("=");
				if (keyPaire.length > 1) {
					if ("spkey".equals(keyPaire[0])) {
						return new AuthInfo(keyPaire[1], null);
					}
				}

			}

		}

		return new AuthInfo(null, null);

	}

}
