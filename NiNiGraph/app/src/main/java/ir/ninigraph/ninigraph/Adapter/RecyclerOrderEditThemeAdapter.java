package ir.ninigraph.ninigraph.Adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import ir.ninigraph.ninigraph.Activity.ChooseAndUploadThemePicture;
import ir.ninigraph.ninigraph.Activity.FollowOrderActivity;
import ir.ninigraph.ninigraph.Model.OrderEditTheme;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.GlideApp;

import static android.content.Context.DOWNLOAD_SERVICE;

public class RecyclerOrderEditThemeAdapter extends RecyclerView.Adapter<RecyclerOrderEditThemeAdapter.RecyclerViewHolder> {

    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    List<OrderEditTheme.orderThemeModel> themeList;
    OrderEditTheme.orderThemeModel theme;
    AlertDialog dialog;

    public RecyclerOrderEditThemeAdapter(Context context, List<OrderEditTheme.orderThemeModel> themeList) {
        this.context = context;
        this.themeList = themeList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recycler_order_edit_theme, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, final int position) {

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final OrderEditTheme.orderThemeModel th = themeList.get(position);
        theme = th;

        //Set Data
        GlideApp.with(context).
                load(th.url).
                placeholder(R.drawable.img_logo2).
                skipMemoryCache(true).
                into(holder.img_theme);

        Toast.makeText(context, "image_status: "+theme.image_status, Toast.LENGTH_SHORT).show();
        switch (th.image_status) {

            case 0:
                holder.img_status.setImageResource(R.drawable.icon_warning);
                holder.card_theme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(context, "position: "+position, Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "id: "+th.getId(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(context, "برای ارسال عکس، لمس طولانی کنید", Toast.LENGTH_LONG).show();
                    }
                });
                holder.card_theme.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        Intent intent = new Intent(context, ChooseAndUploadThemePicture.class);
                        intent.putExtra("theme_id", th.getId());
                        context.startActivity(intent);

                        return true;
                    }
                });
                break;
            case 1:
                holder.img_status.setImageResource(R.drawable.icon_wait);
                holder.card_theme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(context, "در انتظار تایید", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 2:
                holder.img_status.setImageResource(R.drawable.icon_confirm);
                holder.card_theme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(context, "تایید شد", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 3:
                holder.img_status.setImageResource(R.drawable.icon_download2);
                holder.card_theme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(context, "برای دانلود عکس سفارش خود، لمس طولانی کنید", Toast.LENGTH_LONG).show();
                    }
                });
                holder.card_theme.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if (preferences.getBoolean("write_permission", false)) {

                            showLoadingDialog();
                            downloadImage(th.image);
                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("اجازه دسترسی به کارت حافظه داده نشده است، برای صدور مجوز به تنظیمات گوشی خود مراجعه کنید");
                            builder.setPositiveButton("باشه", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    context.startActivity(new Intent(context, FollowOrderActivity.class));
                                }
                            });
                            builder.create().show();
                        }
                        return true;
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return themeList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        CardView card_theme;
        ImageView img_theme, img_status;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            card_theme = itemView.findViewById(R.id.card_theme);
            img_theme = itemView.findViewById(R.id.img_theme);
            img_status = itemView.findViewById(R.id.img_status);
        }
    }

    private void downloadImage(String url) {

        Calendar calendar = Calendar.getInstance();
        String imgName = calendar.get(Calendar.YEAR) + "." + calendar.get(Calendar.MONTH) + "-"
                + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ".jpg";

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/NiNi Graph");
        file.mkdirs();

        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setDestinationInExternalPublicDir("/NiNi Graph/", imgName);

        //Download Starting
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadManager.enqueue(request));
        final Cursor cursor = downloadManager.query(query);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (cursor.moveToFirst()) {
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                    //Toast.makeText(context, String .valueOf(status), Toast.LENGTH_SHORT).show();
                    switch (status) {

                        case 1:

                            dialog.dismiss();
                            Toast.makeText(context, "عکس با موفقیت دانلود شد", Toast.LENGTH_SHORT).show();
                            break;

                        default:

                            dialog.dismiss();
                            Toast.makeText(context, "خطا در بارگیری عکس، مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        }, 2000);
    }

    private void showLoadingDialog() {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_dialog));
        dialog.show();

        //Change Layout
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        width = (int) ((width) * (0.6 / 3));
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
