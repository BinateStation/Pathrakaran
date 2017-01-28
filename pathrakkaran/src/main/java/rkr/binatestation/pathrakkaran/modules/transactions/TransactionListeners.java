package rkr.binatestation.pathrakkaran.modules.transactions;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import java.util.List;

import rkr.binatestation.pathrakkaran.models.UserDetailsModel;

/**
 * Created by RKR on 28/1/2017.
 * TransactionListeners.
 */

interface TransactionListeners {
    interface ViewListener {
        void showProgressBar();

        void hideProgressBar();

        void setRecyclerView(List<UserDetailsModel> userDetailsModelList);

        Context getContext();
    }

    interface PresenterListener {
        void loadTransactionList(LoaderManager loaderManager, long userId);

        void setTransactionList(List<UserDetailsModel> userDetailsModelList);

        void addTransaction(Context context, long userId);

        Context getContext();

    }

    interface InterActorListener {
        void loadTransactionList(LoaderManager loaderManager, long userId);

        void addTransaction(Context context, long userId);
    }
}
