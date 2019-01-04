package ir.ninigraph.ninigraph.Adapter;

import ir.ninigraph.ninigraph.R;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class SliderAdapter extends ss.com.bannerslider.adapters.SliderAdapter{

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {

        switch (position){

            case 0:
                imageSlideViewHolder.bindImageSlide(R.drawable.img_sample1);
                break;
            case 1:
                imageSlideViewHolder.bindImageSlide(R.drawable.img_sample2);
                break;
            case 2:
                imageSlideViewHolder.bindImageSlide(R.drawable.img_sample3);
                break;
        }
    }
}
