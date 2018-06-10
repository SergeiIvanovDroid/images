package com.appdroid.develop.images;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ImagesAdapter extends RecyclerView.Adapter implements RealmChangeListener,Filterable {

    private RealmResults<Image> images;
    private Context context;
    private Realm realm;


    public ImagesAdapter(RealmResults<Image> images, Context context, Realm realm) {
        this.images = images;
        this.context = context;
        this.realm = realm;
    }

    @Override
    public void onChange() {
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new TagsFilter(this);
    }

    public void filterResuls(String text){
        if(text != null){
            String[] tags = text.replaceAll(" ","").split(",");
            RealmResults<Image> realmResults = getImagesByTags(tags);
            images = realmResults;
            notifyDataSetChanged();
        }

    }

    private RealmResults<Image> getImagesByTags(String[] tags){
        RealmQuery<Image> realmQuery = realm.where(Image.class).contains("tags",tags[0]);
        for(int i = 1; i < tags.length;i++){
            realmQuery = realmQuery.contains("tags",tags[i]);
        }
        return realmQuery.findAll();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        @BindView(R.id.itemImageView)ImageView ivItem;
        @BindView(R.id.itemTagsTextView)TextView tvItem;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this,itemView);
        }

        @Override
        public void onClick(View v) {
            String path = images.get(getAdapterPosition()).getPathToImage();
            Intent intent = new Intent(context,ImageActivity.class);
            intent.putExtra("path",path);
            context.startActivity(intent);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        Image image = images.get(position);
        Glide.with(context)
                .load(Uri.parse("file://" + image.getPathToImage()))
                .into(viewHolder.ivItem);

        viewHolder.tvItem.setText(image.getTags());
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
