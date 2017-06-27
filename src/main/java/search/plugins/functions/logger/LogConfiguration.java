package search.plugins.functions.logger;

public class LogConfiguration {
	//{client.type=node, cluster.name=my-local, http.cors.allow-credentials=true, http.cors.allow-origin=/.*/, http.cors.enabled=true, http.type.default=netty4, network.host=0.0.0.0, node.name=Ve_y1qO, path.home=D:\_workspace\ws005\es5test\target\classes, path.logs=D:\_workspace\ws005\es5test\target\classes\logs, transport.type.default=netty4}
	String clusterName;
	String nodeName;
	String pathLogs;
	String pathHome;
	public LogConfiguration(String clusterName, String nodeName, String pathLogs, String pathHome) {
		super();
		this.clusterName = clusterName;
		this.nodeName = nodeName;
		this.pathLogs = pathLogs;
		this.pathHome = pathHome;
	}
	
	

}
