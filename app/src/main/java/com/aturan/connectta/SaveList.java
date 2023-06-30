package com.aturan.connectta;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class SaveList {
    private final StringBuilder sBuilder = new StringBuilder();

    private final Context context;
    private final ArrayList<FilmData> filmDataList;
    private final String fileName;
    private final String tag;
    private final Uri uri;

    public SaveList(Context context, ArrayList<FilmData> filmDataList, String fileName, String tag, Uri uri) {
        this.context = context;
        this.filmDataList = filmDataList;
        this.fileName = fileName;
        this.tag = tag;
        this.uri = uri;
    }

    public void htmlHazirla() {
        sBuilder.append("<html lang=\"tr\">");
        sBuilder.append("<head>");
        sBuilder.append("<style>");
        sBuilder.append(".div {}");
        sBuilder.append(".table { background-color: #fff;border: #ae0072 solid 2px; width: inherit;}");
        sBuilder.append(".td {border: 20px solid #a4066d;padding-left: 15px;}");
        sBuilder.append(".td2 {border: 10px solid #000;}");
        sBuilder.append(".tr {}");
        sBuilder.append(".img {width: 65px;}");
        sBuilder.append(".title {font: bold 15px verdana; color: deeppink;}");
        sBuilder.append(".puantext {color: #3f3f3f;font: bold 11px verdana;}");
        sBuilder.append(" .tbody{float: left;}");
        sBuilder.append("</style>");
        sBuilder.append("<title>ConnectTA</title>");
        sBuilder.append("</head>");
        sBuilder.append("<body>");
        sBuilder.append(tag);
        sBuilder.append("<hr>");
        sBuilder.append("<div class=\"divv\">");
        sBuilder.append("<table class=\"table\">");
        sBuilder.append("<tbody class=\"tbody\">");

        for (int i = 0; i < filmDataList.size(); i++) {

            FilmData filmData = filmDataList.get(i);

            sBuilder.append("<tr class=\"tr\">");
            sBuilder.append("<td class=\"td2\">");
            sBuilder.append("<a href=\"").append(filmData.getFilmUrl()).append("\" title=\"").append(filmData.getFilmAd()).append("\">");
            sBuilder.append("<img src=\"").append(filmData.getFilmResim()).append("\" class=\"img\">");
            sBuilder.append("</a></td>");
            sBuilder.append("<td class=\"td\">");
            sBuilder.append("<a href=\"").append(filmData.getFilmUrl()).append("\" title=\"").append(filmData.getFilmAd()).append("\">");
            sBuilder.append("<span class=\"title\">").append(filmData.getFilmAd()).append("</span></a><br>");
            sBuilder.append("<span class=\"puantext\">IMDB: ").append(filmData.getImdbPuani()).append("</span>");
            sBuilder.append("</td></tr>");
        }
        sBuilder.append("</tbody>");
        sBuilder.append("</table>");
        sBuilder.append("</div>");
        sBuilder.append("</body>");
        sBuilder.append("</html>");

        kaydet();
    }



    static final byte[] UTF8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private void kaydet() {

        try {
            DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri);
            assert documentFile != null;
            DocumentFile newFile = documentFile.createFile("txt", fileName);
            assert newFile != null;
            Uri fileUri = newFile.getUri();
            OutputStream outputStream = context.getContentResolver().openOutputStream(fileUri);
            outputStream.write(UTF8_BOM);
            outputStream.write(sBuilder.toString().getBytes(Charset.defaultCharset()));
            outputStream.close();
            Toast.makeText(context, "Kaydedildi.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Kaydetme başarısız", Toast.LENGTH_SHORT).show();
            System.out.println("HATA" + e.getMessage());
        }
    }
}
