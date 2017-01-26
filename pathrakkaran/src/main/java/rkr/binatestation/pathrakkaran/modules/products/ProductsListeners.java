package rkr.binatestation.pathrakkaran.modules.products;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import java.util.List;

import rkr.binatestation.pathrakkaran.models.AgentProductModel;

/**
 * Created by RKR on 8/1/2017.
 * ProductsListeners.
 */

interface ProductsListeners {
    interface ViewListener {
        void showProgressBar();

        void hideProgressBar();

        void setRecyclerView(List<AgentProductModel> productModelList);

        Context getContext();
    }

    interface PresenterListener {
        void loadProductList(LoaderManager loaderManager);

        void setProductList(List<AgentProductModel> productModelList);

        Context getContext();

    }

    interface InterActorListener {
        void loadProductList(LoaderManager loaderManager);
    }
}
