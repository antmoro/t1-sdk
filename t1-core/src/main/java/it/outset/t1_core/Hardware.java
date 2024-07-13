package it.outset.t1_core;

import it.outset.t1_core.type.HardwareType;

/**
 * Created by antonio on 13/10/16.
 */

public class Hardware extends Releases {

    public Hardware(String version) {
        this.setVersion(version);
    }

    public HardwareType getType() {
        return HardwareType.fromVersionName(getVersion());
    }
}

