package rkr.binatestation.pathrakkaran.modules.subscribers;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import java.util.List;

import rkr.binatestation.pathrakkaran.models.UserDetailsModel;

import static rkr.binatestation.pathrakkaran.models.UserDetailsModel.USER_TYPE_SUBSCRIBER;

/**
 * Created by RKR on 28/1/2017.
 * SubscriberPresenter.
 */

class SubscriberPresenter implements SubscriberListeners.PresenterListener {
    private static final String TAG = "SubscriberPresenter";
    private SubscriberListeners.ViewListener mViewListener;
    private SubscriberListeners.InterActorListener mInterActorListener;

    SubscriberPresenter(SubscriberListeners.ViewListener viewListener) {
        mViewListener = viewListener;
        mInterActorListener = new SubscriberInterActor(this);
    }

    private boolean isViewListener() {
        return mViewListener != null;
    }

    private boolean isInterActorLive() {
        return mInterActorListener != null;
    }


    @Override
    public void loadSubscriberList(LoaderManager loaderManager, long userId) {
        Log.d(TAG, "loadSubscriberList() called with: loaderManager = [" + loaderManager + "], userId = [" + userId + "]");
        if (isViewListener()) {
            mViewListener.showProgressBar();
        }
        if (isInterActorLive()) {
            mInterActorListener.loadSubscriberList(loaderManager, userId);
        }

    }

    @Override
    public void setSubscriberList(List<UserDetailsModel> userDetailsModelList) {
        Log.d(TAG, "setSubscriberList() called with: userDetailsModelList = [" + userDetailsModelList + "]");
        if (isViewListener()) {
            mViewListener.setRecyclerView(userDetailsModelList);
            mViewListener.hideProgressBar();
        }
    }

    @Override
    public void registerSubscriber(Context context, String name, String mobile, String email, long userId) {
        Log.d(TAG, "registerSubscriber() called with: context = [" + context + "], name = [" + name + "], mobile = [" + mobile + "], email = [" + email + "], userId = [" + userId + "]");
        int userTypeValue = USER_TYPE_SUBSCRIBER;
        if (isInterActorLive()) {
            mInterActorListener.register(context, name, mobile, email, userTypeValue, userId);
        }
    }

    @Override
    public Context getContext() {
        if (isViewListener()) {
            return mViewListener.getContext();
        }
        return null;
    }
}
