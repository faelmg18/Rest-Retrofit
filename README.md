# Rest-Retrofit
RestRetorif, é uma Lib de auxilio para o uso da Lib da retrofit.



## Retrofit
Retrofit é um Cliente HTTP seguro para Android e Java da Square, Inc.
[Retrofit](https://github.com/square/retrofit) - Retrofit.

## OkHttp

Um cliente HTTP e HTTP / 2 para aplicativos Android e Java
[OkHttp](https://github.com/square/okhttp) - OkHttp.

### Pré-requisitos
* Retrofit requires at minimum Java 7 or Android 2.3.

Intalação
--------
Maven
```xml
<dependency>
  <groupId>com.github.faelmg18</groupId>
  <artifactId>rhf-rest-retrofit</artifactId>
  <version>0.0.2</version>
  <type>aar</type>
</dependency>
```
ou Gradle:
```groovy
implementation 'com.github.faelmg18:rhf-rest-retrofit:0.0.2'
```

## Uso da Lib Rest-Retrofit

```
Crie a interface de comunicação
  
  public interface RepositoryInterface {
            @GET("search/repositories")
            Call<GitRepositories> doGetListRepositoriesModel(@Query("q") String language,
            @Query("sort") String sort,
            @Query("page") int page);
            }
}
```

## BaseApiCliente - Classe que herdará da nossa lib para fazer a ligação com a Lib Retrofit
Agora crie uma classe que irá herdar de AbstractAPIClient, o parâmentro T deverá ser a interface criada para fazer a comunicação com a lib da Retrofit

```
public class BaseApiCliente<T> extends AbstractAPIClient<T> {

            private ProgressDialog dialog;

            public BaseApiCliente(RHFViewInterface rhfViewInterface) {
            super(rhfViewInterface);
            }

            @Override
            protected String getBaseUrl() {
            return "https://api.github.com/";
            }

            @Override
            protected void onStart() throws Exception {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Aguarde...");
            dialog.show();
            }

            @Override
            protected void onEnd() throws Exception {
            dialog.dismiss();
            }

```
## Classe de serviços
 Crie sua classe de serviços que ira herdar de BaseApiClient como no exemplo! OBS: essa classe "BaseApiCliente" poderá ser uma classe do seu gosto
 
```
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


```

## Fazendo a chamada 
### Obs: Sua Activity devera implementar a interface RHFViewInterface, com ela conseguimos fazer a chamda dentro de uma Thread de UI
 Interface de chamda dos serviços para o uso da lib Retrofit
 Exemplos vamos fazer uma chamada API do git

Em sua activity crie uma instancia que faça a chamada da APi

```
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
            // No caso da API do Git ela nos devolve uma classe que contem uma lista dos respositórios mais usados.
            // Use um adaptador para mostrar o resultado ao usuário.
                GitAdapter adapter = new GitAdapter(s.getItems(), MainActivity.this);
                listView.setAdapter(adapter);
            }

            @Override
            public void onError(Call<GitRepositories> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
```


## Contribuições

* **Jackson Core databind** - [jackson-databind](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind/2.0.1)
* **Jackson Core annotations** - [jackson-annotations](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations/2.2.1)

## Autor

* **Rafael Henrique Fernandes** - [Rafael Fernandes](https://github.com/faelmg18)

## Licença

Este projeto é licenciado sob a licença MIT - consulte o arquivo [LICENSE.md](LICENSE.md) para obter detalhes


