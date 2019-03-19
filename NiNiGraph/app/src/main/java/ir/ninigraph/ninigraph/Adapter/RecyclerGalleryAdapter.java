package ir.ninigraph.ninigraph.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.ninigraph.ninigraph.Activity.ChoosePictureActivity;
import ir.ninigraph.ninigraph.Activity.NewEditOrderActivity;
import ir.ninigraph.ninigraph.Activity.PaymentEditActivity;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.GlideApp;
import ir.ninigraph.ninigraph.Util.ImageUtil;

public class RecyclerGalleryAdapter extends RecyclerView.Adapter<RecyclerGalleryAdapter.RecyclerViewHolder> {

    Context context;
    List<String> picturesList;
    List<Uri> uriList;
    int picCount = 0;

    public RecyclerGalleryAdapter(Context context, List<String> picturesList) {
        this.context = context;
        this.picturesList = picturesList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recycler_gallery, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {

        uriList = new ArrayList<>();
        final String picture = picturesList.get(position);
        final Uri picUri = Uri.parse(picture);

        holder.image.setImageURI(picUri);

        holder.cardImage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if (holder.tickImage.getVisibility() == View.GONE) {

                    picCount++;
                    if (picCount <= 10){

                        //..Display
                        holder.tickImage.setVisibility(View.VISIBLE);
                        holder.tickImage.setAnimation(AnimationUtils.loadAnimation(context, R.anim.show_circle_img_tick));
                        holder.txtSelectedCount.setText(String.valueOf(picCount));
                        ChoosePictureActivity.picCount.setText("×" + picCount);

                        uriList.add(picCount - 1, picUri);
                        //Toast.makeText(context, uriList.get(picCount - 1).toString(), Toast.LENGTH_SHORT).show();
                    }



                    /*//..On Choose Btn Click
                    NewEditOrderActivity.txt_choose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(context, PaymentEditActivity.class);
                            for (int i = 0; i < selected_lists.size(); i++) {

                                intent.putExtra("count", selected_lists.size());
                                intent.putIntegerArrayListExtra("selected_themes" + i, selected_lists.get(i));
                            }
                            context.startActivity(intent);
                        }
                    });*/
                } else {


                    picCount--;
                    if (picCount >= 0){

                        //..Display
                        holder.tickImage.setVisibility(View.GONE);
                        holder.tickImage.setAnimation(AnimationUtils.loadAnimation(context, R.anim.hide_circle_img_tick));
                        ChoosePictureActivity.picCount.setText("×" + picCount);

                        uriList.remove(picCount - 1);
                        Toast.makeText(context, uriList.get(picCount - 1).toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return picturesList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        CardView cardImage;
        ImageView image;
        RelativeLayout tickImage;
        TextView txtSelectedCount;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            cardImage = itemView.findViewById(R.id.card_image);
            image = itemView.findViewById(R.id.image);
            tickImage = itemView.findViewById(R.id.tick_image);
            txtSelectedCount = itemView.findViewById(R.id.txt_selected_count);
        }
    }
}
