package com.br.rhf.testapirestapp.service;


import com.br.rhf.testapirestapp.model.GitRepositories;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RepositoryInterface {
    @GET("search/repositories")
    Call<GitRepositories> doGetListRepositoriesModel(@Query("q") String language,
                                                     @Query("sort") String sort,
                                                     @Query("page") int page);
}
