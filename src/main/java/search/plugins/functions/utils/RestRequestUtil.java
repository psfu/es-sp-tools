package search.plugins.functions.utils;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.RestActionListener;

import search.plugins.common.Common;
import sun.misc.Unsafe;

public class RestRequestUtil {
	static boolean inited = false;

	private static Unsafe unsafe;

	static {

		AccessController.doPrivileged(new PrivilegedAction<Object>() {

			@Override
			public Object run() {
				try {
					Field field = Unsafe.class.getDeclaredField("theUnsafe");
					field.setAccessible(true);
					unsafe = (Unsafe) field.get(null);

				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		});

	}

	static Field ch = null;
	static {
		AccessController.doPrivileged(new PrivilegedAction<Object>() {

			@Override
			public Object run() {
				try {
					ch = RestActionListener.class.getDeclaredField("channel");
					if (!ch.isAccessible()) {
						ch.setAccessible(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		});
	}

	@SuppressWarnings({ "deprecation", "rawtypes" })
	public static <Response extends ActionResponse> RestRequest getRestRequest(ActionListener<Response> listener) {
		RestRequest rq = null;
		Object o1 = unsafe.getObject(listener, 16);
		RestActionListener rl = null;

		if (o1 instanceof RestActionListener) {
			rl = (RestActionListener) o1;
		} else {
			o1 = unsafe.getObject(listener, 24);
			if (o1 != null && o1 instanceof RestActionListener) {
				rl = (RestActionListener) o1;
			} else {

			}

		}

		if (rl != null) {
			RestChannel rch = null;
			try {
				// rch = (RestChannel) ch.get(listener);
				rch = (RestChannel) ch.get(rl);
				Common.log0("rch:\t" + rch);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (rch != null) {
				rq = rch.request();

			}
		}
		return rq;
	}

	public static void init() {
		inited = true;

	}

}
