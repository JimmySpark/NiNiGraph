package ir.ninigraph.ninigraph.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ir.ninigraph.ninigraph.Model.OrderDrawingData;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.GlideApp;

public class RecyclerDrawingWork1Adapter extends RecyclerView.Adapter<RecyclerDrawingWork1Adapter.RecyclerViewHolder>{

    Context context;
    List<OrderDrawingData.WorkModel> list;

    public RecyclerDrawingWork1Adapter(Context context, List<OrderDrawingData.WorkModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_drawing_work1, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        OrderDrawingData.WorkModel work = list.get(position);
        if (work.getUrl1().equals("") || work.getUrl1() == null)
            holder.card_image.setVisibility(View.GONE);
        else
            GlideApp.with(context)
                    .load(work.getUrl1())
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.img_logo2)
                    .into(holder.img_work);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        CardView card_image;
        ImageView img_work;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            card_image = itemView.findViewById(R.id.card_image);
            img_work = itemView.findViewById(R.id.img_work);
        }
    }
}
