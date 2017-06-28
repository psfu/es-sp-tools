package search.plugins.functions.auth;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.tasks.Task;

import search.plugins.common.Common;

public class SimpleAuther implements Auther {
	public void log(int level, Object o) {
		if (level > Common.logLevel) {
			Common.log0(o);
		}
	}

	public boolean using = true;
	
	public boolean restUsing = true;
	/**
	 * java client iplist is in optimizing
	 */
	public boolean clienttUsing = false;
	int seed = 0x8100;
	
//	@Override
//	public void setSeed(int value) {
//		seed = value;
//	}

	@Inject
	public SimpleAuther(Settings settings) {

	}
	
	@Override
	public void updateSettings(Properties pp) {
		// TODO Auto-generated method stub
		String seed = pp.getProperty("auth.seed");
		if (seed != null) {
			this.seed = Integer.parseInt(seed);
		}
		
		String usingAuth = pp.getProperty("auther.using","true");
		this.using = Boolean.parseBoolean(usingAuth);
	}

	public Map<String, Permissions> ipListClient = new HashMap<>();
	{
		ipListClient.put("10", new Permissions(true));
		ipListClient.put("127", new Permissions(true));
		ipListClient.put("0", new Permissions(true));
	}
	{
		ipListClient.put("127.0.0.1", new Permissions(true));
		ipListClient.put("0.0.0.0", new Permissions(true));
	}
	public Map<String, Permissions> ipListRest = new HashMap<>();
	{
		ipListRest.put("0.0.0.0", new Permissions(true));
	}

	@Override
	public <Response extends ActionResponse> boolean auth(Task task, String action, ActionRequest request, ActionListener<Response> listener,
			boolean isJavaClient) {
		if (!restUsing && !isJavaClient) {
			return true;
		}
		if (!clienttUsing && isJavaClient) {
			return true;
		}
		if (isJavaClient) {
			return dealClientAuth(request);
		} else {
			return dealRestAuth(task, action, request, listener);
		}

	}

	private <Response extends ActionResponse> boolean dealRestAuth(Task task, String action, ActionRequest request, ActionListener<Response> listener) {
		if (request.remoteAddress() != null) {
			String addr = request.remoteAddress().getAddress();
			Permissions permission = ipListRest.get(addr);
			if (permission != null) {
				if (permission.allowed == true) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			log(10, "dealRestAuth: remoteAddress is null!");
			return true;
		}

		AuthInfo info = SimpleAutherUtil.getAuthInfo(task, action, request, listener);
		if (info.key == null) {
			return false;
		}
		return valKey(null, info.user, info.key);
	}

	private boolean dealClientAuth(ActionRequest request) {
		try {
			String addr = request.remoteAddress().getAddress();
			String[] ips = addr.split(".");
			String ipCheck = "";
			for (String ip : ips) {
				ipCheck += ip;
				Permissions permission = ipListClient.get(ipCheck);
				if (permission.allowed) {
					return true;
				}
				ipCheck += ".";
			}
		} catch (Exception e) {
			e.printStackTrace();
			//
			return true;
		}
		return false;
	}

	@Override
	public boolean doRestAuth(RestRequest request, NodeClient client, String user, String pwd) {
		// Map<String, String> params = request.params();
		// String user = params.get("user");
		// String pwd = params.get("pwd");
		if (user != null && pwd != null && user.equals(pwd)) {
			return true;
		}
		return false;
	}

	@Override
	public String genKey(Calendar cal, String user) {
		long time = cal.getTimeInMillis();
		int v1 = (int) (time & 0xfea0067);
		int v2 = v1 | 0x10000 | seed;
		return Integer.toHexString(v2);
	}

	@Override
	public boolean valKey(Calendar cal, String user, String key) {
		int v1 = Integer.parseInt(key, 16);
		int v2 = v1 & seed;
		if (v2 == seed) {
			return true;
		}
		return false;
	}

	@Override
	public void start() {
		this.restUsing = true;

	}

	@Override
	public void end() {
		this.restUsing = false;
	}

	@Override
	public boolean isRunning() {
		return using;
	}
	
	@Override
	public boolean isUsing() {
		return using;
	}

	public static void main(String[] args) {
		SimpleAuther sa = new SimpleAuther(null);
		
		System.out.println("seed:" + sa.seed);
		
		Calendar cal = Calendar.getInstance();
		String key = sa.genKey(cal, null);
		System.out.println("key:" + key);
		System.out.println(sa.valKey(cal, null, key));
	}

	@Override
	public Map<String, Permissions> getIpListRest() {
		return this.ipListRest;
	}

	@Override
	public Map<String, Permissions> getIpListClient() {
		// TODO Auto-generated method stub
		return this.ipListClient;
	}

	

	

}
