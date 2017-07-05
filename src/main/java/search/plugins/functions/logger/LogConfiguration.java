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

public class LogConfiguration {
	//{client.type=node, cluster.name=my-local, http.cors.allow-credentials=true, http.cors.allow-origin=/.*/, http.cors.enabled=true, http.type.default=netty4, network.host=0.0.0.0, node.name=Ve_y1qO, path.home=D:\_workspace\ws005\es5test\target\classes, path.logs=D:\_workspace\ws005\es5test\target\classes\logs, transport.type.default=netty4}
	String clusterName;
	String nodeName;
	String pathLogs;
	String pathHome;
	public LogConfiguration(String clusterName, String nodeName, String pathLogs, String pathHome) {
		super();
		this.clusterName = clusterName;
		this.nodeName = nodeName;
		this.pathLogs = pathLogs;
		this.pathHome = pathHome;
	}
	
	

}
