package it.outset.t1_core.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;

public abstract class BaseLifecycleObserver implements DefaultLifecycleObserver {
    private Lifecycle lifecycle;

    public void setLifecycle(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
        this.lifecycle.addObserver(this);
    }

    public BaseLifecycleObserver(@NonNull Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
        this.lifecycle.addObserver(this);
    }

}

