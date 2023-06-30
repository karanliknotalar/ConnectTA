package com.aturan.connectta;

import static android.os.Looper.getMainLooper;

import android.os.Handler;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectTA {
    private String kullaniciAdi, kullaniciSifre;
    private String siteUrl;
    private final Context context;
    private ProgressDialog pDialog;
    private Handler handler;

    public ConnectTA(String kullaniciAdi, String kullaniciSifre, String siteUrl, Context context) {
        this.kullaniciAdi = kullaniciAdi;
        this.kullaniciSifre = kullaniciSifre;
        this.siteUrl = siteUrl;
        this.context = context;
    }

    public ConnectTA(Context context) {
        this.context = context;
    }

    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:96.0) Gecko/20100101 Firefox/96.0";
    private String cookie;

    public String getCookie() {
        return cookie;
    }

    public Boolean loginTA() throws IOException {
        handler = new Handler(getMainLooper());
        handler.post(() -> {
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.setMessage("Giriş yapılıyor");
            pDialog.show();
        });

        try {
            String url = siteUrl + "/login.php";

            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .build();

            RequestBody requestBody = new FormBody.Builder()
                    .add("redirect", "/index.php")
                    .add("username", kullaniciAdi)
                    .add("password", kullaniciSifre)
                    .add("login", "Giriş")
                    .add("autologin", "on")
                    .build();

            Request request = new Request.Builder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/114.0")
                    .url(url)
                    .post(requestBody)
                    .build();


            Response response = okHttpClient.newCall(request).execute();

            if (cookieManager.getCookieStore().getCookies().size() > 0)
                cookie = TextUtils.join(";", cookieManager.getCookieStore().getCookies());

            if (response.isSuccessful() && cookie.contains("phpbb2mysql_")) {
                pDialogClose();
                return true;
            } else {
                pDialogClose();
                return false;
            }

        } catch (Exception e) {
            pDialogClose();
            System.out.println("Hata_loginTA: " + e.getMessage());
            return false;
        }
    }

    private final ArrayList<FilmData> filmDataList = new ArrayList<>();
    private final ArrayList<FilmData> diziDataList = new ArrayList<>();
    private boolean filmSayfaSonumu = false;
    private boolean diziSayfaSonumu = false;
    private boolean anError = false;
    private String totalSayi;
    private int artisMik;

    public String getTotalSayi() {
        return totalSayi;
    }

    public int getArtisMik() {
        return artisMik;
    }

    public ArrayList<FilmData> getFilmDataList() {
        return filmDataList;
    }

    public ArrayList<FilmData> getDiziDataList() {
        return diziDataList;
    }

    public boolean isDiziSayfaSonumu() {
        return diziSayfaSonumu;
    }

    public boolean isFilmSayfaSonumu() {
        return filmSayfaSonumu;
    }

    public boolean isAnError() {
        return anError;
    }

    public void getFilmListesi(int pagenumber, String cook) {

        try {

            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .build();

            Request request = new Request.Builder()
                    .header("User-Agent", userAgent)
                    .addHeader("Cookie", cook)
                    .url("https://turkcealtyazi.org/watch.php?is=list&p=" + pagenumber)
                    .get()
                    .build();

            Response response = okHttpClient.newCall(request).execute();

            String responseBody = response.body().string();

            totalSayi = Jsoup.parse(responseBody).body().select("td").text();
            if(totalSayi.contains("Listenizde") && totalSayi.contains("bulunuyor"))
                totalSayi = totalSayi.split(" ")[1];

            responseBody = responseBody.substring(responseBody.indexOf("film bulunuyor</td>"), responseBody.indexOf("<div align=\"center\" style=\"margin:10px\"><div class=\"pagin\">"));

            /*if (responseBody.contains("<ul> </ul> <div class=\"clear\"></div> </div>")) {
                filmSayfaSonumu = true;
                return;
            }*/

            List<String> filmResim = new ArrayList<>();
            for (Element link : Jsoup.parse(responseBody).select("img[src]"))
                if (link.toString().contains("film"))
                    filmResim.add("https://turkcealtyazi.org" + link.attr("src"));

            List<String> filmUrl = new ArrayList<>();
            List<String> filmAd = new ArrayList<>();
            for (Element link : Jsoup.parse(responseBody).select("a[href]"))
                if (link.toString().contains("mov") && !TextUtils.isEmpty(link.text())) {
                    filmUrl.add("https://turkcealtyazi.org" + link.attr("href"));
                    filmAd.add(link.text());
                }

            List<String> imdbPuan = new ArrayList<>();
            for (Element link : Jsoup.parse(responseBody).select("div[class]"))
                if (link.toString().contains("ncorner") && !TextUtils.isEmpty(link.text()))
                    imdbPuan.add(link.text());

            if (filmResim.size() == 0) {
                filmSayfaSonumu = true;
                return;
            }

            if (filmResim.size() == filmUrl.size() && filmResim.size() == imdbPuan.size()) {
                for (int i = 0; i < filmResim.size(); i++)
                    filmDataList.add(new FilmData(filmAd.get(i), filmResim.get(i), filmUrl.get(i), imdbPuan.get(i)));

            }

            artisMik = filmResim.size();
            anError = false;
        } catch (Exception e) {
            anError = true;
            e.printStackTrace();
            System.out.println("Hata_getFilmListesi: " + e.getMessage());
        }
    }

    public void getDiziListesi(int pagenumber, String cook) {

        try {
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .build();

            Request request = new Request.Builder()
                    .header("User-Agent", userAgent)
                    .addHeader("Cookie", cook)
                    .url("https://turkcealtyazi.org/watch.php?is=slist&p=" + pagenumber)
                    .get()
                    .build();


            Response response = okHttpClient.newCall(request).execute();

            String responseBody = response.body().string();

            totalSayi = Jsoup.parse(responseBody).body().select("td").text();
            if(totalSayi.contains("Listenizde") && totalSayi.contains("bulunuyor"))
                totalSayi = totalSayi.split(" ")[1];

            responseBody = responseBody.substring(responseBody.indexOf("dizi bulunuyor</td>"),
                    responseBody.indexOf("<div align=\"center\" style=\"margin:10px\"><div class=\"pagin\">"));

            /*if (responseBody.contains("<ul> </ul> <div class=\"clear\"></div> </div>")) {
                diziSayfaSonumu = true;
                return;
            }*/

            List<String> filmResim = new ArrayList<>();
            for (Element link : Jsoup.parse(responseBody).select("img[src]"))
                if (link.toString().contains("film"))
                    filmResim.add("https://turkcealtyazi.org" + link.attr("src"));

            List<String> filmUrl = new ArrayList<>();
            List<String> filmAd = new ArrayList<>();
            for (Element link : Jsoup.parse(responseBody).select("a[href]"))
                if (link.toString().contains("mov") && !TextUtils.isEmpty(link.text())) {
                    filmUrl.add("https://turkcealtyazi.org" + link.attr("href"));
                    filmAd.add(link.text());
                }

            List<String> imdbPuan = new ArrayList<>();
            for (Element link : Jsoup.parse(responseBody).select("div[class]"))
                if (link.toString().contains("ncorner") && !TextUtils.isEmpty(link.text()))
                    imdbPuan.add(link.text());

            if (filmResim.size() == 0) {
                diziSayfaSonumu = true;
                return;
            }

            if (filmResim.size() == filmUrl.size() && filmResim.size() == imdbPuan.size()) {
                for (int i = 0; i < filmResim.size(); i++)
                    diziDataList.add(new FilmData(filmAd.get(i), filmResim.get(i), filmUrl.get(i), imdbPuan.get(i)));

            }
            artisMik = filmResim.size();
            anError = false;
        } catch (Exception e) {
            anError = true;
            e.printStackTrace();
            System.out.println("Hata_getFilmListesi: " + e.getMessage());
        }
    }

    private void pDialogClose() {
        handler = new Handler(getMainLooper());
        handler.post(() -> {
            if (pDialog.isShowing())
                pDialog.dismiss();
        });
    }
}
