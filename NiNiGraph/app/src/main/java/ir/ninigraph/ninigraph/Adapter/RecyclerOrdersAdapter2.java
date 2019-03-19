package ir.ninigraph.ninigraph.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ir.ninigraph.ninigraph.Activity.OrderDrawingActivity;
import ir.ninigraph.ninigraph.Model.Orders;
import ir.ninigraph.ninigraph.R;

public class RecyclerOrdersAdapter2 extends RecyclerView.Adapter<RecyclerOrdersAdapter2.RecyclerViewHolder> {

    Context context;
    List<Orders.OrderDrawingModel> orderList;

    public RecyclerOrdersAdapter2(Context context, List<Orders.OrderDrawingModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recycler_orders, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        final Orders.OrderDrawingModel order = orderList.get(position);

        if (order.getStatus() == 3){

            holder.cardOrder.setVisibility(View.GONE);
            holder.cardOrder2.setVisibility(View.VISIBLE);
            holder.txtStatus.setText("در انتظار تایید مدیر");
        }
        else if (order.getStatus() == 4){

            orderList.remove(position);
            notifyItemRemoved(position);
        }

        if (position > 0){

            holder.cardOrder.setVisibility(View.GONE);
            holder.cardOrder2.setVisibility(View.VISIBLE);
            holder.txtStatus.setText("ابتدا سفارش خود را کامل کنید");
        }

        holder.txtOrderId.setText(String.valueOf(order.getId()));
        holder.txtOrderType.setText("سفارش طراحی سیاه قلم");
        holder.txtOrderInfo.setText("اندازه عکس:");
        switch (order.getSize()) {

            case 30:
                holder.txtOrderInfoCount.setText("(30 × 40)");
                break;
            case 35:
                holder.txtOrderInfoCount.setText("(35 × 50)");
                break;
            case 50:
                holder.txtOrderInfoCount.setText("(50 × 70)");
                break;
        }
        holder.txtOrderDuration.setText(order.getDoing_expire() + " ساعت");

        holder.cardOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, OrderDrawingActivity.class);
                intent.putExtra("id", order.getId());
                intent.putExtra("size", order.getSize());
                intent.putExtra("dExpire", order.getDoing_expire());
                intent.putExtra("aExpire", order.getAccepting_expire());
                intent.putExtra("imageUrl", order.getImage_url());
                intent.putExtra("status", order.getStatus());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {


        CardView cardOrder;
        CardView cardOrder2;
        TextView txtStatus;
        TextView txtOrderId;
        TextView txtOrderType;
        TextView txtOrderInfoCount;
        TextView txtOrderInfo;
        TextView txtOrderDuration;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            cardOrder = itemView.findViewById(R.id.card_order);
            cardOrder2 = itemView.findViewById(R.id.card_order2);
            txtStatus = itemView.findViewById(R.id.txt_status);
            txtOrderId = itemView.findViewById(R.id.txt_order_id);
            txtOrderType = itemView.findViewById(R.id.txt_order_type);
            txtOrderInfoCount = itemView.findViewById(R.id.txt_order_info_count);
            txtOrderInfo = itemView.findViewById(R.id.txt_order_info);
            txtOrderDuration = itemView.findViewById(R.id.txt_order_duration);
        }
    }
}
