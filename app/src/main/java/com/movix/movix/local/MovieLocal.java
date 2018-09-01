package com.movix.movix.local;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.movix.movix.Entities.Movie;

@Entity
public class MovieLocal {
    @PrimaryKey @NonNull
    public Integer id;
    public String title;
    public String poster_path;
    public boolean video;

    public MovieLocal() {
    }
    public MovieLocal(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.poster_path = movie.getPoster_path();
        this.video = movie.getVideo();
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public Movie parse() {
        Movie movie = new Movie();
        movie.setId(this.getId());
        movie.setTitle(this.getTitle());
        movie.setPoster_path(this.getPoster_path());
        movie.setVideo(this.isVideo());
        return  movie;
    }
}
