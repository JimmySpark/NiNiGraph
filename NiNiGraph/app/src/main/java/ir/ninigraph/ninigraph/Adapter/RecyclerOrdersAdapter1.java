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

import ir.ninigraph.ninigraph.Activity.OrderEditActivity;
import ir.ninigraph.ninigraph.Model.Orders;
import ir.ninigraph.ninigraph.R;

public class RecyclerOrdersAdapter1 extends RecyclerView.Adapter<RecyclerOrdersAdapter1.RecyclerViewHolder> {

    Context context;
    List<Orders.OrderEditModel> orderList;

    public RecyclerOrdersAdapter1(Context context, List<Orders.OrderEditModel> orderList) {
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

        Orders.OrderEditModel order = orderList.get(position);

        if (order.getStatus() == 4){

            holder.cardOrder.setVisibility(View.GONE);
            holder.cardOrder2.setVisibility(View.VISIBLE);
            holder.txtStatus.setText("در انتظار تایید مدیر");
        }
        else if (order.getStatus() == 5){

            orderList.remove(position);
            notifyItemRemoved(position);
        }

        if (position > 0){

            holder.cardOrder.setVisibility(View.GONE);
            holder.cardOrder2.setVisibility(View.VISIBLE);
            holder.txtStatus.setText("ابتدا سفارش خود را کامل کنید");
        }

        holder.txtOrderId.setText(String.valueOf(order.getId()));
        holder.txtOrderType.setText("سفارش طراحی عکس کودک");
        holder.txtOrderInfo.setText("تعداد عکس:");
        holder.txtOrderInfoCount.setText(String.valueOf(order.getCount()));
        holder.txtOrderDuration.setText(order.getDoing_expire() + " ساعت");

        holder.cardOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, OrderEditActivity.class);
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
