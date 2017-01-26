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

        void registerSupplier(Context context, String name, String mobile, String email, long userId);

        Context getContext();

    }

    interface InterActorListener {
        void loadSuppliersList(LoaderManager loaderManager, long userId);

        void register(Context context, String name, String mobile, String email, int userTypeValue, long userId);
    }
}
