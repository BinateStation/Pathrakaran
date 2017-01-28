package rkr.binatestation.pathrakkaran.modules.transactions;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import java.util.List;

import rkr.binatestation.pathrakkaran.models.UserDetailsModel;

/**
 * Created by RKR on 28/1/2017.
 * TransactionPresenter.
 */

class TransactionPresenter implements TransactionListeners.PresenterListener {

    private static final String TAG = "TransactionPresenter";
    private TransactionListeners.ViewListener mViewListener;
    private TransactionListeners.InterActorListener mInterActorListener;

    TransactionPresenter(TransactionListeners.ViewListener viewListener) {
        mViewListener = viewListener;
        mInterActorListener = new TransactionsInterActor(this);
    }

    private boolean isViewListener() {
        return mViewListener != null;
    }

    private boolean isInterActorLive() {
        return mInterActorListener != null;
    }

    @Override
    public void loadTransactionList(LoaderManager loaderManager, long userId) {
        Log.d(TAG, "loadTransactionList() called with: loaderManager = [" + loaderManager + "], userId = [" + userId + "]");
        if (isViewListener()) {
            mViewListener.showProgressBar();
        }
        if (isInterActorLive()) {
            mInterActorListener.loadTransactionList(loaderManager, userId);
        }
    }

    @Override
    public void setTransactionList(List<UserDetailsModel> userDetailsModelList) {
        Log.d(TAG, "setTransactionList() called with: userDetailsModelList = [" + userDetailsModelList + "]");
        if (isViewListener()) {
            mViewListener.setRecyclerView(userDetailsModelList);
            mViewListener.hideProgressBar();
        }
    }

    @Override
    public void addTransaction(Context context, long userId) {
        Log.d(TAG, "addTransaction() called with: context = [" + context + "], userId = [" + userId + "]");
        if (isInterActorLive()) {
            mInterActorListener.addTransaction(context, userId);
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
