package com.br.rhf.testapirestapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.br.rhf.restretrofit.communication.APIClientResponseListener;
import com.br.rhf.restretrofit.communication.RHFViewInterface;
import com.br.rhf.testapirestapp.model.GitRepositories;
import com.br.rhf.testapirestapp.service.RepositoryService;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements RHFViewInterface {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list_view);

        RepositoryService.getInstance(this).getRepositories(0, new APIClientResponseListener<GitRepositories>() {
            @Override
            public void onSuccess(GitRepositories s) {
                GitAdapter adapter = new GitAdapter(s.getItems(), MainActivity.this);
                listView.setAdapter(adapter);
            }

            @Override
            public void onError(Call<GitRepositories> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
