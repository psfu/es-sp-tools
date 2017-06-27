package search.plugins.functions.logger;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.tasks.Task;

import search.plugins.functions.SpService;

public interface ActionLogger extends SpService{

	

	void log(Task task, String action, ActionRequest request, boolean isAuthed);

}
