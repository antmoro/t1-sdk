package it.outset.t1_core.models;

import it.outset.t1_core.Constants;

public class Peripheral {
    public String name;
    public String address;

    public Peripheral(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public boolean validAddress() {
        return address != null &&
                address.length() > 0 &&
                !address.equals(Constants.BLUETOOTH.MAC_ADDRESS_EMPTY);
    }
}
