package search.plugins;

import org.elasticsearch.common.inject.AbstractModule;

public class SpActionModule extends AbstractModule {


	@Override
	protected void configure() {
		//this.bind(SpActionFilter.class).asEagerSingleton();
		
		//this.bind(Auther.class).to(SimpleAuther.class).asEagerSingleton();
		//this.bind(ActionLogger.class).to(CachedFileLogger.class).asEagerSingleton();
	}

}
