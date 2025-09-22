package com.github.hatkid.command;

import com.github.hatkid.ai.UserManager;

public abstract class BaseCommand implements CommandHandler{

    protected final UserManager userManager;
    protected final String description;

    public BaseCommand(UserManager userManager, String description){
        this.userManager = userManager;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
