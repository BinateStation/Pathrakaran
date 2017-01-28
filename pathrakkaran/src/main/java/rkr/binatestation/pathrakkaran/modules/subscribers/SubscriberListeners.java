package rkr.binatestation.pathrakkaran.modules.subscribers;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import java.util.List;

import rkr.binatestation.pathrakkaran.models.UserDetailsModel;

/**
 * Created by RKR on 28/1/2017.
 * SubscriberListeners.
 */

interface SubscriberListeners {
    interface ViewListener {
        void showProgressBar();

        void hideProgressBar();

        void setRecyclerView(List<UserDetailsModel> userDetailsModelList);

        void addItem(UserDetailsModel userDetailsModel);

        Context getContext();
    }

    interface PresenterListener {
        void loadSubscriberList(LoaderManager loaderManager, long userId);

        void setSubscriberList(List<UserDetailsModel> userDetailsModelList);

        void registerSubscriber(Context context, String name, String mobile, String email, long userId);

        void addToSubscriberList(UserDetailsModel userDetailsModel);
        Context getContext();

    }

    interface InterActorListener {
        void loadSubscriberList(LoaderManager loaderManager, long userId);

        void register(Context context, String name, String mobile, String email, int userTypeValue, long userId);
    }
}
