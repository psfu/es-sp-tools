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
package org.elasticsearch.bootstrap;

import java.security.AccessController;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.util.Map;

import org.elasticsearch.SpecialPermission;

import search.plugins.common.Common;

public class SecurityHelper {

	static public void addPolicy() {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			sm.checkPermission((java.security.Permission) new SpecialPermission());
		}

		AccessController.doPrivileged(new PrivilegedAction<Object>() {

			@Override
			public Object run() {
				Policy p = Policy.getPolicy();

				if (p instanceof ESPolicy) {
					ESPolicy ep = (ESPolicy) p;
					Map<String, Policy> plugins = ep.plugins;
					Common.log0(plugins);
				}

				// URL url = SecurityHelper.class.getClassLoader().getResource("elasticsearch-5.4.0.jar");
				// System.out.println(url);
				return null;
			}

		});
	}

}
