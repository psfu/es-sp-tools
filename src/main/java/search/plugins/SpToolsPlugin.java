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
package search.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.elasticsearch.action.support.ActionFilter;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;

import search.plugins.action.RestConsoleAction;
import search.plugins.action.RestSpToolsAction;
import search.plugins.common.Common;
import search.plugins.filter.SpActionFilter;
import search.plugins.functions.utils.RestRequestUtil;

public class SpToolsPlugin extends Plugin implements ActionPlugin {
	public void log(int level, Object o) {
		if (level > Common.logLevel) {
			Common.log0(o);
		}
	}

	@SuppressWarnings("unused")
	private final Settings settings;

	SpActionModule m = new SpActionModule();

	// org.elasticsearch.common.inject.Injector injector = org.elasticsearch.common.inject.Guice.createInjector(modules);

	// D:\_lib\_elasticsearch\elasticsearch-5.4.1\temp>jar -cvfm elasticsearch-5.4.1.jar MANIFEST.MF -C elasticsearch-5.4.1 .
	public SpToolsPlugin(Settings settings) {
		this.settings = settings;

		// SecurityHelper.addPolicy();
		
		RestRequestUtil.init();
		log(3, "---> SpToolsPlugin init");
	}

	@Override
	public Collection<Module> createGuiceModules() {
		List<Module> r = new ArrayList<>();
		// r.add(new SpActionModule());
		r.add(m);
		return r;
	}

	@Override
	public List<Class<? extends ActionFilter>> getActionFilters() {

		List<Class<? extends ActionFilter>> r = new ArrayList<>();
		r.add(SpActionFilter.class);
		return r;
	}

	// @Override
	// public List getActions() {
	// return Arrays.asList(new ActionHandler<>(ReindexAction.INSTANCE, TransportReindexAction.class));
	// }

	@Override
	public List<RestHandler> getRestHandlers(Settings settings, RestController restController, ClusterSettings clusterSettings,
			IndexScopedSettings indexScopedSettings, SettingsFilter settingsFilter, IndexNameExpressionResolver indexNameExpressionResolver,
			Supplier<DiscoveryNodes> nodesInCluster) {
		log(1, "---> getRestHandlers");

		return Arrays.asList(new RestSpToolsAction(settings, restController), new RestConsoleAction(settings, restController, clusterSettings, settingsFilter));

	}
	
	public static void main(String[] args) {
		System.out.println("st-tools ..... ");
	}
	
	// @Override
	// public String description() {
	// return "a tools for elasticsearch";
	// }
	//
	// @Override
	// public String name() {
	// return "sp tools";
	// }
	//
	// @Override
	// public Collection<Module> nodeModules() {
	// System.out.println("---> nodeModules");
	// return Collections.<Module>singletonList(new SpActionModule());
	// }
	//
	// public void onModule(ActionModule module) {
	// System.out.println("---> onModule");
	// module.registerFilter(SpActionFilter.class);
	// }

}
