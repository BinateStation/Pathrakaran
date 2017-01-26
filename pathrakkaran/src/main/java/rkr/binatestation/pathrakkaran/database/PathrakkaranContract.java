package rkr.binatestation.pathrakkaran.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by RKR on 30/10/2016.
 * PathrakkaranContract.
 */

public final class PathrakkaranContract {

    // The name for the entire content provider.
    static final String CONTENT_AUTHORITY = "rkr.binatestation.pathrakkaran";
    // The content paths.
    static final String PATH_COMPANY_MASTER = "company_master";
    static final String PATH_PRODUCT_MASTER = "product_master";
    static final String PATH_AGENT_PRODUCT_LIST = "agent_product_list";
    static final String PATH_USER_DETAILS = "user_details";
    static final String PATH_AGENT_PRODUCT_LIST_JOIN_PRODUCT_MASTER_JOIN_COMPANY_MASTER = "agent_product_list_join_product_master_join_company_master";
    /**
     * INT,
     * INTEGER,
     * TINYINT,
     * SMALLINT,
     * MEDIUMINT,
     * BIGINT,
     * UNSIGNED BIG INT,
     * INT2,
     * INT8
     */
    private static final String INTEGER = " INTEGER ";
    /**
     * CHARACTER(20),
     * VARCHAR(255),
     * VARYING CHARACTER(255),
     * NCHAR(55),
     * NATIVE CHARACTER(70),
     * NVARCHAR(100),
     * TEXT,
     * CLOB
     */
    private static final String TEXT = " TEXT ";
    /**
     * REAL,
     * DOUBLE,
     * DOUBLE PRECISION,
     * FLOAT
     */
    private static final String REAL = " REAL ";
    private static final String NONE = " NONE ";//BLOB, no data type specified
    private static final String NUMERIC = " NUMERIC ";//NUMERIC, DECIMAL(10,5), BOOLEAN, DATE DATETIME
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
                COLUMN_COMPANY_STATUS + NUMERIC +
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
        // product type identifier 1- Daily, 2 - Weekly,3 - Bimonthly, 4- monthly, 5- Yearly
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
                COLUMN_PRODUCT_TYPE + INTEGER + COMMA +
                COLUMN_PRODUCT_PRICE + REAL + COMMA +
                COLUMN_PRODUCT_COST + REAL + COMMA +
                COLUMN_PRODUCT_STATUS + NUMERIC +
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
        public static final String COLUMN_AGENT_ID = "APL_agent_id";
        // product id
        public static final String COLUMN_PRODUCT_ID = "APL_product_id";
        // save status 1- saved, 2 - For saving, 3 - temp
        public static final String COLUMN_SAVE_STATUS = "APL_save_status";
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

    public static final class UserDetailsTable implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_DETAILS).build();
        // user id
        public static final String COLUMN_USER_ID = "user_id";
        // user name
        public static final String COLUMN_NAME = "name";
        // user status
        public static final String COLUMN_ADDRESS = "address";
        // postcode
        public static final String COLUMN_POSTCODE = "postcode";
        // user email id
        public static final String COLUMN_EMAIL = "email";
        // user mobile number
        public static final String COLUMN_MOBILE = "mobile";
        // user image url
        public static final String COLUMN_IMAGE = "image";
        // user type 1- agent, 2 supplier, 3 subscriber
        public static final String COLUMN_USER_TYPE = "user_type";
        // user latitude
        public static final String COLUMN_LATITUDE = "latitude";
        // user longitude
        public static final String COLUMN_LONGITUDE = "longitude";
        // Name of the User details table.
        public static final String TABLE_NAME = PATH_USER_DETAILS;
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "." + PATH_USER_DETAILS;
        // Create a table to hold User details.
        static final String SQL_QUERY_CREATE_TABLE = CREATE_TABLE + TABLE_NAME + " (" +
                _ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA +
                COLUMN_USER_ID + INTEGER + UNIQUE + NOT_NULL + COMMA +
                COLUMN_NAME + TEXT + NOT_NULL + COMMA +
                COLUMN_ADDRESS + TEXT + COMMA +
                COLUMN_POSTCODE + TEXT + COMMA +
                COLUMN_EMAIL + TEXT + COMMA +
                COLUMN_MOBILE + INTEGER + COMMA +
                COLUMN_IMAGE + TEXT + COMMA +
                COLUMN_USER_TYPE + INTEGER + NOT_NULL + COMMA +
                COLUMN_LATITUDE + REAL + COMMA +
                COLUMN_LONGITUDE + REAL +
                " );";

        // Returns the Uri referencing a Picture with the specified id.
        static Uri buildUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
