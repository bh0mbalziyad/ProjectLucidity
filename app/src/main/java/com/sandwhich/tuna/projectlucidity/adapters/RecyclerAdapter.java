package com.sandwhich.tuna.projectlucidity.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
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
import com.sandwhich.tuna.projectlucidity.models.Post;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {
    public final List<Post> itemModels = new ArrayList<>();
    public static Context context;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public RecyclerAdapter(Context c){
        context = c;
    }

    public void addItems(@NonNull Collection<Post> models){
        itemModels.addAll(models);
        notifyItemRangeInserted(itemModels.size()-1,itemModels.size());
    }

    public int clearItems(){
        try{
            itemModels.clear();
            return 1;
        }
        catch (Exception ex){
            return -1;
        }
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        return new RecyclerHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerHolder holder, int position) {
        //todo assign data to recyler view items from itemModels collection object
        holder.parentWebsite.setText(itemModels.get(position).getHost());
        holder.articleHeadline.setText(itemModels.get(position).getHeadline());
        holder.timeStamp.setText("8h");
        imageLoader.loadImage(itemModels.get(position).getImageUrl(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                failReason.getCause().printStackTrace();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.headerImage.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    class RecyclerHolder extends RecyclerView.ViewHolder{
        TextView parentWebsite,timeStamp,articleHeadline;
        ImageView headerImage;
        public RecyclerHolder(View itemView) {
            super(itemView);
            parentWebsite = itemView.findViewById(R.id.parent_website);
            timeStamp= itemView.findViewById(R.id.timestamp);
            articleHeadline= itemView.findViewById(R.id.article_header_content);
            headerImage= itemView.findViewById(R.id.article_header_image);
        }
    }
}
