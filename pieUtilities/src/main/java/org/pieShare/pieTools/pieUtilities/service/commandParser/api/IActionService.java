package org.pieShare.pieTools.pieUtilities.service.commandParser.api;

import java.util.Map;

/**
 * Created by Svetoslav on 09.01.14.
 */
public interface IActionService {

	public void doAction(Map<String, Object> args);

	public String getCommandName();

	public String getProgramName();

	public Map<String, Class> getArguments();
}
