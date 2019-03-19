package ir.ninigraph.ninigraph.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.ninigraph.ninigraph.Model.Theme;
import ir.ninigraph.ninigraph.Model.ThemeCategory;
import ir.ninigraph.ninigraph.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class RecyclerThemeCategoryAdapter extends RecyclerView.Adapter<RecyclerThemeCategoryAdapter.RecyclerViewHolder> {

    Context context;
    List<ThemeCategory> themeCategoryList;
    List<ArrayList<Integer>> selected_lists;

    public RecyclerThemeCategoryAdapter(Context context, List<ThemeCategory> themeCategoryList) {
        this.context = context;
        this.themeCategoryList = themeCategoryList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_theme_category, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {

        //Values
        final ThemeCategory category = themeCategoryList.get(position);
        selected_lists = new ArrayList<>();
        for (int i = 0; i < themeCategoryList.size(); i++)
            selected_lists.add(i, null);


        holder.txt_title.setText(category.getTitle());
        holder.txt_selected_count.setText("0");

        holder.card_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.recycler_theme.getVisibility() == View.GONE) {

                    getTheme(
                            category.getId(),
                            holder.img_icon,
                            holder.txt_selected_count,
                            holder.recycler_theme,
                            holder.progressBar,
                            position
                    );
                } else {
                    holder.img_icon.setImageResource(R.drawable.icon_closed_category);
                    holder.recycler_theme.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return themeCategoryList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        CardView card_category;
        TextView txt_title;
        TextView txt_selected_count;
        ImageView img_icon;
        RecyclerView recycler_theme;
        ProgressBar progressBar;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            card_category = itemView.findViewById(R.id.card_category);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_selected_count = itemView.findViewById(R.id.txt_selected_count);
            img_icon = itemView.findViewById(R.id.img_icon);
            recycler_theme = itemView.findViewById(R.id.recycler_theme);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    //Classes
    private void getTheme(int id, final ImageView img_icon, final TextView txt_selected_count,
                          final RecyclerView recycler_theme, final ProgressBar progressBar, final int position
    ) {

        progressBar.setVisibility(View.VISIBLE);

        apiService.getTheme(id).enqueue(new Callback<List<Theme>>() {
            @Override
            public void onResponse(Call<List<Theme>> call, Response<List<Theme>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        img_icon.setImageResource(R.drawable.icon_oppened_category);
                        progressBar.setVisibility(View.GONE);
                        recycler_theme.setVisibility(View.VISIBLE);
                        recycler_theme.setLayoutManager(new GridLayoutManager(
                                context,
                                2
                        ));
                        recycler_theme.setAdapter(new RecyclerThemeAdapter(
                                context,
                                response.body(),
                                txt_selected_count,
                                selected_lists,
                                position
                        ));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Theme>> call, Throwable t) {

            }
        });
    }
}
