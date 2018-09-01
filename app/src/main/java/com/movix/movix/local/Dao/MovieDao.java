package com.movix.movix.local.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import com.movix.movix.local.MovieLocal;

import android.arch.paging.DataSource;

import java.util.List;

import io.reactivex.Single;

@Dao
public abstract class MovieDao {

    public void insertMovie(MovieLocal movieLocal) {
        insert(movieLocal);
    }
    @Query("SELECT * FROM MovieLocal")
    public abstract LiveData<List<MovieLocal>> getAllFavoriteMovies();

    @Insert(onConflict = OnConflictStrategy.FAIL)
    abstract long insert(MovieLocal movieLocal);

    @Query("DELETE FROM MovieLocal where id = :id")
    public abstract void remove(Integer id);

    @Query("SELECT * FROM MovieLocal where id = :id")
    public abstract MovieLocal selectMovie(Integer id);

}