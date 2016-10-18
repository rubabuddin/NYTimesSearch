package com.rubabuddin.nytimessearch.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rubabuddin.nytimessearch.R;
import com.rubabuddin.nytimessearch.models.Article;

import java.util.List;

/**
 * Created by rubab.uddin on 10/16/2016.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView articleHeadline;
        public ImageView articlePhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            //itemView.setOnClickListener(this);
            articleHeadline = (TextView) itemView.findViewById(R.id.tvArticleHeadline);
            articlePhoto = (ImageView) itemView.findViewById(R.id.ivArticlePhoto);
        }
    }
        private List<Article> articles;
        private Context context;

        public ArticlesAdapter(Context context, List<Article> articles) {
            this.articles = articles;
            this.context = context;
        }

        private Context getContext() {
            return context;
        }

        @Override
        public ArticlesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View articleView = inflater.inflate(R.layout.item_article_card, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(articleView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ArticlesAdapter.ViewHolder viewHolder, int position) {

            Article article = articles.get(position);

            viewHolder.articleHeadline.setText(articles.get(position).getHeadline());
            //holder.articlePhoto.setImageResource(articles.get(position).getPhoto());

            Glide.with(context)
                    .load(articles.get(position).getPhoto())
                    .placeholder(R.drawable.article_blank)
                    .error(R.drawable.article_blank)
                    //.transform(new RoundedCornersTransformation(10, 10))
                    .into(viewHolder.articlePhoto);

        }

        @Override
        public int getItemCount() {
            return this.articles.size();
        }
}
