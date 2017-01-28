package rkr.binatestation.pathrakkaran.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import rkr.binatestation.pathrakkaran.R;
import rkr.binatestation.pathrakkaran.models.UserDetailsModel;
import rkr.binatestation.pathrakkaran.modules.profile.UserProfileActivity;
import rkr.binatestation.pathrakkaran.network.VolleySingleTon;

import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER;


/**
 * Created by RKR on 8/2/2016.
 * ProductAdapter.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ItemHolder> {

    private List<UserDetailsModel> mUserDetailsModelList = new ArrayList<>();

    public void setUserDetailsModelList(List<UserDetailsModel> userDetailsModelList) {
        this.mUserDetailsModelList = userDetailsModelList;
        notifyDataSetChanged();
    }

    public void addItem(UserDetailsModel userDetailsModel) {
        if (mUserDetailsModelList != null) {
            int count = getItemCount();
            mUserDetailsModelList.add(userDetailsModel);
            notifyItemInserted(count);
        }
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        UserDetailsModel userDetailsModel = mUserDetailsModelList.get(position);
        if (userDetailsModel != null) {
            holder.nameTextView.setText(userDetailsModel.getName());
            holder.mobileTextView.setText(userDetailsModel.getMobile());
            holder.networkImageView.setImageUrl(
                    VolleySingleTon.getDomainUrlForImage() + userDetailsModel.getImage(),
                    VolleySingleTon.getInstance(holder.networkImageView.getContext()).getImageLoader()
            );
            holder.networkImageView.setDefaultImageResId(R.drawable.ic_face_24dp);
            holder.networkImageView.setErrorImageResId(R.drawable.ic_face_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return mUserDetailsModelList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        private NetworkImageView networkImageView;
        private TextView nameTextView;
        private TextView mobileTextView;

        ItemHolder(View itemView) {
            super(itemView);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.AULI_image);
            nameTextView = (TextView) itemView.findViewById(R.id.AULI_name);
            mobileTextView = (TextView) itemView.findViewById(R.id.AULI_mobile);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    if (context != null) {
                        context.startActivity(new Intent(context, UserProfileActivity.class)
                                .putExtra(KEY_USER, mUserDetailsModelList.get(getAdapterPosition())));
                    }
                }
            });
        }
    }
}
