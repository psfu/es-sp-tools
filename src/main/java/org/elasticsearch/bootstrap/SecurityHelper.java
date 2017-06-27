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
