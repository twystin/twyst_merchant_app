package receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import java.util.ArrayList;

/**
 * Created by tushar on 31/03/16.
 */
public class OrderTrackerResultReceiver extends ResultReceiver {
    private ArrayList<Receiver> mReceivers;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public OrderTrackerResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if ((mReceivers != null) && (mReceivers.size() != 0)) {
            for (Receiver r : mReceivers) {
                r.onReceiveResult(resultCode, resultData);
            }
        }
    }

    /**
     * Add a receiver.
     *
     * @param receiver
     * @return if successfully added, return true.
     */
    public boolean addReceiver(Receiver receiver) {
        if (mReceivers.indexOf(receiver) != -1) {
            mReceivers.add(receiver);
            return true;
        }
        return false;
    }

    /**
     * Remove a receiver
     *
     * @param receiver
     * @return if successfully removed, return true.
     */
    public boolean removeReceiver(Receiver receiver) {
        if (mReceivers.indexOf(receiver) != -1) {
            mReceivers.remove(receiver);
            return true;
        }
        return false;
    }

    /**
     * Public interface for activities which would like to be receive results.
     */
    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }
}
