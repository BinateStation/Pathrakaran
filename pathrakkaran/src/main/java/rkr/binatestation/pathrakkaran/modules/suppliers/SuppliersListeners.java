package rkr.binatestation.pathrakkaran.modules.suppliers;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import java.util.List;

import rkr.binatestation.pathrakkaran.models.UserDetailsModel;

/**
 * Created by RKR on 26/1/2017.
 * SuppliersListeners.
 */

interface SuppliersListeners {
    interface ViewListener {
        void showProgressBar();

        void hideProgressBar();

        void setRecyclerView(List<UserDetailsModel> userDetailsModelList);

        Context getContext();
    }

    interface PresenterListener {
        void loadSuppliersList(LoaderManager loaderManager, long userId);

        void setSuppliersList(List<UserDetailsModel> userDetailsModelList);

        Context getContext();

    }

    interface InterActorListener {
        void loadSuppliersList(LoaderManager loaderManager, long userId);
    }
}
