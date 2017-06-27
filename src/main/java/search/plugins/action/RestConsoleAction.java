package search.plugins.action;

import java.io.File;
import java.io.IOException;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestRequest.Method;
import org.elasticsearch.rest.RestStatus;

import search.plugins.common.Common;

public class RestConsoleAction extends BaseRestHandler {

	public void log(int level, Object o) {
		if (level > Common.logLevel) {
			Common.log0(o);
		}
	}

	// {client.type=node, cluster.name=my-local, http.cors.allow-credentials=true, http.cors.allow-origin=/.*/, http.cors.enabled=true,
	// http.type.default=netty4, network.host=0.0.0.0, node.name=Ve_y1qO, path.home=D:\_workspace\ws005\es5test\target\classes,
	// path.logs=D:\_workspace\ws005\es5test\target\classes\logs, transport.type.default=netty4}
	String getPath = null;

	public RestConsoleAction(Settings settings, RestController controller, ClusterSettings clusterSettings, SettingsFilter settingsFilter) {
		super(settings);
		// TODO Auto-generated constructor stub
		controller.registerHandler(Method.GET, "/_console", this);
		controller.registerHandler(Method.GET, "/_console/{func}/{action}", this);

		String path = Common.getPathResources(settings);
		this.getPath = path;
		log(9, path);

	}

	

	@Override
	protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
		final String func = request.hasParam("func") ? request.param("func") : "con";
		final String funcAction = request.hasParam("action") ? request.param("action") : "jquery.js";

		RestChannelConsumer rr = null;

		if ("con".equals(func)) {
			rr = returnConsloe(request, client);
		} else if ("js".equals(func)) {
			rr = returnJs(request, client, funcAction);
		}

		return rr;
	}

	private RestChannelConsumer returnJs(RestRequest request, NodeClient client, String funcAction) {
		if ("jquery.js".equals(funcAction)) {
			RestChannelConsumer rr = channel -> {
				// RestRequest r = channel.request();

				byte[] rs = Common.readFile(new File(getPath + "jquery-3.2.1.comp.js"));
				//image/jpeg
				//
				channel.sendResponse(new BytesRestResponse(RestStatus.OK, "application/x-javascript", rs));
			};
			return rr;
		}
		return null;
	}

	private RestChannelConsumer returnConsloe(RestRequest request, NodeClient client) {
		RestChannelConsumer rr = channel -> {
			// RestRequest r = channel.request();

			byte[] rs = Common.readFile(new File(getPath + "console.pub.html"));

			channel.sendResponse(new BytesRestResponse(RestStatus.OK, "text/html; charset=UTF-8", rs));
		};
		return rr;
	}

	

}
