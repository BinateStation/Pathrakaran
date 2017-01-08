package rkr.binatestation.pathrakaran.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static rkr.binatestation.pathrakaran.database.PathrakaranContract.AgentProductListTable.COLUMN_AGENT_ID;
import static rkr.binatestation.pathrakaran.database.PathrakaranContract.AgentProductListTable.COLUMN_PRODUCT_ID;

/**
 * Created by RKR on 08/01/2017.
 * AgentProductModel.
 */

public class AgentProductModel implements Parcelable {
    public static final Creator<AgentProductModel> CREATOR = new Creator<AgentProductModel>() {
        @Override
        public AgentProductModel createFromParcel(Parcel in) {
            return new AgentProductModel(in);
        }

        @Override
        public AgentProductModel[] newArray(int size) {
            return new AgentProductModel[size];
        }
    };
    private static final String TAG = "AgentProductModel";
    private long id;
    private long productId;
    private long agentId;
    private ProductMasterModel productMasterModel;
    private CompanyMasterModel companyMasterModel;

    public AgentProductModel(long productId, long agentId) {
        this.productId = productId;
        this.agentId = agentId;
    }

    private AgentProductModel(Parcel in) {
        id = in.readLong();
        productId = in.readLong();
        agentId = in.readLong();
        productMasterModel = in.readParcelable(ProductMasterModel.class.getClassLoader());
        companyMasterModel = in.readParcelable(CompanyMasterModel.class.getClassLoader());
    }

    public static List<AgentProductModel> getAgentProductModelList(Cursor cursor) {
        Log.d(TAG, "getAgentProductModelList() called with: cursor = [" + cursor + "]");
        List<AgentProductModel> agentProductModelList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    agentProductModelList.add(cursorToAgentProductModelJoinProductsMasterJoinCompanyMaster(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        Log.d(TAG, "getAgentProductModelList() returned: " + agentProductModelList);
        return agentProductModelList;
    }

    private static AgentProductModel cursorToAgentProductModelJoinProductsMasterJoinCompanyMaster(Cursor cursor) {
        Log.d(TAG, "cursorToAgentProductModelJoinProductsMasterJoinCompanyMaster() called with: cursor = [" + cursor + "]");
        AgentProductModel agentProductModel = new AgentProductModel(
                cursor.getLong(cursor.getColumnIndex(COLUMN_PRODUCT_ID)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_AGENT_ID))
        );
        agentProductModel.setId(cursor.getLong(cursor.getColumnIndex("APM." + _ID)));
        agentProductModel.setProductMasterModel(ProductMasterModel.cursorToProductMasterModel(cursor, true));
        agentProductModel.setCompanyMasterModel(CompanyMasterModel.cursorToCompanyMasterModel(cursor, true));
        Log.d(TAG, "cursorToAgentProductModelJoinProductsMasterJoinCompanyMaster() returned: " + agentProductModel);
        return agentProductModel;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public ProductMasterModel getProductMasterModel() {
        return productMasterModel;
    }

    public void setProductMasterModel(ProductMasterModel productMasterModel) {
        this.productMasterModel = productMasterModel;
    }

    public CompanyMasterModel getCompanyMasterModel() {
        return companyMasterModel;
    }

    public void setCompanyMasterModel(CompanyMasterModel companyMasterModel) {
        this.companyMasterModel = companyMasterModel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(productId);
        dest.writeLong(agentId);
        dest.writeParcelable(productMasterModel, flags);
        dest.writeParcelable(companyMasterModel, flags);
    }

}
