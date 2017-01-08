package rkr.binatestation.pathrakaran.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by RKR on 30/10/2016.
 * PathrakaranContract.
 */

public final class PathrakaranContract {

    // The name for the entire content provider.
    static final String CONTENT_AUTHORITY = "rkr.binatestation.pathrakaran";
    // The content paths.
    static final String PATH_COMPANY_MASTER = "company_master";
    static final String PATH_PRODUCT_MASTER = "product_master";
    static final String PATH_AGENT_PRODUCT_LIST = "agent_product_list";
    static final String PATH_AGENT_PRODUCT_LIST_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER = "agent_product_list_join_product_master_join_company_master";
    private static final String INTEGER = " INTEGER ";
    private static final String TEXT = " TEXT ";
    private static final String PRIMARY_KEY = " PRIMARY KEY ";
    private static final String AUTOINCREMENT = " AUTOINCREMENT ";
    private static final String COMMA = " , ";
    private static final String UNIQUE = " UNIQUE ";
    private static final String NOT_NULL = " NOT NULL ";
    private static final String CREATE_TABLE = " CREATE TABLE ";
    // Base of all URIs that will be used to contact the content provider.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class CompanyMasterTable implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMPANY_MASTER).build();
        // Company id primary key.
        public static final String COLUMN_COMPANY_ID = "CM_company_id";
        // Name of the Company.
        public static final String COLUMN_COMPANY_NAME = "CM_company_name";
        // logo image of the Company.
        public static final String COLUMN_COMPANY_LOGO = "CM_company_logo";
        // logo image of the Company.
        public static final String COLUMN_COMPANY_STATUS = "CM_company_status";
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_COMPANY_MASTER;
        // Name of the category table.
        static final String TABLE_NAME = PATH_COMPANY_MASTER;
        // Create a table to hold categories.
        static final String SQL_QUERY_CREATE_TABLE = CREATE_TABLE + TABLE_NAME + " (" +
                _ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA +
                COLUMN_COMPANY_ID + INTEGER + UNIQUE + NOT_NULL + COMMA +
                COLUMN_COMPANY_NAME + TEXT + NOT_NULL + COMMA +
                COLUMN_COMPANY_LOGO + TEXT + COMMA +
                COLUMN_COMPANY_STATUS + INTEGER +
                " );";

        // Returns the Uri referencing a category with the specified id.
        static Uri buildUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ProductMasterTable implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCT_MASTER).build();
        // Product id primary key
        public static final String COLUMN_PRODUCT_ID = "PM_product_id";
        // company id foreign key company table
        public static final String COLUMN_COMPANY_ID = "PM_company_id";
        // product name
        public static final String COLUMN_PRODUCT_NAME = "PM_product_name";
        // product image url
        public static final String COLUMN_PRODUCT_IMAGE = "PM_product_image";
        // product type identifier D- Daily, W - Weekly, M- monthly, Y- Yearly
        public static final String COLUMN_PRODUCT_TYPE = "PM_product_type";
        // product selling price
        public static final String COLUMN_PRODUCT_PRICE = "PM_product_price";
        // product cost
        public static final String COLUMN_PRODUCT_COST = "PM_product_cost";
        // product status
        public static final String COLUMN_PRODUCT_STATUS = "PM_product_status";
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_PRODUCT_MASTER;
        // Name of the Picture table.
        static final String TABLE_NAME = PATH_PRODUCT_MASTER;
        // Create a table to hold Product master.
        static final String SQL_QUERY_CREATE_TABLE = CREATE_TABLE + TABLE_NAME + " (" +
                _ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA +
                COLUMN_PRODUCT_ID + INTEGER + UNIQUE + NOT_NULL + COMMA +
                COLUMN_COMPANY_ID + INTEGER + COMMA +
                COLUMN_PRODUCT_NAME + TEXT + COMMA +
                COLUMN_PRODUCT_IMAGE + TEXT + COMMA +
                COLUMN_PRODUCT_TYPE + TEXT + COMMA +
                COLUMN_PRODUCT_PRICE + INTEGER + COMMA +
                COLUMN_PRODUCT_COST + INTEGER + COMMA +
                COLUMN_PRODUCT_STATUS + INTEGER +
                " );";

        // Returns the Uri referencing a Picture with the specified id.
        static Uri buildUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class AgentProductListTable implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_AGENT_PRODUCT_LIST).build();

        public static final Uri CONTENT_URI_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER = BASE_CONTENT_URI.buildUpon().appendPath(PATH_AGENT_PRODUCT_LIST_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER).build();
        // agent id
        public static final String COLUMN_AGENT_ID = "agent_id";
        // product id
        public static final String COLUMN_PRODUCT_ID = "product_id";
        // save status 1- saved, 2 - For saving, 3 - temp
        public static final String COLUMN_SAVE_STATUS = "save_status";
        // Name of the Agent Product list table.
        public static final String TABLE_NAME = PATH_AGENT_PRODUCT_LIST;
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_AGENT_PRODUCT_LIST;
        // Create a table to hold Product master.
        static final String SQL_QUERY_CREATE_TABLE = CREATE_TABLE + TABLE_NAME + " (" +
                _ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA +
                COLUMN_AGENT_ID + INTEGER + COMMA +
                COLUMN_PRODUCT_ID + INTEGER + UNIQUE + NOT_NULL + COMMA +
                COLUMN_SAVE_STATUS + INTEGER +
                " );";

        // Returns the Uri referencing a Picture with the specified id.
        static Uri buildUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
