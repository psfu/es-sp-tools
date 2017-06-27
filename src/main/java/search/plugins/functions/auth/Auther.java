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

	public boolean using = true;

	String genKey(Calendar cal, String user);

	boolean valKey(Calendar cal, String user, String key);

	boolean doRestAuth(RestRequest request, NodeClient client, String user, String pwd);

	<Response extends ActionResponse> boolean auth(Task task, String action, ActionRequest request, ActionListener<Response> listener, boolean isJavaClient);

	Map<String, Permissions> getIpListRest();

	Map<String, Permissions> getIpListClient();

	public void setSeed(int value);

}
