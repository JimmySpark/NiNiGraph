package ir.ninigraph.ninigraph.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ir.ninigraph.ninigraph.Model.Picture;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.GlideApp;


public class RecyclerOccasionalAdapter extends RecyclerView.Adapter<RecyclerOccasionalAdapter.RecyclerViewHolder>{

    Context context;
    List<Picture> pictureList;

    public RecyclerOccasionalAdapter(Context context, List<Picture> pictureList) {
        this.context = context;
        this.pictureList = pictureList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_occasional, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        GlideApp
                .with(context)
                .load(pictureList.get(position).getUrl())
                .placeholder(R.drawable.img_logo2)
                .skipMemoryCache(true)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
        }
    }
}
