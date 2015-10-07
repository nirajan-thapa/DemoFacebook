package com.demoproject.util;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Class that provides an instance of the {@link Bus}
 *
 * Created by Nirajan on 10/3/15.
 */
public class BusProvider {
    private static final Bus BUS = new Bus(ThreadEnforcer.MAIN);

    public static Bus getInstance(){
        return BUS;
    }

    private BusProvider(){

    }
}
