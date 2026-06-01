package it.outset.t1_core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.os.Build;

import org.junit.Test;

public class PermissionHandlerLogicTest {

    @Test
    public void preQ_neverNeedsBackground() {
        // Below API 29, background location is implicitly covered by foreground.
        assertFalse(PermissionHandler.computeNeedsBackgroundLocation(
                Build.VERSION_CODES.P, true, true, false));
    }

    @Test
    public void qPlus_foregroundGranted_backgroundMissing_needsIt() {
        assertTrue(PermissionHandler.computeNeedsBackgroundLocation(
                Build.VERSION_CODES.Q, true, false, false));
        assertTrue(PermissionHandler.computeNeedsBackgroundLocation(
                Build.VERSION_CODES.TIRAMISU, false, true, false));
    }

    @Test
    public void qPlus_backgroundAlreadyGranted_doesNotNeedIt() {
        // MDM pre-grant case.
        assertFalse(PermissionHandler.computeNeedsBackgroundLocation(
                Build.VERSION_CODES.TIRAMISU, true, true, true));
    }

    @Test
    public void qPlus_noForeground_doesNotNeedBackground() {
        // Can't request background before foreground is granted.
        assertFalse(PermissionHandler.computeNeedsBackgroundLocation(
                Build.VERSION_CODES.Q, false, false, false));
    }
}
