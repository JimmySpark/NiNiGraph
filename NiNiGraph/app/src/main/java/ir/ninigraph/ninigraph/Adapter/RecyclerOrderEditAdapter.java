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
import android.widget.Toast;

import java.util.List;

import ir.ninigraph.ninigraph.Activity.UploadEditPicActivity;
import ir.ninigraph.ninigraph.Model.OrderEdit;
import ir.ninigraph.ninigraph.R;

public class RecyclerOrderEditAdapter extends RecyclerView.Adapter<RecyclerOrderEditAdapter.RecyclerViewHolder> {

    Context context;
    List<OrderEdit> orderList;

    public RecyclerOrderEditAdapter(Context context, List<OrderEdit> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_order_edit, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {

        //Values
        final OrderEdit orderEdit = orderList.get(position);

        //Set Data
        holder.txt_order_count.setText(orderEdit.getCount() + "");
        holder.txt_order_id.setText(String.valueOf(orderEdit.getId()));
        holder.txt_date.setText(orderEdit.getDate());
        holder.txt_time.setText(orderEdit.getTime());
        holder.txt_price.setText(orderEdit.getPrice() + "");

        switch (orderEdit.getStatus()) {
            case 1:
                holder.txt_order_status.setText("عکس ها را ارسال کنید");
                holder.btn_up_down.setText("ارسال عکس");
                holder.img_up_down.setImageResource(R.drawable.icon_upload);
                holder.btn_upload_download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, UploadEditPicActivity.class);
                        intent.putExtra("status", orderEdit.getStatus());
                        intent.putExtra("order_id", orderEdit.getId());
                        context.startActivity(intent);
                    }
                });
                break;
            case 2:
                holder.txt_order_status.setText("در انتظار تایید عکس ها");
                holder.btn_up_down.setText("وضعیت عکس ها");
                holder.img_up_down.setVisibility(View.GONE);
                holder.btn_upload_download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, UploadEditPicActivity.class);
                        intent.putExtra("status", orderEdit.getStatus());
                        intent.putExtra("order_id", orderEdit.getId());
                        context.startActivity(intent);
                    }
                });
                break;
            case 3:
                holder.txt_order_status.setText("در حال طراحی");
                holder.btn_upload_download.setVisibility(View.GONE);
                break;
            case 4:
                holder.txt_order_status.setText("در انتظار تایید مدیر");
                holder.btn_upload_download.setVisibility(View.GONE);
                break;
            case 5:
                holder.txt_order_status.setText("تکمیل سفارش");
                holder.btn_up_down.setText("دریافت سفارش");
                holder.img_up_down.setImageResource(R.drawable.icon_download);
                holder.btn_upload_download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, UploadEditPicActivity.class);
                        intent.putExtra("status", orderEdit.getStatus());
                        intent.putExtra("order_id", orderEdit.getId());
                        context.startActivity(intent);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        CardView card_order;
        TextView txt_order_count, txt_order_id, txt_order_status, txt_date, txt_time, txt_price;
        LinearLayout btn_upload_download;
        Button btn_up_down;
        ImageView img_up_down;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            card_order = itemView.findViewById(R.id.card_order);
            txt_order_count = itemView.findViewById(R.id.txt_payment_title);
            txt_order_id = itemView.findViewById(R.id.txt_order_id);
            txt_order_status = itemView.findViewById(R.id.txt_order_status);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_time = itemView.findViewById(R.id.txt_time);
            txt_price = itemView.findViewById(R.id.txt_price);
            btn_upload_download = itemView.findViewById(R.id.btn_upload_download);
            btn_up_down = itemView.findViewById(R.id.btn_up_down);
            img_up_down = itemView.findViewById(R.id.img_up_down);
        }
    }
}
