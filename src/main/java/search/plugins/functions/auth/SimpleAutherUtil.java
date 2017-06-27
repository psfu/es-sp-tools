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
