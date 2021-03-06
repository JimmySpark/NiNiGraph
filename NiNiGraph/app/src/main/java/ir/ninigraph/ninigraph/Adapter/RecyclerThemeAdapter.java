package ir.ninigraph.ninigraph.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.ninigraph.ninigraph.Activity.NewEditOrderActivity;
import ir.ninigraph.ninigraph.Activity.PaymentEditActivity;
import ir.ninigraph.ninigraph.Activity.UploadEditPicActivity;
import ir.ninigraph.ninigraph.Model.Theme;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.GlideApp;

public class RecyclerThemeAdapter extends RecyclerView.Adapter<RecyclerThemeAdapter.RecyclerViewHolder> {

    //Values
    Context context;
    List<Theme> themeList;
    TextView txt_selected_count;
    List<ArrayList<Integer>> selected_lists;
    int pos;
    ArrayList<Integer> selected_theme;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public RecyclerThemeAdapter(Context context, List<Theme> themeList, TextView txt_selected_count, List<ArrayList<Integer>> selected_lists, int position) {
        this.context = context;
        this.themeList = themeList;
        this.selected_lists = selected_lists;
        this.pos = position;
        this.txt_selected_count = txt_selected_count;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_theme, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        final Theme theme = themeList.get(position);
        selected_theme = new ArrayList<>();


        //Load Theme Image
        GlideApp
                .with(context)
                .load(theme.getUrl())
                .placeholder(R.drawable.img_logo2)
                .skipMemoryCache(true)
                .into(holder.image);

        //On Theme Click
        holder.card_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.circle_img_tick.getVisibility() == View.GONE) {

                    //..Display
                    holder.circle_img_tick.setVisibility(View.VISIBLE);
                    holder.circle_img_tick.setAnimation(AnimationUtils.loadAnimation(context, R.anim.show_circle_img_tick));

                    //..Get And Set Id
                    editor.putInt("c", preferences.getInt("c", 0) + 1).apply();
                    selected_theme.add(theme.getId());
                    selected_lists.remove(pos);
                    selected_lists.add(pos, selected_theme);
                    NewEditOrderActivity.txt_choose.setVisibility(View.VISIBLE);
                    if (preferences.getInt("c", 0) == 1)
                        NewEditOrderActivity.txt_choose.setAnimation(AnimationUtils.loadAnimation(context, R.anim.show_circle_img_tick));
                    NewEditOrderActivity.txt_choose.setText(preferences.getInt("c", 0) + " تا انتخاب کردم");

                    //..Show Selected Themes Count
                    txt_selected_count.setText(selected_theme.size() + "");

                    //..On Choose Btn Click
                    NewEditOrderActivity.txt_choose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(context, PaymentEditActivity.class);
                            for (int i = 0; i < selected_lists.size(); i++) {

                                intent.putExtra("count", selected_lists.size());
                                intent.putIntegerArrayListExtra("selected_themes" + i, selected_lists.get(i));
                            }
                            context.startActivity(intent);
                        }
                    });
                } else {
                    holder.circle_img_tick.setVisibility(View.GONE);
                    holder.circle_img_tick.setAnimation(AnimationUtils.loadAnimation(context, R.anim.hide_circle_img_tick));

                    for (int i = 0; i < selected_theme.size(); i++) {

                        if (selected_theme.get(i) == theme.getId())
                            selected_theme.remove(i);
                    }

                    editor.putInt("c", preferences.getInt("c", 0) - 1).apply();
                    txt_selected_count.setText(String.valueOf(selected_theme.size()));
                    if (preferences.getInt("c", 0) == 0) {
                        NewEditOrderActivity.txt_choose.setAnimation(AnimationUtils.loadAnimation(context, R.anim.hide_circle_img_tick));
                        NewEditOrderActivity.txt_choose.setVisibility(View.GONE);
                    }
                    NewEditOrderActivity.txt_choose.setText(preferences.getInt("c", 0) + " تا انتخاب کردم");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return themeList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        CardView card_theme;
        ImageView image;
        CircleImageView circle_img_tick;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            card_theme = itemView.findViewById(R.id.card_theme);
            image = itemView.findViewById(R.id.image);
            circle_img_tick = itemView.findViewById(R.id.circle_img_tick);
        }
    }
}
