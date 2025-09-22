package com.github.hatkid.state;

import com.github.hatkid.ai.UserManager;

public abstract class BaseStateHandler {

    protected final UserManager userManager;

    public BaseStateHandler(UserManager userManager){
        this.userManager = userManager;
    }

}
