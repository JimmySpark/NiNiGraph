package ir.ninigraph.ninigraph.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ir.ninigraph.ninigraph.Model.HomePage;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.GlideApp;


public class RecyclerAdsAdapter extends RecyclerView.Adapter<RecyclerAdsAdapter.RecyclerViewHolder>{

    Context context;
    List<HomePage.AdsModel> adsList;

    public RecyclerAdsAdapter(Context context, List<HomePage.AdsModel> adsList) {
        this.context = context;
        this.adsList = adsList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_ads, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        final HomePage.AdsModel ads = adsList.get(position);

        GlideApp
                .with(context)
                .load(ads.getUrl())
                .placeholder(R.drawable.img_logo2)
                .skipMemoryCache(true)
                .into(holder.img_ad);

        if (ads.getIsLinkEnable() == 1){

            holder.card_ad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent lunchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(ads.getLink()));
                    context.startActivity(lunchBrowser);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return adsList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        CardView card_ad;
        ImageView img_ad;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            card_ad = itemView.findViewById(R.id.card_ad);
            img_ad = itemView.findViewById(R.id.img_ad);
        }
    }
}
