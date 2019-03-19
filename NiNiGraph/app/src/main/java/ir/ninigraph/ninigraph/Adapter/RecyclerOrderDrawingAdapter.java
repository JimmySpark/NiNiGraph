package ir.ninigraph.ninigraph.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ir.ninigraph.ninigraph.Activity.UploadEditPicActivity;
import ir.ninigraph.ninigraph.Model.OrderDrawing;
import ir.ninigraph.ninigraph.Model.OrderEdit;
import ir.ninigraph.ninigraph.R;

public class RecyclerOrderDrawingAdapter extends RecyclerView.Adapter<RecyclerOrderDrawingAdapter.RecyclerViewHolder> {

    Context context;
    List<OrderDrawing.OrderDrawingModel> orderList;

    public RecyclerOrderDrawingAdapter(Context context, List<OrderDrawing.OrderDrawingModel> orderList) {
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

            case 30:
                holder.txtSize.setText("(30 × 40)");
                break;
            case 35:
                holder.txtSize.setText("(35 × 50)");
                break;
            case 50:
                holder.txtSize.setText("(50 × 70)");
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
                holder.txtOrderStatus.setText("در حال طراحی");
                break;
            case 3:
                holder.txtOrderStatus.setText("در صف ارسال");
                break;
            case 4:
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
