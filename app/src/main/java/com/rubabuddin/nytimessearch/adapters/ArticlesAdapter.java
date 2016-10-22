package com.rubabuddin.nytimessearch.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rubabuddin.nytimessearch.R;
import com.rubabuddin.nytimessearch.models.Article;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by rubab.uddin on 10/16/2016.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int CARD_ARTICLE = 0,  CARD_HEADLINE = 1;

    private List<Article> articles;
   //private final OnItemClickListener listener;
    private Context context;

    public ArticlesAdapter(Context context, List<Article> articles) {
        this.articles = articles;
        //this.listener = listener;
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemClick(Article article);
    }


    private Context getContext() {
        return context;
    }

    public static class ViewHolderFull extends RecyclerView.ViewHolder {

        final TextView articleHeadline = (TextView) itemView.findViewById(R.id.tvArticleHeadline);
        final ImageView articlePhoto = (ImageView) itemView.findViewById(R.id.ivArticlePhoto);
        final CardView cardView = (CardView) itemView.findViewById(R.id.card_view);
        final RelativeLayout cardViewContainer = (RelativeLayout) itemView.findViewById(R.id.cardViewContainer);

        public ViewHolderFull(View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolderPartial extends RecyclerView.ViewHolder {

        final TextView articleHeadline = (TextView) itemView.findViewById(R.id.tvArticleHeadlineOnly);
        final CardView cardView = (CardView) itemView.findViewById(R.id.card_view_headline);

        public ViewHolderPartial(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Article article = articles.get(position);
        if (article.getPhoto() == null || article.getPhoto().equals("")){
            return CARD_HEADLINE;
        } else {
            return CARD_ARTICLE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case CARD_HEADLINE:
                View headlineView = inflater.inflate(R.layout.item_headline_card, parent, false);
                viewHolder = new ViewHolderPartial(headlineView);
                break;
            case CARD_ARTICLE:
                View articleView = inflater.inflate(R.layout.item_article_card, parent, false);
                viewHolder = new ViewHolderFull(articleView);
                break;
            default:
                View v = inflater.inflate(R.layout.item_headline_card, parent, false);
                viewHolder = new ViewHolderPartial(v);
                break;
        }

        return viewHolder;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private void configureViewHolderFull(final ViewHolderFull viewHolderFull, int position) {
        Article article = articles.get(position);
        viewHolderFull.articleHeadline.setText(article.getHeadline());

        Bitmap bitmap = getBitmapFromURL(article.getPhoto());
        Palette.from(bitmap).maximumColorCount(24).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                // Get the "vibrant" color swatch based on the bitmap
                Palette.Swatch vibrant = palette.getLightVibrantSwatch();
                if (vibrant != null) {
                    // Set the background color of a layout based on the vibrant color
                    viewHolderFull.cardViewContainer.setBackgroundColor(vibrant.getRgb());
                    // Update the title TextView with the proper text color
                    viewHolderFull.articleHeadline.setTextColor(vibrant.getTitleTextColor());
                }
            }
        });

        Glide.with(context)
                .load(article.getPhoto())
                .placeholder(R.drawable.article_blank)
                .error(R.drawable.article_blank)
                .override(800, 600) //height, width
                .fitCenter()
                //.override(article.getPhotoHeight(), article.getPhotoWidth()) //height, width
                //.transform(new RoundedCornersTransformation(10, 10))
                .into(viewHolderFull.articlePhoto);
        /*
        viewHolderFull.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(item);
            }
        });
*/

    }

    private void configureViewHolderPartial(ViewHolderPartial viewHolderPartial, int position) {
        Article article = articles.get(position);
        viewHolderPartial.articleHeadline.setText(article.getHeadline());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case CARD_HEADLINE:
                ViewHolderPartial viewHolderPartial = (ViewHolderPartial) viewHolder;
                configureViewHolderPartial(viewHolderPartial, position);
                break;
            case CARD_ARTICLE:
                ViewHolderFull viewHolderFull = (ViewHolderFull) viewHolder;
                configureViewHolderFull(viewHolderFull, position);
                break;
            default:
                viewHolderPartial = (ViewHolderPartial) viewHolder;
                configureViewHolderPartial(viewHolderPartial, position);
                break;
        }
    }

    /*

    @Override public void onViewRecycled(RecyclerView.ViewHolder viewHolder){
        super.onViewRecycled(viewHolder);
        switch (viewHolder.getItemViewType()) {
            case CARD_ARTICLE:
                //viewHolder = (ViewHolderFull) viewHolder;
                //Glide.clear((.articlePhoto);
                break;
            default:
                break;
        }

    }
    */


 /*       viewHolder.articleHeadline.setText(article.getHeadline());
        //holder.articlePhoto.setImageResource(articles.get(position).getPhoto());
        Log.d("DEBUG", article.getPhotoHeight() + " , " + article.getPhotoWidth());
        if (article.getPhoto() == null || article.getPhoto() == "") {
            Glide.with(context)
                    .load(R.drawable.article_blank)
                    .override(800, 600) //height, width
                    //.override(article.getPhotoHeight(), article.getPhotoWidth()) //height, width
                    //.transform(new RoundedCornersTransformation(10, 10))
                    .into(viewHolder.articlePhoto);
        } else {
            Glide.with(context)
                    .load(article.getPhoto())
                    .placeholder(R.drawable.article_blank)
                    .error(R.drawable.article_blank)
                    .override(800, 600) //height, width
                    //.override(article.getPhotoHeight(), article.getPhotoWidth()) //height, width
                    //.transform(new RoundedCornersTransformation(10, 10))
                    .into(viewHolder.articlePhoto);
        }
    }
*/
    @Override
    public int getItemCount() {
        return this.articles.size();
    }
}
