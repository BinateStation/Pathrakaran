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
import rkr.binatestation.pathrakkaran.models.TransactionModel;
import rkr.binatestation.pathrakkaran.models.UserDetailsModel;
import rkr.binatestation.pathrakkaran.network.VolleySingleTon;
import rkr.binatestation.pathrakkaran.utils.GeneralUtils;

import static rkr.binatestation.pathrakkaran.utils.Constants.DATE_PATTERN_DD_MM_YYYY_HH_MM_A;


/**
 * Created by RKR on 8/2/2016.
 * ProductAdapter.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ItemHolder> {

    private List<TransactionModel> mTransactionModelList = new ArrayList<>();

    public void setTransactionModelList(List<TransactionModel> agentProductModelList) {
        this.mTransactionModelList = agentProductModelList;
        notifyDataSetChanged();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_product_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        TransactionModel transactionModel = mTransactionModelList.get(position);
        if (transactionModel != null) {
            holder.transactionDateTextView.setText(GeneralUtils.getDate(transactionModel.getDate(), DATE_PATTERN_DD_MM_YYYY_HH_MM_A));
            holder.balanceAmountTextView.setText(String.format(Locale.getDefault(), "%.2f", transactionModel.getAmount()));
            UserDetailsModel userDetailsModel = transactionModel.getUserDetailsModel();
            if (userDetailsModel != null) {
                holder.userNameTextView.setText(userDetailsModel.getName());
                holder.userImageView.setImageUrl(
                        VolleySingleTon.getDomainUrlForImage() + userDetailsModel.getImage(),
                        VolleySingleTon.getInstance(holder.userImageView.getContext()).getImageLoader()
                );
            }
        }
        holder.userImageView.setDefaultImageResId(R.drawable.ic_face_24dp);
        holder.userImageView.setErrorImageResId(R.drawable.ic_face_24dp);
    }

    @Override
    public int getItemCount() {
        return mTransactionModelList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        private NetworkImageView userImageView;
        private TextView userNameTextView;
        private TextView transactionDateTextView;
        private TextView balanceLabelTextView;
        private TextView balanceAmountTextView;

        ItemHolder(View itemView) {
            super(itemView);
            userImageView = (NetworkImageView) itemView.findViewById(R.id.APLI_product_image);
            userNameTextView = (TextView) itemView.findViewById(R.id.APLI_product_name);
            transactionDateTextView = (TextView) itemView.findViewById(R.id.APLI_company_name);
            balanceLabelTextView = (TextView) itemView.findViewById(R.id.APLI_product_type);
            balanceAmountTextView = (TextView) itemView.findViewById(R.id.APLI_product_price);
            balanceLabelTextView.setText(R.string.balance);
        }
    }
}
