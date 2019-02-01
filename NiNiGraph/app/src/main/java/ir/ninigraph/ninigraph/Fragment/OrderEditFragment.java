package ir.ninigraph.ninigraph.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import ir.ninigraph.ninigraph.Activity.FollowOrderActivity;
import ir.ninigraph.ninigraph.Adapter.RecyclerOrderEditAdapter;
import ir.ninigraph.ninigraph.Model.OrderEdit;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Server.ApiClient;
import ir.ninigraph.ninigraph.Server.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderEditFragment extends Fragment {

    public OrderEditFragment() {

    }

    //Values
    SharedPreferences preferences;
    RecyclerView recycler_order_edit;
    int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_edit, container, false);

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        id = preferences.getInt("id", 0);

        //Views
        recycler_order_edit = view.findViewById(R.id.recycler_order_edit);


        getOrderEdit();
        return view;
    }

    //Classes
    private void getOrderEdit(){

        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<OrderEdit>> call = apiService.getOrderEdit(id);

        call.enqueue(new Callback<List<OrderEdit>>() {
            @Override
            public void onResponse(Call<List<OrderEdit>> call, Response<List<OrderEdit>> response) {
                if (response.isSuccessful() && response.body() != null){

                    recycler_order_edit.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recycler_order_edit.setAdapter(new RecyclerOrderEditAdapter(getActivity(), response.body()));
                }
            }

            @Override
            public void onFailure(Call<List<OrderEdit>> call, Throwable t) {

                Log.i("ERrrror", t.getMessage());
                Toast.makeText(getActivity(), "خطا در ارسال درخواست برای دریافت اطلاعات، لطفا مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
