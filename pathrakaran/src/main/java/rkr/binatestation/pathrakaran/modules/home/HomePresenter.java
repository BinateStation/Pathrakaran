package rkr.binatestation.pathrakaran.modules.home;

import android.content.Context;

/**
 * Created by RKR on 8/1/2017.
 * HomePresenter.
 */

class HomePresenter implements HomeListeners.PresenterListener {
    private static final String TAG = "HomePresenter";

    private HomeListeners.ViewListener mViewListener;
    private HomeListeners.InterActorListener mInterActorListener;

    HomePresenter(HomeListeners.ViewListener viewListener) {
        mViewListener = viewListener;
        mInterActorListener = new HomeInterActor(this);

    }

    private boolean isViewLive() {
        return mViewListener != null;
    }

    private boolean isInterActorLive() {
        return mInterActorListener != null;
    }

    @Override
    public void getMasters(Context context) {
        if (isInterActorLive()) {
            mInterActorListener.getMasters(context);
        }
    }
}
