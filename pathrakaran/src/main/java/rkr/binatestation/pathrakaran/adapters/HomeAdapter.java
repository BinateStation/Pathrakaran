package rkr.binatestation.pathrakaran.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rkr.binatestation.pathrakaran.R;


/**
 * Created by RKR on 8/2/2016.
 * HomeAdapter.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ItemHolder> {
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(View itemView) {
            super(itemView);
        }
    }
}
