package com.sandwhich.tuna.projectlucidity.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sandwhich.tuna.projectlucidity.R;
import com.sandwhich.tuna.projectlucidity.interfaces.ItemClickListener;
import com.sandwhich.tuna.projectlucidity.interfaces.TinyDB;
import com.sandwhich.tuna.projectlucidity.models.Post;
import com.sandwhich.tuna.projectlucidity.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {
    public final List<Post> itemModels = new ArrayList<>();
    TinyDB tdb;
    User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public static Context context;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ItemClickListener mListener;
    public RecyclerAdapter(Context c, User currentUser, ItemClickListener mListener){
        this.currentUser = currentUser;
        context = c;
        this.mListener = mListener;
        tdb = new TinyDB(c);

    }

    public void addItems(@NonNull Collection<Post> newModels){
        itemModels.clear();
        itemModels.addAll(newModels);
        notifyDataSetChanged();
    }

    public void updateItem(Post p,int position){
        itemModels.set(position,p);
        notifyItemChanged(position);
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        return new RecyclerHolder(v,this.mListener);
    }

    @Override
    public void onBindViewHolder(final RecyclerHolder holder, int position) {
        if (itemModels.get(position).getUsersWhoLiked().containsKey(currentUser.getUid())){
            switch (itemModels.get(position).getUsersWhoLiked().get(currentUser.getUid())){
                case LIKED:
                    holder.postLike.setImageDrawable(context.getDrawable(R.drawable.post_liked));
                    holder.postDislike.setImageDrawable(context.getDrawable(R.drawable.post_dislike));
                    break;
                case DISLIKED:
                    holder.postDislike.setImageDrawable(context.getDrawable(R.drawable.post_disliked));
                    holder.postLike.setImageDrawable(context.getDrawable(R.drawable.post_like));
                    break;
                case NEUTRAl:
                    holder.postDislike.setImageDrawable(context.getDrawable(R.drawable.post_dislike));
                    holder.postLike.setImageDrawable(context.getDrawable(R.drawable.post_like));
                    break;
                default:
                        holder.postDislike.setImageDrawable(context.getDrawable(R.drawable.post_dislike));
                        holder.postLike.setImageDrawable(context.getDrawable(R.drawable.post_like));
            }
        }
        holder.parentWebsite.setText(itemModels.get(position).getHost());
        holder.articleHeadline.setText(itemModels.get(position).getHeadline());
        holder.timeStamp.setText(itemModels.get(position).getPostDate().getDate());
        holder.postLikeCount.setText(""+itemModels.get(position).getPostLikeCount());
        try{
            holder.headerImage.setImageBitmap(tdb.getImage("bitmaps/"+itemModels.get(position).getPostUrl()+".bmp"));
        }catch (Exception ex){
            System.out.println("Unable to fetch bitmaps");
            ex.printStackTrace();
        }
//        try{
//            holder.headerImage.setImageBitmap(tdb.getObject("bitmaps/"+itemModels.get(position).getUrlForFirebasePath()+".bmp",Bitmap.class));
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//        imageLoader.loadImage(itemModels.get(position).getImageUrl(), new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                failReason.getCause().printStackTrace();
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                holder.headerImage.setImageBitmap(loadedImage);
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View layoutView;
        ItemClickListener listener;
        TextView parentWebsite,timeStamp,articleHeadline,postLikeCount;
        ImageView headerImage,postLike,postDislike;
        public RecyclerHolder(View itemView,ItemClickListener mListener) {
            super(itemView);
            this.layoutView=itemView;
            this.listener = mListener;
            postLikeCount = itemView.findViewById(R.id.likeCounter);
            postDislike = itemView.findViewById(R.id.postDislike);
            postLike = itemView.findViewById(R.id.postLike);
            postLike.setOnClickListener(this);
            postDislike.setOnClickListener(this);
            parentWebsite = itemView.findViewById(R.id.parent_website);
            timeStamp= itemView.findViewById(R.id.timestamp);
            articleHeadline= itemView.findViewById(R.id.article_header_content);
            headerImage= itemView.findViewById(R.id.article_header_image);
            headerImage.setOnClickListener(this);
//            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(),layoutView);
        }
    }
}
