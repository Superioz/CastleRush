package de.superioz.cr.util;

import de.superioz.cr.common.lang.LanguageManager;
import de.superioz.library.bukkit.common.command.CommandType;
import de.superioz.library.bukkit.common.command.CommandWrapper;
import de.superioz.library.bukkit.util.ChatUtil;
import de.superioz.library.java.util.list.ListUtil;
import de.superioz.library.java.util.list.PageableList;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public class PluginHelp {

	public List<CommandWrapper> commands = new ArrayList<>();
	public int objectsPerPage = 1;

	public PageableList<CommandWrapper> pageableList;

	/**
	 * Class for show command help to players
	 *
	 * @param objectsPerPage Commands per page
	 * @param objects        List of commands
	 */
	public PluginHelp(int objectsPerPage, List<CommandWrapper> objects){
		this.commands = objects;
		this.objectsPerPage = objectsPerPage;
		this.pageableList = new PageableList<>(objectsPerPage, objects);
	}

	/**
	 * Gets a list of commands for given page
	 *
	 * @param page The page
	 * @return A list of text components
	 */
	public List<TextComponent> getPage(int page){
		List<TextComponent> list = new ArrayList<>();

		if(!checkPage(page))
			return list;
		list.add(new TextComponent(PluginUtilities.getSpacer("CastleRush Help &7(&e" + page + "&7/"
				+ pageableList.getTotalPages() + "&7)")));
		list.addAll(pageableList.calculatePage(page).stream()
				.map(this::getCommandHelp).collect(Collectors.toList()));

		if(page < pageableList.getTotalPages()){
			list.add(new TextComponent(""));

			String cmd = "castlerush help " + (page + 1);
			TextComponent tc = new TextComponent(ChatUtil.colored(LanguageManager.get("helpCommandNextPage")
					.replace("%label", cmd)));
			tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
					new TextComponent(ChatUtil.colored(LanguageManager.get("helpCommandNextPageHover")))
			}));
			tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + cmd));

			list.add(tc);
		}

		list.add(new TextComponent(PluginUtilities.getSpacer("CastleRush Help &7(&e" + page + "&7/"
				+ pageableList.getTotalPages() + "&7)")));
		return list;
	}

	/**
	 * Checks given page
	 *
	 * @param page The page
	 * @return Result as boolean
	 */
	public boolean checkPage(int page){
		return this.pageableList.firstCheckPage(page);
	}

	/**
	 * Gets help for given command
	 *
	 * @param wrapper The command
	 * @return The help as textcomponent
	 */
	public TextComponent getCommandHelp(CommandWrapper wrapper){
		if(wrapper == null)
			return new TextComponent(ChatUtil.colored(PluginColor.DARK + "#"));

		String command = wrapper.getLabel() + (wrapper.getUsage().isEmpty() ? "" :
				" &7" + wrapper.getUsage());
		String coloredCommand = wrapper.getLabel() + (wrapper.getUsage().isEmpty() ? "" :
				" &7" + wrapper.getUsage());

		if(wrapper.getCommandType() == CommandType.NESTED){
			command = wrapper.getParent().getParent().getLabel() + " "
					+ wrapper.getParent().getLabel() + " " + command;
			coloredCommand = wrapper.getParent().getParent().getLabel() + " " + PluginColor.GOLD
					+ wrapper.getParent().getLabel() + " " + PluginColor.SHINE + coloredCommand;
		}
		else if(wrapper.getCommandType() == CommandType.SUB){
			command = wrapper.getParent().getLabel() + " " + command;
			coloredCommand = wrapper.getParent().getLabel() + " &6" + coloredCommand;
		}

		TextComponent textComponent = new TextComponent(ChatUtil.colored(LanguageManager.get("helpCommandListItem")
				.replace("%label", coloredCommand)));
		textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
				new TextComponent(ChatUtil.colored(LanguageManager.get("helpCommandHover")
						.replace("%desc", wrapper.getDescription())
						.replace("%permission", wrapper.getPermission())
						.replace("%aliases", ListUtil.insert(wrapper.getAliases(), ", "))))
		}));
		textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + command));

		return textComponent;
	}

}
