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
package search.plugins.functions.auth;

import java.util.HashMap;
import java.util.Map;

public class Permissions {
	boolean allowed;

	public Permissions(boolean allowed) {
		super();
		this.allowed = allowed;
	}
	
	

	@Override
	public String toString() {
		return Boolean.toString(allowed);
	}



	public static Map<String, Permissions> parserMap(String in) {
		String[] items = in.split(";");
		Map<String, Permissions> r = new HashMap<>();

		try {
			for (String item : items) {
				String[] row = item.split(":");
				String key = row[0];
				String value0 = row[1];
				Boolean value1 = Boolean.parseBoolean(value0);
				Permissions value = new Permissions(value1);
				r.put(key, value);
			}
			return r;
		} catch (Exception e) {
			throw e;
		}
	}

}
