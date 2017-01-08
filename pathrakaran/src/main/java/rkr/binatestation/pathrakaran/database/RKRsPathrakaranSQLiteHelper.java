package rkr.binatestation.pathrakaran.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import rkr.binatestation.pathrakaran.database.PathrakaranContract.AgentProductListTable;
import rkr.binatestation.pathrakaran.database.PathrakaranContract.CompanyMasterTable;
import rkr.binatestation.pathrakaran.database.PathrakaranContract.ProductMasterTable;

class RKRsPathrakaranSQLiteHelper extends SQLiteOpenHelper {

    // The name of our database.
    private static final String DATABASE_NAME = "pathrakaran.db";
    private static int DB_VERSION = 1;

    RKRsPathrakaranSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // Do the creating of the databases.
        database.execSQL(CompanyMasterTable.SQL_QUERY_CREATE_TABLE);
        database.execSQL(ProductMasterTable.SQL_QUERY_CREATE_TABLE);
        database.execSQL(AgentProductListTable.SQL_QUERY_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simply discard all old data and start over when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + CompanyMasterTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ProductMasterTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AgentProductListTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do the same thing as upgrading...
        onUpgrade(db, oldVersion, newVersion);
    }
}