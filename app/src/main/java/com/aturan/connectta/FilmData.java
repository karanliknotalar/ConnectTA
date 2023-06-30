package com.aturan.connectta;

public class FilmData {
    private String filmAd;
    private String filmResim;
    private String filmUrl;
    private String imdbPuani;

    public FilmData(String filmAd, String filmResim, String filmUrl, String imdbPuani) {
        this.filmAd = filmAd;
        this.filmResim = filmResim;
        this.filmUrl = filmUrl;
        this.imdbPuani = imdbPuani;
    }

    public String getFilmAd() {
        return filmAd;
    }

    public void setFilmAd(String filmAd) {
        this.filmAd = filmAd;
    }

    public String getFilmResim() {
        return filmResim;
    }

    public void setFilmResim(String filmResim) {
        this.filmResim = filmResim;
    }

    public String getFilmUrl() {
        return filmUrl;
    }

    public void setFilmUrl(String filmUrl) {
        this.filmUrl = filmUrl;
    }

    public String getImdbPuani() {
        return imdbPuani;
    }

    public void setImdbPuani(String imdbPuani) {
        this.imdbPuani = imdbPuani;
    }
}
