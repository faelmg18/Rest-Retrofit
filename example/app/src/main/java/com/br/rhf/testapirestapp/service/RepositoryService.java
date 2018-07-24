package com.br.rhf.testapirestapp.service;


import com.br.rhf.restretrofit.communication.APIClientResponseListener;
import com.br.rhf.restretrofit.communication.RHFViewInterface;
import com.br.rhf.testapirestapp.communication.BaseApiCliente;
import com.br.rhf.testapirestapp.model.GitRepositories;

import retrofit2.Call;

public class RepositoryService extends BaseApiCliente<RepositoryInterface> {

    private static final String REPOSITORIES_LANGUAGE = "language:Java";
    private static final String REPOSITORIES_SORT = "stars";

    private static volatile RepositoryService instance;

    public RepositoryService(RHFViewInterface activity) {
        super(activity);
    }

    public static RepositoryService getInstance(RHFViewInterface rhfViewInterface) {
        instance = new RepositoryService(rhfViewInterface);
        return instance;
    }

    public void getRepositories(int page, final APIClientResponseListener<GitRepositories> listener) {

        Call<GitRepositories> call = getInterface()
                .doGetListRepositoriesModel(REPOSITORIES_LANGUAGE, REPOSITORIES_SORT, page);
        execute(call, listener);
    }
}
