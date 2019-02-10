package ir.ninigraph.ninigraph.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import ir.ninigraph.ninigraph.Model.OrderEditTheme;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.GlideApp;

public class RecyclerOrderEditThemeAdapter extends RecyclerView.Adapter<RecyclerOrderEditThemeAdapter.RecyclerViewHolder>{

    Context context;
    List<OrderEditTheme.order_theme> themeList;

    public RecyclerOrderEditThemeAdapter(Context context, List<OrderEditTheme.order_theme> themeList) {
        this.context = context;
        this.themeList = themeList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recycler_order_edit_theme, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        //Values
        OrderEditTheme.order_theme theme = themeList.get(position);

        //Set Data
        GlideApp.
                with(context).
                load(theme.getUrl()).
                placeholder(R.drawable.img_logo2).
                skipMemoryCache(true).
                into(holder.img_theme);

        switch (theme.getImage_status()){

            case 0:
                holder.img_status.setImageResource(R.drawable.icon_warning);
                break;
            case 1:
                holder.img_status.setImageResource(R.drawable.icon_confirm);
                break;
            case 2:
                holder.img_status.setImageResource(R.drawable.icon_wait);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return themeList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView img_theme, img_status;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            img_theme = itemView.findViewById(R.id.img_theme);
            img_status = itemView.findViewById(R.id.img_status);
        }
    }
}
