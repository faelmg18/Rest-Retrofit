package com.br.rhf.restretrofit.communication;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.br.rhf.restretrofit.communication.dataserializer.DataSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//
// Created by rafael.alves on 16/07/2018.
//
// @param <T> = Interface
//            <p>
//            AbstractAPIClient é uma api de para auxiliar no uso da apit retrofit2,okhttp3
//            <p>
//            Por exemplo, vamos fazer uma chamada API do git
//            <pre><code>
//
//            Crie a interface de comunicação
//
//            public interface RepositoryInterface {
//            @GET("search/repositories")
//            Call<GitRepositories> doGetListRepositoriesModel(@Query("q") String language,
//            @Query("sort") String sort,
//            @Query("page") int page);
//            }
//
//            Crie uma classe que irá herdar de AbstractAPIClient, o parâmentro T deverá ser a interface criada para fazer a comunicação com a lib da Retrofit
//
//            public class BaseApiCliente<T> extends AbstractAPIClient<T> {
//
//            private ProgressDialog dialog;
//
//            public BaseApiCliente(RHFViewInterface rhfViewInterface) {
//            super(rhfViewInterface);
//            }
//
//            @Override
//            protected String getBaseUrl() {
//            return "https://api.github.com/";
//            }
//
//            @Override
//            protected void onStart() throws Exception {
//            dialog = new ProgressDialog(getActivity());
//            dialog.setMessage("Aguarde...");
//            dialog.show();
//            }
//
//            @Override
//            protected void onEnd() throws Exception {
//            dialog.dismiss();
//            }
//            }
//
//            Crie sua classe de serviços que ira herdar de BaseApiClient como no exemplo! OBS: essa classe "BaseApiCliente" poderá ser uma classe do seu gosto;
//
//            public class RepositoryService extends BaseApiCliente<RepositoryInterface> {
//
//            private static final String REPOSITORIES_LANGUAGE = "language:Java";
//            private static final String REPOSITORIES_SORT = "stars";
//
//            private static volatile RepositoryService instance;
//
//            public RepositoryService(RHFViewInterface activity) {
//            super(activity);
//            }
//
//            public static RepositoryService getInstance(RHFViewInterface rhfViewInterface) {
//            instance = new RepositoryService(rhfViewInterface);
//            return instance;
//            }
//
//            public void getRepositories(int page, final APIClientResponseListener<GitRepositories> listener) {
//
//            Call<GitRepositories> call = getmInterface()
//            .doGetListRepositoriesModel(REPOSITORIES_LANGUAGE, REPOSITORIES_SORT, page);
//            execute(call, listener);
//            }
//            }
//
//            Em sua activity crie uma instancia que faça a chamada da APi
//
//            public void getRepositoriesGit(){
//            RepositoryService.getInstance(this).getRepositories(0, new APIClientResponseListener<GitRepositories>() {
//            @Override
//            public void onSuccess(GitRepositories s) {
//            GitAdapter adapter = new GitAdapter(s.getItems(), MainActivity.this);
//            listView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onError(Call<GitRepositories> call, Throwable throwable) {
//
//            }
//            });
//            }
//                       </code></pre>
// @author Rafael Henrique Fernandes (faelmg18@gmail.com)
//

public abstract class AbstractAPIClient<T> {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static volatile Retrofit retrofit = null;

    protected abstract void onStart() throws Exception;

    protected abstract void onEnd() throws Exception;

    protected abstract String getBaseUrl();

    private T mInterface;

    private WeakReference<RHFViewInterface> viewInterfaceWeakReference;
    private Gson gson;

    public AbstractAPIClient(RHFViewInterface activity) {
        this.viewInterfaceWeakReference = new WeakReference<>(activity);
        initClient();
    }

    private void initClient() {

        HttpLoggingInterceptor interceptor = getHttpLoggingInterceptor();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        setTimeout(builder);
        builder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = getHeaders(original);
                return chain.proceed(request);
            }
        });

        OkHttpClient client = builder.build();
        gson = getGson();
        retrofit = builder(client, getConvertFactory());

        Type type = getClass().getGenericSuperclass();
        Type t = ((ParameterizedType) type).getActualTypeArguments()[0];
        createInterface((Class<T>) t);
    }

    @NonNull
    private Retrofit builder(OkHttpClient client, Converter.Factory factory) {
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(factory)
                .client(client)
                .build();
    }

    protected Converter.Factory getConvertFactory() {
        return getGsonConverterFactory(gson);
    }

    @NonNull
    private Converter.Factory getGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @NonNull
    private Gson getGson() {
        return new GsonBuilder().setDateFormat(getPattern()).create();
    }

    @NonNull
    private String getPattern() {
        return DEFAULT_DATE_FORMAT;
    }

    private void createInterface(Class<T> tClass) {
        mInterface = retrofit.create(tClass);
    }

    public T getInterface() {
        return mInterface;
    }

    protected void setTimeout(OkHttpClient.Builder builder) {
        builder.connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
    }

    @NonNull
    protected HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        interceptor.setLevel
                (HttpLoggingInterceptor.Level.BODY);
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return interceptor;
    }

    protected Request getHeaders(Request original) {
        return original.newBuilder()
                .header("Content-type", "application/json")
                .method(original.method(), original.body())
                .build();
    }

    protected <T> void execute(final Call<T> call, final APIClientResponseListener listener) {
        startProcess();
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(final Call<T> call, final Response<T> response) {
                Log.d("AbstractAPIClient", "AbstractAPIClient Success => code = " + response.code() + "");
                if (viewInterfaceWeakReference != null && viewInterfaceWeakReference.get() != null) {
                    viewInterfaceWeakReference.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (response.code() != 200) {
                                listener.onError(call, new Exception("Internal server error " + call.request().body()));
                                return;
                            }

                            listener.onSuccess(response.body());
                        }
                    });
                    finishProcess();
                }
            }

            @Override
            public void onFailure(final Call<T> call, final Throwable t) {
                call.cancel();
                Log.d("AbstractAPIClient", "AbstractAPIClient Failure, motive is = " + t.getMessage());
                if (viewInterfaceWeakReference != null) {
                    viewInterfaceWeakReference.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(call, t);
                        }
                    });
                }
                finishProcess();
            }
        });
    }

    private void startProcess() {
        try {
            onStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void finishProcess() {
        try {
            onEnd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void createBody(String json, BodyCreatedListener callListener, MediaType mediaType) {
        RequestBody body = RequestBody.create(mediaType, json);
        if (callListener != null) {
            callListener.onBodyCreated(body);
        }
    }

    protected <T> void createBody(T obj, BodyCreatedListener callListener) {
        String json = DataSerializer.getInstance().toJson(obj);
        createBody(json, callListener, MediaType.parse("application/json; charset=utf-8"));
    }

    protected <T> void createBody(T obj, BodyCreatedListener callListener, MediaType mediaType) {
        String json = DataSerializer.getInstance().toJson(obj);
        createBody(json, callListener, mediaType);
    }

    protected Activity getActivity() {
        return (viewInterfaceWeakReference != null && viewInterfaceWeakReference.get() != null) ? (Activity) viewInterfaceWeakReference.get() : null;
    }

    protected static Retrofit getRetrofit() {
        return retrofit;
    }

    public abstract class BodyCreatedListener {
        protected void onBodyCreated(RequestBody body) {
        }
    }
}
