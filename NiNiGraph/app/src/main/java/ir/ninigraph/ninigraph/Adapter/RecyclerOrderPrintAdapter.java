package ir.ninigraph.ninigraph.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ir.ninigraph.ninigraph.Model.OrderDrawing;
import ir.ninigraph.ninigraph.R;

public class RecyclerOrderPrintAdapter extends RecyclerView.Adapter<RecyclerOrderPrintAdapter.RecyclerViewHolder> {

    Context context;
    List<OrderDrawing.OrderDrawingModel> orderList;

    public RecyclerOrderPrintAdapter(Context context, List<OrderDrawing.OrderDrawingModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_order_drawing, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {

        //Values
        final OrderDrawing.OrderDrawingModel order = orderList.get(position);

        //Set Data
        holder.txtOrderId.setText(String.valueOf(order.id));
        switch (order.size){

            case 1:
                holder.txtSize.setText("(A4)");
                break;
            case 2:
                holder.txtSize.setText("(A5)");
                break;
            case 3:
                holder.txtSize.setText("(A6)");
                break;
        }
        holder.txtDate.setText(order.date);
        holder.txtTime.setText(order.time);
        holder.txtPrice.setText(String.valueOf(order.price));

        switch (order.status) {

            case 1:
                holder.txtOrderStatus.setText("ثبت شد");
                break;
            case 2:
                holder.txtOrderStatus.setText("ارسال شد");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

         CardView cardOrder;
         TextView txtOrderId;
         TextView txtSize;
         TextView txtOrderStatus;
         TextView txtDate;
         TextView txtTime;
         TextView txtPrice;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            cardOrder = itemView.findViewById(R.id.card_order);
            txtOrderId = itemView.findViewById(R.id.txt_order_id);
            txtSize = itemView.findViewById(R.id.txt_size);
            txtOrderStatus = itemView.findViewById(R.id.txt_order_status);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtTime = itemView.findViewById(R.id.txt_time);
            txtPrice = itemView.findViewById(R.id.txt_price);
        }
    }
}
