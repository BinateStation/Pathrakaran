package rkr.binatestation.pathrakkaran.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rkr.binatestation.pathrakkaran.R;
import rkr.binatestation.pathrakkaran.models.AgentProductModel;
import rkr.binatestation.pathrakkaran.models.CompanyMasterModel;
import rkr.binatestation.pathrakkaran.models.ProductMasterModel;
import rkr.binatestation.pathrakkaran.network.VolleySingleTon;


/**
 * Created by RKR on 8/2/2016.
 * ProductAdapter.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ItemHolder> {

    private List<AgentProductModel> mAgentProductModelList = new ArrayList<>();

    public void setProductModelList(List<AgentProductModel> agentProductModelList) {
        this.mAgentProductModelList = agentProductModelList;
        notifyDataSetChanged();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_product_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        AgentProductModel agentProductModel = mAgentProductModelList.get(position);
        ProductMasterModel productMasterModel = agentProductModel.getProductMasterModel();
        CompanyMasterModel companyMasterModel = agentProductModel.getCompanyMasterModel();
        if (companyMasterModel != null) {
            holder.companyNameTextView.setText(companyMasterModel.getCompanyName());
        }
        if (productMasterModel != null) {
            holder.productNameTextView.setText(productMasterModel.getProductName());
            holder.productTypeTextView.setText(productMasterModel.getProductTypeLabel(productMasterModel.getProductType()));
            holder.productPriceTextView.setText(String.format(Locale.getDefault(), "%s", productMasterModel.getProductPrice()));
            holder.productNetworkImageView.setImageUrl(
                    VolleySingleTon.getDomainUrlForImage() + productMasterModel.getProductImage(),
                    VolleySingleTon.getInstance(holder.productNetworkImageView.getContext()).getImageLoader()
            );
            holder.productNetworkImageView.setDefaultImageResId(R.drawable.ic_menu_gallery);
            holder.productNetworkImageView.setErrorImageResId(R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return mAgentProductModelList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        NetworkImageView productNetworkImageView;
        TextView productNameTextView;
        TextView companyNameTextView;
        TextView productTypeTextView;
        TextView productPriceTextView;

        ItemHolder(View itemView) {
            super(itemView);
            productNetworkImageView = (NetworkImageView) itemView.findViewById(R.id.APLI_product_image);
            productNameTextView = (TextView) itemView.findViewById(R.id.APLI_product_name);
            companyNameTextView = (TextView) itemView.findViewById(R.id.APLI_company_name);
            productTypeTextView = (TextView) itemView.findViewById(R.id.APLI_product_type);
            productPriceTextView = (TextView) itemView.findViewById(R.id.APLI_product_price);
        }
    }
}
