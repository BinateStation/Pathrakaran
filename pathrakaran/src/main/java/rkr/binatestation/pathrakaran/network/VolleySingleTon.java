package rkr.binatestation.pathrakaran.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.BuildConfig;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by RKR on 12-11-2015.
 * VolleySingleTon.
 */
public class VolleySingleTon {
    private static final String domainUrl = "http://itzlarc.in/pathrakkaran/";
    private static final String localDomainUrl = "";
    private static final String domainUrlForImage = "http://itzlarc.in/pathrakkaran/images/profile/";
    private static final String localDomainUrlForImage = "http://itzlarc.in/pathrakkaran/images/profile/";

    private static VolleySingleTon mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private VolleySingleTon(Context context) {
        mRequestQueue = getRequestQueue(context);

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized VolleySingleTon getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleTon(context);
        }
        return mInstance;
    }

    public static String getDomainUrlForImage() {
        switch (BuildConfig.BUILD_TYPE) {
            case "release":
            case "debug":
                return domainUrlForImage;
            default:
                return localDomainUrlForImage;
        }
    }

    public static String getDomainUrl() {
        switch (BuildConfig.BUILD_TYPE) {
            case "release":
            case "debug":
                return domainUrl;
            default:
                return localDomainUrl;
        }
    }

    private RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Context context, Request<T> req) {
        getRequestQueue(context).add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
