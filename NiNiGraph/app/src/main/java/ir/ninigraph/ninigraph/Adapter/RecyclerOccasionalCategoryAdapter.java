package ir.ninigraph.ninigraph.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import ir.ninigraph.ninigraph.Activity.MainMenuActivity;
import ir.ninigraph.ninigraph.Model.Ads;
import ir.ninigraph.ninigraph.Model.OccasionalCategory;
import ir.ninigraph.ninigraph.Model.Picture;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Server.ApiClient;
import ir.ninigraph.ninigraph.Server.ApiService;
import ir.ninigraph.ninigraph.Util.GlideApp;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecyclerOccasionalCategoryAdapter extends RecyclerView.Adapter<RecyclerOccasionalCategoryAdapter.RecyclerViewHolder>{

    Context context;
    List<OccasionalCategory> occasionalCategoryList;

    public RecyclerOccasionalCategoryAdapter(Context context, List<OccasionalCategory> occasionalCategoryList) {
        this.context = context;
        this.occasionalCategoryList = occasionalCategoryList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_occasional_category, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {

        final OccasionalCategory occasionalCategory = occasionalCategoryList.get(position);

        GlideApp
                .with(context)
                .load(occasionalCategory.getUrl())
                .placeholder(R.drawable.img_logo2)
                .skipMemoryCache(true)
                .into(holder.img_cover);

        holder.txt_title.setText(occasionalCategory.getTitle());

        holder.card_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseCategory(
                        occasionalCategory.getId(),
                        occasionalCategory.getTitle(),
                        holder.progressBar,
                        holder.txt_title
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return occasionalCategoryList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        CardView card_category;
        ImageView img_cover;
        TextView txt_title;
        ProgressBar progressBar;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            card_category = itemView.findViewById(R.id.card_category);
            img_cover = itemView.findViewById(R.id.img_cover);
            txt_title = itemView.findViewById(R.id.txt_title);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    //Classes
    private void chooseCategory(int id, final String title, final ProgressBar progressBar, final TextView txt_title){

        progressBar.setVisibility(View.VISIBLE);
        txt_title.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<Picture>> call = apiService.getOccasional(id);

        call.enqueue(new Callback<List<Picture>>() {
            @Override
            public void onResponse(Call<List<Picture>> call, Response<List<Picture>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){

                        progressBar.setVisibility(View.GONE);
                        txt_title.setVisibility(View.VISIBLE);

                        MainMenuActivity.recycler_occasional.setLayoutManager(new LinearLayoutManager(
                                context,
                                LinearLayoutManager.HORIZONTAL,
                                false
                        ));
                        MainMenuActivity.recycler_occasional.setAdapter(new RecyclerOccasionalAdapter(
                                context,
                                response.body()
                        ));

                        MainMenuActivity.recycler_occasional_category.setVisibility(View.GONE);
                        MainMenuActivity.recycler_occasional.setVisibility(View.VISIBLE);
                        MainMenuActivity.txt_occasional_title.setText(
                                MainMenuActivity.txt_occasional_title.getText() + " > " + title
                        );
                        MainMenuActivity.img_back_category.setVisibility(View.VISIBLE);

                        MainMenuActivity.img_back_category.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                MainMenuActivity.recycler_occasional_category.setVisibility(View.VISIBLE);
                                MainMenuActivity.recycler_occasional.setVisibility(View.GONE);
                                MainMenuActivity.txt_occasional_title.setText("مناسبتی ها");
                                MainMenuActivity.img_back_category.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Picture>> call, Throwable t) {

            }
        });
    }
}
