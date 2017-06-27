package search.plugins.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.elasticsearch.common.settings.Settings;

public class Common {
	
	public final static int logLevel = 1;
	
	public final static void log0(Object o){
		
	}

	public static void log(int level, Object o) {
		if (level > Common.logLevel) {
			Common.log0(o);
		}
	}

	public static String getPathResources(Settings settings) {
		String sep = "/";
		String path = settings.get("path.home");
		String pluginPath = settings.get("sp.tools.path.name", "tools-sp");
		path += sep + "plugins";
		path += sep + pluginPath;
		path += sep + "resources";
		path += sep;
		return path;
	}

	public static String getPath(Settings settings) {
		String sep = "/";
		String path = settings.get("path.home");
		String pluginPath = settings.get("sp.tools.path.name", "tools-sp");
		path += sep + "plugins";
		path += sep + pluginPath;
		path += sep;
		return path;
	}

	public static byte[] readFile(File f) {
		if (!f.exists()) {
			return null;
		}
		int filelength = (int) f.length();
		log(1, filelength);

		byte[] r = new byte[filelength];

		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(f))) {
			in.read(r);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;
	}

	public static Properties loadPropertiesfile(String filePath) {
		Properties properties = new Properties();
		try {
			//properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath));
			FileReader fr = new FileReader(new File(filePath));
			properties.load(fr);
		} catch (IOException e) {
			log(10, "The properties file is not loaded.\r\n" + e);
			throw new IllegalArgumentException("The properties file is not loaded.\r\n" + e);
		}

		return properties;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
