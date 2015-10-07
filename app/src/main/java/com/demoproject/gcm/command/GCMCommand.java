package com.demoproject.gcm.command;

import android.content.Context;

/**
 * Created by Nirajan on 10/6/2015.
 */

public abstract class GCMCommand {
    public abstract void execute(Context context, String type, String extraData);
}

