package rkr.binatestation.pathrakkaran.modules.home;

import android.content.Context;

/**
 * Created by RKR on 8/1/2017.
 * HomeListeners.
 */

interface HomeListeners {
    interface ViewListener {

    }

    interface PresenterListener {
        void getMasters(Context context);
    }

    interface InterActorListener {
        void getMasters(Context context);
    }
}
