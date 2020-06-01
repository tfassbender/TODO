package net.jfabricationgames.todo.commands.button;

import net.jfabricationgames.todo.commands.AbstractButtonCommand;
import net.jfabricationgames.todo.commands.ButtonCommand;
import net.jfabricationgames.todo.configuration.hotkey.TodoHotkeyConfigurator;
import net.jfabricationgames.todo.frame.TodoFrameController;
import net.jfabricationgames.todo.frame.util.DialogUtils;

public class SettingsButtonCommand extends AbstractButtonCommand implements ButtonCommand {
	
	public SettingsButtonCommand(TodoFrameController controller) {
		super(controller);
	}
	
	@Override
	public void execute() {
		//TODO implement real settings dialog
		TodoHotkeyConfigurator hotkeyConfigurator = new TodoHotkeyConfigurator(controller);//doesn't configure anything, as long as not executed on the code area
		DialogUtils.showInfoDialog("TODO Settings", "WIP", "Hotkeys:\n\n" + hotkeyConfigurator.getDescriptionText());
	}
}
