package com.example.viltrade2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.viltrade2.R;
import com.example.viltrade2.models.NewProductsModel;
import com.example.viltrade2.models.ShowAllModel;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.itemViewHolder> {

    private Context context;
    private List<NewProductsModel> list;

    public SearchAdapter(Context context, List<NewProductsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_all_item, parent, false);
        return new itemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
        NewProductsModel model = list.get(position);
        Glide.with(context).load(model.getImg_url()).into(holder.mItemImage);
        holder.mCost.setText("Rp. " + model.getPrice());
        holder.mName.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<NewProductsModel> newList) {
        list = newList;
        notifyDataSetChanged();
    }

    class itemViewHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImage;
        private TextView mCost;
        private TextView mName;

        public itemViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemImage = itemView.findViewById(R.id.item_image);
            mCost = itemView.findViewById(R.id.item_cost);
            mName = itemView.findViewById(R.id.item_name);
        }
    }
}
