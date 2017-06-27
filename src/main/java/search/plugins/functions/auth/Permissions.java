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
