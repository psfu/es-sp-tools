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

import java.util.Calendar;
import java.util.Map;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.tasks.Task;

import search.plugins.functions.SpService;

public interface Auther extends SpService {

	//public boolean using = true;

	String genKey(Calendar cal, String user);

	boolean valKey(Calendar cal, String user, String key);

	boolean doRestAuth(RestRequest request, NodeClient client, String user, String pwd);

	<Response extends ActionResponse> boolean auth(Task task, String action, ActionRequest request, ActionListener<Response> listener, boolean isJavaClient);

	Map<String, Permissions> getIpListRest();

	Map<String, Permissions> getIpListClient();

	//public void setSeed(int value);

}
