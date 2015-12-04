package de.superioz.cr.util;

import de.superioz.cr.main.CastleRush;
import de.superioz.library.java.util.list.ListUtil;
import de.superioz.library.java.util.list.PageableList;
import de.superioz.library.minecraft.server.common.command.CommandType;
import de.superioz.library.minecraft.server.common.command.CommandWrapper;
import de.superioz.library.minecraft.server.util.ChatUtil;
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
public class CommandHelpPage {

    public List<String> commandPatterns = new ArrayList<>();
    public List<CommandWrapper> commands = new ArrayList<>();
    public int objectsPerPage = 1;

    public PageableList<CommandWrapper> pageableList;

    public CommandHelpPage(int objectsPerPage, List<CommandWrapper> objects){
        this.commands = objects;
        this.objectsPerPage = objectsPerPage;
        this.pageableList = new PageableList<>(objectsPerPage, objects);
    }

    public List<TextComponent> getPage(int page){
        List<TextComponent> list = new ArrayList<>();

        if(!checkPage(page))
            return list;
        list.add(new TextComponent(getSpacer("CastleRush Help &7(&b" + page + "&7/"
                + pageableList.getTotalPages() + "&7)")));
        list.addAll(pageableList.calculatePage(page).stream()
                .map(this::getCommandHelp).collect(Collectors.toList()));

        if(page < pageableList.getTotalPages()){
            list.add(new TextComponent(""));

            String cmd = "castlerush help " + (page+1);
            TextComponent tc = new TextComponent(ChatUtil.colored(CastleRush.getProperties().get("helpCommandNextPage")
                    .replace("%label", cmd)));
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                    new TextComponent(ChatUtil.colored(CastleRush.getProperties().get("helpCommandNextPageHover")))
            }));
            tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + cmd));

            list.add(tc);
        }

        list.add(new TextComponent(getSpacer("CastleRush Help &7(&b" + page + "&7/"
                + pageableList.getTotalPages() + "&7)")));
        return list;
    }

    public boolean checkPage(int page){
        return this.pageableList.firstCheckPage(page);
    }

    public TextComponent getCommandHelp(CommandWrapper wrapper){
        if(wrapper == null)
            return new TextComponent(ChatUtil.colored("&8#"));

        String command = wrapper.getLabel() + (wrapper.getUsage().isEmpty() ? "" :
                " " + wrapper.getUsage());

        if(wrapper.getCommandType() == CommandType.NESTED){
            command = wrapper.getParent().getParent().getLabel() + " "
                    + wrapper.getParent().getLabel() + " " + command;
        }
        else if(wrapper.getCommandType() == CommandType.SUB){
            command = wrapper.getParent().getLabel() + " " + command;
        }

        TextComponent textComponent = new TextComponent(ChatUtil.colored(CastleRush.getProperties().get("helpCommandListItem")
                .replace("%label", command)));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                new TextComponent(ChatUtil.colored(CastleRush.getProperties().get("helpCommandHover")
                        .replace("%desc", wrapper.getDescription())
                        .replace("%permission", wrapper.getPermission())
                        .replace("%aliases", ListUtil.insert(wrapper.getAliases(), ", "))))
        }));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+command));

        return textComponent;
    }

    public String getSpacer(String middle){
        return ChatUtil.colored("&8===========[ &b" + middle + "&r &8]===========");
    }

}
