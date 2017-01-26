package rkr.binatestation.pathrakkaran.modules.products;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import java.util.List;

import rkr.binatestation.pathrakkaran.models.AgentProductModel;

/**
 * Created by RKR on 8/1/2017.
 * ProductsPresenter.
 */

class ProductsPresenter implements ProductsListeners.PresenterListener {
    private static final String TAG = "ProductsPresenter";
    private ProductsListeners.ViewListener mViewListener;
    private ProductsListeners.InterActorListener mInterActorListener;

    ProductsPresenter(ProductsListeners.ViewListener viewListener) {
        mViewListener = viewListener;
        mInterActorListener = new ProductsInterActor(this);
    }

    private boolean isViewListener() {
        return mViewListener != null;
    }

    private boolean isInterActorLive() {
        return mInterActorListener != null;
    }

    @Override
    public void loadProductList(LoaderManager loaderManager) {
        Log.d(TAG, "loadProductList() called with: loaderManager = [" + loaderManager + "]");
        if (isViewListener()) {
            mViewListener.showProgressBar();
        }
        if (isInterActorLive()) {
            mInterActorListener.loadProductList(loaderManager);
        }
    }

    @Override
    public void setProductList(List<AgentProductModel> productModelList) {
        if (isViewListener()) {
            mViewListener.setRecyclerView(productModelList);
            mViewListener.hideProgressBar();
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
