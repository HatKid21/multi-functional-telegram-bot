package com.github.hatkid.command;

import com.github.hatkid.TelegramBot;
import com.github.hatkid.ai.UserManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class    CommandDispatcher {

    private final Map<String, CommandHandler> handlerMap = new HashMap<>();

    private final UserManager userManager;

    public CommandDispatcher(UserManager userStateManager){
        this.userManager = userStateManager;
        init();
    }

    public Set<Map.Entry<String, CommandHandler>> getEntry(){
        return handlerMap.entrySet();
    }

    private void init(){
        handlerMap.put("/clear", new ClearCommand(userManager));
        handlerMap.put("/addreminder", new AddReminderCommand(userManager));
        handlerMap.put("/cancel", new CancelCommand(userManager));
        handlerMap.put("/instruction", new SetInstructionsCommand(userManager));
        handlerMap.put("/reminderlist", new ReminderListCommand(userManager));
        handlerMap.put("/removereminder",new RemoveReminderCommand(userManager));
        handlerMap.put("/clearinstruction", new ClearInstructionsCommand(userManager));
    }

    public void dispatch(String command, long chatId, TelegramBot bot){
        if (!handlerMap.containsKey(command)){
            bot.sendMessage(chatId,"Такой команды не существует.");
            return;
        }
        CommandHandler commandHandler = handlerMap.get(command.strip());
        commandHandler.handle(chatId,bot);
    }

}
