package ir.ninigraph.ninigraph.Adapter;

import android.content.Context;
import java.util.List;
import ir.ninigraph.ninigraph.Model.Picture;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.GlideApp;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;


public class SliderAdapter extends ss.com.bannerslider.adapters.SliderAdapter{

    Context context;
    List<Picture> pictureList;

    public SliderAdapter(Context context, List<Picture> pictureList) {
        this.context = context;
        this.pictureList = pictureList;
    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder holder) {

        GlideApp
                .with(context)
                .load(pictureList.get(position).getUrl())
                .placeholder(R.drawable.logo)
                .skipMemoryCache(true)
                .into(holder.imageView);
    }
}
