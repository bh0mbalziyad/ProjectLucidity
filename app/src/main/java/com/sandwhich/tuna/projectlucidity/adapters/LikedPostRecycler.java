package com.sandwhich.tuna.projectlucidity.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sandwhich.tuna.projectlucidity.R;
import com.sandwhich.tuna.projectlucidity.interfaces.ItemClickListener;
import com.sandwhich.tuna.projectlucidity.models.Post;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LikedPostRecycler extends RecyclerView.Adapter<LikedPostRecycler.ViewHolder> {
    Context context;
    List<Post> postList;
    ItemClickListener itemClickListener;
    Post post;

    public LikedPostRecycler(Context context,ItemClickListener itemClickListener){
        postList = new ArrayList<>();
        this.context=context;
        this.itemClickListener=itemClickListener;
    }

    public void addItems(@NonNull Collection<Post> posts){
        this.postList.clear();
        this.postList.addAll(posts);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(this.context).inflate(R.layout.liked_post_item_list,parent,false);
        return new ViewHolder(v,this.itemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        post = postList.get(position);
        holder.parentWebsite.setText(post.getHost());
        holder.timeStamp.setText(post.getPostDate().getDate());
        holder.articleHeadline.setText(post.getHeadline());
        Glide.with(context)
                .load(post.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.headerImage);


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View layoutView;
        ItemClickListener listener;
        TextView parentWebsite,timeStamp,articleHeadline;
        ImageView headerImage;
        public ViewHolder(View itemView,ItemClickListener listener) {
            super(itemView);
            this.layoutView=itemView;
            this.listener = listener;
            parentWebsite = itemView.findViewById(R.id.parent_website);
            timeStamp= itemView.findViewById(R.id.timestamp);
            articleHeadline= itemView.findViewById(R.id.article_header_content);
            headerImage= itemView.findViewById(R.id.article_header_image);
            headerImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view,getAdapterPosition(),layoutView);
        }
    }
}
