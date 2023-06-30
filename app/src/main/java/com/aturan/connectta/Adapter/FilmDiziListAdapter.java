package com.aturan.connectta.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aturan.connectta.FilmData;
import com.aturan.connectta.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FilmDiziListAdapter extends RecyclerView.Adapter<FilmDiziListAdapter.viewHolder> {

    private Context context;
    private ArrayList<FilmData> filmData;

    public FilmDiziListAdapter(Context context, ArrayList<FilmData> filmData) {
        this.context = context;
        this.filmData = filmData;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.film_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        holder.adi.setText(filmData.get(position).getFilmAd());
        holder.imdb.setText(filmData.get(position).getImdbPuani());
        holder.url.setText(filmData.get(position).getFilmUrl());

        Picasso.get().load(filmData.get(position).getFilmResim())
                .resize((int) context.getResources().getDimension(R.dimen.resimWidth),
                        (int) context.getResources().getDimension(R.dimen.resimHeight))
                .into(holder.resim);
    }

    @Override
    public int getItemCount() {
        return filmData.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<FilmData> list) {
        filmData = list;
        notifyDataSetChanged();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        private ImageView resim, openUrl, shareUrl;
        private TextView adi, imdb, url;
        private FilmData film;

        public viewHolder(@NonNull View v) {
            super(v);
            resim = v.findViewById(R.id.film_list_item_resmi);
            adi = v.findViewById(R.id.film_list_item_adi);
            imdb = v.findViewById(R.id.film_list_item_imdb);
            url = v.findViewById(R.id.film_list_item_url);
            openUrl = v.findViewById(R.id.film_list_item_openUrl);
            shareUrl = v.findViewById(R.id.film_list_item_shareUrl);

            openUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() == -1)
                        return;
                    openBrowser(filmData.get(getAdapterPosition()).getFilmUrl());

                }
            });

            shareUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*if (getAdapterPosition() == -1)
                        return;
*/
                    film = filmData.get(getAdapterPosition());

                    ShareCompat.IntentBuilder builder = new ShareCompat.IntentBuilder(context);
                    builder.setType("text/plain")
                            .setChooserTitle("Şununla Paylaş")
                            .setText(film.getFilmAd()
                                    + "\n\n"
                                    + "Imdbn Puanı: " + film.getImdbPuani()
                                    + "\n\n"
                                    + film.getFilmUrl())
                            .startChooser();
                }
            });
        }

        private Dialog d;
        private WebView web;
        private WindowManager.LayoutParams params;
        private Window window;

        @SuppressLint("SetJavaScriptEnabled")
        private void openBrowser(String url) {
            d = new Dialog(context);
            d.setContentView(R.layout.open_browser_dialog);
            window = d.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            params = window.getAttributes();
            d.getWindow().setAttributes(params);

            web = d.findViewById(R.id.open_browser_webview);

            web.getSettings().setJavaScriptEnabled(true);
            web.loadUrl(url);
            web.setWebViewClient(new WebViewClient());

            d.show();

        }
    }
}
