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

import java.util.List;

import ir.ninigraph.ninigraph.Adapter.RecyclerOrderEditAdapter;
import ir.ninigraph.ninigraph.Model.OrderEdit;
import ir.ninigraph.ninigraph.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class OrderEditFragment extends Fragment {

    public OrderEditFragment() {

    }

    //Values
    SharedPreferences preferences;
    RecyclerView recycler_order_edit;
    TextView txt_no_order;
    ProgressBar progress_bar;
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
        txt_no_order = view.findViewById(R.id.txt_no_order);
        progress_bar = view.findViewById(R.id.progress_bar);


        //Get Order From Server
        getOrderEdit();

        return view;
    }

    private void getOrderEdit() {

        apiService.getOrderEdit(id).enqueue(new Callback<List<OrderEdit>>() {
            @Override
            public void onResponse(Call<List<OrderEdit>> call, Response<List<OrderEdit>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        progress_bar.setVisibility(View.GONE);
                        recycler_order_edit.setVisibility(View.VISIBLE);
                        recycler_order_edit.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recycler_order_edit.setAdapter(new RecyclerOrderEditAdapter(getActivity(), response.body()));
                    } else {
                        progress_bar.setVisibility(View.GONE);
                        txt_no_order.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<OrderEdit>> call, Throwable t) {

                progress_bar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "خطا در ارسال درخواست برای دریافت اطلاعات، لطفا مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
