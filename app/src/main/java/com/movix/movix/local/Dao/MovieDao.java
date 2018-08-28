package com.movix.movix.local.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import com.movix.movix.local.MovieLocal;

import android.arch.paging.DataSource;

import io.reactivex.Single;

@Dao
public abstract class MovieDao {

    @Query("SELECT * FROM MovieLocal")
    public abstract DataSource.Factory<Integer, MovieLocal> getAllFavoriteMovies();

    @Insert(onConflict = OnConflictStrategy.FAIL)
    abstract long insert(MovieLocal movieLocal);

    @Delete
    public abstract void delete(MovieLocal... movieLocals);

    @Query("SELECT * FROM MovieLocal where id = :id")
    public abstract Single<MovieLocal> selectMovie(Integer id);

}