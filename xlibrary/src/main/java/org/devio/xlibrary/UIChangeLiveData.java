package org.devio.xlibrary;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public class UIChangeLiveData extends SingleLiveEvent {
    public SingleLiveEvent<Void> showDialogEvent;
    public SingleLiveEvent<Void> dismissDialogEvent;
    public SingleLiveEvent<Throwable> failureEvent;

    public SingleLiveEvent<Void> getShowDialogEvent() {
        return showDialogEvent = createLiveData(showDialogEvent);
    }

    public SingleLiveEvent<Void> getDismissDialogEvent() {
        return dismissDialogEvent = createLiveData(dismissDialogEvent);
    }

    public SingleLiveEvent<Throwable> getFailureEvent() {
        return failureEvent = createLiveData(failureEvent);
    }

    private <T> SingleLiveEvent<T> createLiveData(SingleLiveEvent<T> liveData) {
        if (liveData == null) {
            liveData = new SingleLiveEvent<>();
        }
        return liveData;
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer observer) {
        super.observe(owner, observer);
    }
}
