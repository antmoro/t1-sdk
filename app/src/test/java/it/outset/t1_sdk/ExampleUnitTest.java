package it.outset.t1_sdk;

import org.junit.Test;

import static org.junit.Assert.*;

import it.outset.t1_core.Firmware;
import it.outset.t1_core.models.cu.ControlUnit;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void firmware_getVersion() {
        Firmware firmware = new Firmware();
        assertEquals("0.1.0", firmware.getVersion());
    }

    @Test
    public void testControlUnit_getId() {
        ControlUnit cu = new ControlUnit();
        assertEquals(0, cu.getId());
    }
}