package com.github.hatkid.state;

import com.github.hatkid.TelegramBot;
import com.github.hatkid.UserState;
import com.github.hatkid.ai.UserManager;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.HashMap;
import java.util.Map;

public class StateBasedMessageManager {

    private final Map<UserState, StateHandler> handlerMap = new HashMap<>();
    private final UserManager userManager;

    public StateBasedMessageManager(UserManager userManager){
        this.userManager = userManager;
        init();
    }

    private void init(){
        handlerMap.put(UserState.SET_BOT_INSTRUCTIONS, new SetInstructionsStateHandler(userManager));
        handlerMap.put(UserState.SET_REMINDER_TEXT, new SetReminderTextHandler(userManager));
        handlerMap.put(UserState.SET_REMINDER_TIME, new SetReminderTimeHandler(userManager));
        handlerMap.put(UserState.IDLE,new IdleStateHandler(userManager));
        handlerMap.put(UserState.REMOVE_REMINDER,new RemoveReminderHandler(userManager));
    }

    public void dispatch(Message message, long chatId, TelegramBot bot){
        StateHandler stateHandler = handlerMap.get(userManager.getUser(chatId).getState());
        stateHandler.handle(message,chatId,bot);
    }

}
