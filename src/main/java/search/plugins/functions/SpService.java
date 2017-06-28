package search.plugins.functions;

import java.util.Properties;

public interface SpService {
	public void start();
	public void end();
	public boolean isRunning();
	public boolean isUsing();
	public void updateSettings(Properties pp);
}
