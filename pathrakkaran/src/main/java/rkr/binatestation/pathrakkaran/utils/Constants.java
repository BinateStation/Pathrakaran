package rkr.binatestation.pathrakkaran.utils;

/**
 * Created by RKR on 17/10/2016.
 * Constants.
 */

public class Constants {
    /**
     * General Constants Keys
     */
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_PHONE = "user_phone";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_IMAGE = "user_image";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_USER_ADDRESS = "user_address";
    public static final String KEY_USER_POSTCODE = "user_postcode";
    public static final String KEY_USER_LATITUDE = "user_latitude";
    public static final String KEY_USER_LONGITUDE = "user_longitude";
    public static final String KEY_NAME = "name";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_STATUS = "status";
    public static final String KEY_DATA = "data";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_COMPANIES = "companies";
    public static final String KEY_PRODUCTS = "products";
    public static final String KEY_COMPANY_ID = "company_id";
    public static final String KEY_COMPANY_NAME = "company_name";
    public static final String KEY_COMPANY_STATUS = "company_status";
    public static final String KEY_PRODUCT_NAME = "product_name";
    public static final String KEY_PRODUCT_PRICE = "product_price";
    public static final String KEY_PRODUCT_IMAGE = "product_image";
    public static final String KEY_PRODUCT_TYPE = "product_type";
    public static final String KEY_PRODUCT_STATUS = "product_status";
    public static final String KEY_USER = "user";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_LOGIN_TYPE = "login_type";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_POSTCODE = "postcode";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_AGENT_ID = "agent_id";
    public static final String KEY_PRODUCT_ID = "product_id";
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    public static final int REQUEST_READ_CONTACTS = 100;
    public static final int REQUEST_LOCATION_PERMISSION = 101;
    public static final int REQUEST_EXTERNAL_STORAGE = 102;

    /**
     * API End URL
     */
    public static final String END_URL_USER_LOGIN = "user/login";
    public static final String END_URL_USER_REGISTER = "user/register";
    public static final String END_URL_USER_PROFILE = "user/profile";
    public static final String END_URL_USER_PROFILE_UPDATE = "user/profile/update";
    public static final String END_URL_MASTERS = "masters";
    public static final String END_URL_PRODUCTS_MY_PRODUCTS = "products/myProducts";
    public static final String END_URL_PRODUCTS_SUBSCRIBE = "products/subscribe";
    public static final String END_URL_SUPPLIERS_GET_LIST = "suppliers/getList";
    public static final String END_URL_SUPPLIERS_REGISTER = "suppliers/register";

    /**
     * Cursor loaders
     */
    public static final int CURSOR_LOADER_LOAD_AGENT_PRODUCTS = 1;
    public static final int CURSOR_LOADER_LOAD_COMPANIES = 2;
    public static final int CURSOR_LOADER_LOAD_PRODUCTS = 3;
    public static final int CURSOR_LOADER_LOAD_SUPPLIERS = 4;
    public static final int CURSOR_LOADER_LOAD_SUBSCRIBERS = 5;
}
