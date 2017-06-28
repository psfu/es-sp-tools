package search.plugins.filter;

import java.net.InetSocketAddress;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.rest.RestRequest;

import search.plugins.functions.utils.RestRequestUtil;

public class SpActionFilterUtil {

	public static boolean using = true;

	static <Request extends ActionRequest, Response extends ActionResponse> void checkRemoteAddress(Request request, ActionListener<Response> listener) {

		if (!using) {
			return;
		}

		listener.getClass();

		RestRequest rq = RestRequestUtil.getRestRequest(listener);

		if (rq != null) {
			rq.getRemoteAddress();
			//TODO
			//Common.log0("rq.getRemoteAddress():\t" + rq.getRemoteAddress());
			InetSocketAddress rq1 = (InetSocketAddress) rq.getRemoteAddress();
			request.remoteAddress(new InetSocketTransportAddress(rq1));
		}

		// Object o1 = unsafe.getObject(listener, 16);
		// RestActionListener rl = null;
		//
		// if (o1 instanceof RestActionListener) {
		// rl = (RestActionListener) o1;
		// } else {
		// unsafe.getObject(listener, 24);
		// unsafe.getObject(listener, 32);
		// }
		//
		// // if(listener instanceof org.elasticsearch.rest.action.RestStatusToXContentListener){
		// //
		// // }
		// // if(listener instanceof org.elasticsearch.action.support.TransportAction){
		// //
		// // }
		// // if (listener instanceof RestActionListener) {
		// // RestActionListener ls = (RestActionListener) listener;
		// if (rl != null) {
		// RestChannel rch = null;
		// try {
		// // rch = (RestChannel) ch.get(listener);
		// rch = (RestChannel) ch.get(rl);
		// System.out.println("rch:\t" + rch);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// if (rch != null) {
		// RestRequest rq = rch.request();
		//
		// }
		// }
	}

	public static void init() {
		// RestRequestUtil.init();
	}

}
