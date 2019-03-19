package ir.ninigraph.ninigraph.Fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ir.ninigraph.ninigraph.Adapter.RecyclerOrderDrawingAdapter;
import ir.ninigraph.ninigraph.Adapter.RecyclerOrderEditAdapter;
import ir.ninigraph.ninigraph.Model.OrderDrawing;
import ir.ninigraph.ninigraph.Model.OrderEdit;
import ir.ninigraph.ninigraph.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class OrderDrawingFragment extends Fragment {

    //Values
    SharedPreferences preferences;
    int id;
    private RecyclerView recyclerOrderDrawing;
    private TextView txtNoOrder;
    private ProgressBar progressBar;

    public OrderDrawingFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_drawing, container, false);
        initView(view);

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        id = preferences.getInt("id", 0);

        //Get Order From Server
        getOrderEdit();

        return view;
    }

    private void initView(View view) {
        recyclerOrderDrawing = view.findViewById(R.id.recycler_order_drawing);
        txtNoOrder = view.findViewById(R.id.txt_no_order);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void getOrderEdit() {

        JSONObject data = new JSONObject();

        try {
            data.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiService.getOrderDrawing(data.toString()).enqueue(new Callback<OrderDrawing>() {
            @Override
            public void onResponse(Call<OrderDrawing> call, Response<OrderDrawing> response) {
                if (response.isSuccessful()){
                    if (response.body().order != null){

                        progressBar.setVisibility(View.GONE);
                        recyclerOrderDrawing.setVisibility(View.VISIBLE);
                        recyclerOrderDrawing.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerOrderDrawing.setAdapter(new RecyclerOrderDrawingAdapter(getActivity(), response.body().order));
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        txtNoOrder.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderDrawing> call, Throwable t) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "خطا در دریافت اطلاعات، مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
