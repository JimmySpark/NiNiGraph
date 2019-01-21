package ir.ninigraph.ninigraph.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ir.ninigraph.ninigraph.Model.PaymentCaption;
import ir.ninigraph.ninigraph.R;

public class RecyclerPaymentCaptionAdapter extends RecyclerView.Adapter<RecyclerPaymentCaptionAdapter.RecyclerViewHolder>{

    Context context;
    List<PaymentCaption> captionList;

    public RecyclerPaymentCaptionAdapter(Context context, List<PaymentCaption> captionList) {
        this.context = context;
        this.captionList = captionList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_payment_caption, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        //Values
        PaymentCaption paymentCaption = captionList.get(position);

        holder.txt_caption.setText("• " + paymentCaption.getCaption());
    }

    @Override
    public int getItemCount() {
        return captionList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        TextView txt_caption;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_caption = itemView.findViewById(R.id.txt_caption);
        }
    }
}
