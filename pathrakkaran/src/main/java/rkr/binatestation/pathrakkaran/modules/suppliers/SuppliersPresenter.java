package rkr.binatestation.pathrakkaran.modules.suppliers;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import java.util.List;

import rkr.binatestation.pathrakkaran.models.UserDetailsModel;

import static rkr.binatestation.pathrakkaran.models.UserDetailsModel.USER_TYPE_SUPPLIER;

/**
 * Created by RKR on 26/1/2017.
 * SuppliersPresenter.
 */

class SuppliersPresenter implements SuppliersListeners.PresenterListener {

    private static final String TAG = "SuppliersPresenter";
    private SuppliersListeners.ViewListener mViewListener;
    private SuppliersListeners.InterActorListener mInterActorListener;

    SuppliersPresenter(SuppliersListeners.ViewListener viewListener) {
        mViewListener = viewListener;
        mInterActorListener = new SuppliersInterActor(this);
    }

    private boolean isViewListener() {
        return mViewListener != null;
    }

    private boolean isInterActorLive() {
        return mInterActorListener != null;
    }


    @Override
    public void loadSuppliersList(LoaderManager loaderManager, long userId) {
        Log.d(TAG, "loadSuppliersList() called with: loaderManager = [" + loaderManager + "], userId = [" + userId + "]");
        if (isViewListener()) {
            mViewListener.showProgressBar();
        }
        if (isInterActorLive()) {
            mInterActorListener.loadSuppliersList(loaderManager, userId);
        }

    }

    @Override
    public void setSuppliersList(List<UserDetailsModel> userDetailsModelList) {
        if (isViewListener()) {
            mViewListener.setRecyclerView(userDetailsModelList);
            mViewListener.hideProgressBar();
        }
    }

    @Override
    public void registerSupplier(Context context, String name, String mobile, String email, long userId) {
        int userTypeValue = USER_TYPE_SUPPLIER;
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
