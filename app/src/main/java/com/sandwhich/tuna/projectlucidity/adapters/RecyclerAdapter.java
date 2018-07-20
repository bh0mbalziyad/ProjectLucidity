package com.sandwhich.tuna.projectlucidity.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sandwhich.tuna.projectlucidity.R;
import com.sandwhich.tuna.projectlucidity.models.ItemModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {
    public final List<ItemModel> itemModels = new ArrayList<>();
    public static Context context;

    public RecyclerAdapter(Context c){
        context = c;
    }

    public void addItems(@NonNull Collection<ItemModel> models){
        itemModels.addAll(models);
        notifyItemRangeInserted(itemModels.size()-1,itemModels.size());
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        return new RecyclerHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        //todo assign data to recyler view items from itemModels collection object
        holder.articleHeadline.setText(itemModels.get(position).getArticleHeadline());
        holder.parentWebsite.setText(itemModels.get(position).getParentWebsite());
        holder.timeStamp.setText(itemModels.get(position).getTimeStamp());
    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    class RecyclerHolder extends RecyclerView.ViewHolder{
        TextView parentWebsite,timeStamp,articleHeadline;
        AppCompatImageView headerImage;
        public RecyclerHolder(View itemView) {
            super(itemView);
            parentWebsite = itemView.findViewById(R.id.parent_website);
            timeStamp= itemView.findViewById(R.id.timestamp);
            articleHeadline= itemView.findViewById(R.id.article_header_content);
            headerImage= itemView.findViewById(R.id.article_header_image);
        }
    }
}
