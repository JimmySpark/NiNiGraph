package ir.ninigraph.ninigraph.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ir.ninigraph.ninigraph.Model.OrderEdit;
import ir.ninigraph.ninigraph.R;

public class RecyclerOrderEditAdapter extends RecyclerView.Adapter<RecyclerOrderEditAdapter.RecyclerViewHolder>{

    Context context;
    List<OrderEdit> orderList;

    public RecyclerOrderEditAdapter(Context context, List<OrderEdit> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_order_edit, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        //Values
        OrderEdit orderEdit = orderList.get(position);

        //Set Data
        holder.txt_order_count.setText(orderEdit.getCount() + "");
        holder.txt_order_id.setText(orderEdit.getId() + "");
        holder.txt_order_status.setText(orderEdit.getStatus() + "");
        holder.txt_order_image_status.setText(orderEdit.getImage_status() + "");
        holder.txt_date.setText(orderEdit.getDate());
        holder.txt_time.setText(orderEdit.getTime());
        holder.txt_price.setText(orderEdit.getPrice() + "");
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView txt_order_count, txt_order_id, txt_order_status, txt_order_image_status, txt_date, txt_time, txt_price;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_order_count = itemView.findViewById(R.id.txt_order_count);
            txt_order_id = itemView.findViewById(R.id.txt_order_id);
            txt_order_status = itemView.findViewById(R.id.txt_order_status);
            txt_order_image_status = itemView.findViewById(R.id.txt_order_image_status);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_time = itemView.findViewById(R.id.txt_time);
            txt_price = itemView.findViewById(R.id.txt_price);
        }
    }
}
