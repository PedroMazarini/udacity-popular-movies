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
    public String original_title;
    public String title;

    public MovieLocal() {
    }
    public MovieLocal(Movie movie) {
        this.id = movie.getId();
        this.original_title = movie.getOriginal_title();
        this.title = movie.getTitle();
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
