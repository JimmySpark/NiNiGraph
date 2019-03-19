package ir.ninigraph.ninigraph.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.ninigraph.ninigraph.R;


public class SpinnerAdapter extends ArrayAdapter<String> {

    private static final int spinner = 0;
    private static final int spinner_dropdown = 1;
    private LayoutInflater inflater;

    public SpinnerAdapter(Context context, List<String> arrayList) {

        super(context, R.layout.layout_spinner, arrayList);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent, spinner);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent, spinner_dropdown);
    }

    public View getCustomView(int position, ViewGroup viewGroup, int type) {

        View view = null;
        if (type == spinner)
            view = inflater.inflate(R.layout.layout_spinner, viewGroup, false);
        else if (type == spinner_dropdown)
            view = inflater.inflate(R.layout.layout_spinner_dropdown, viewGroup, false);

        TextView txtItem = view.findViewById(R.id.txt_item);
        txtItem.setText(getItem(position));

        return view;
    }
}
