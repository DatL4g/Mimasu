package dev.datlag.mimasu.extension;

import dev.datlag.mimasu.extension.movie.Callback;
import dev.datlag.mimasu.extension.movie.Request;

interface IMovieInfoProvider {
    void requestInfo(in Request request, in Callback callback);
}