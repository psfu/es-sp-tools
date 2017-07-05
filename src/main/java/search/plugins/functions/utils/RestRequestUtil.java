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
