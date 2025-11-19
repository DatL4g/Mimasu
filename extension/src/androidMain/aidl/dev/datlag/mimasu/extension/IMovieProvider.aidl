package dev.datlag.mimasu.extension;

import dev.datlag.mimasu.extension.movie.MovieCallback;

interface IMovieProvider {
    const int VERSION = 1;

    void requestMovieId(in byte[] request, in MovieCallback callback);
}