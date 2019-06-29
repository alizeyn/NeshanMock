package ir.alizeyn.neshanmock.request;


import java.util.concurrent.TimeUnit;

import ir.alizeyn.neshanmock.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mostafa J on 12/14/17.
 */

public class RequestFactory {


    private static Retrofit neshanApiRetrofit;
    private static Retrofit mockRetrofit;


    private static HttpLoggingInterceptor loggingInterceptor;

    static {
        loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }


    final private static OkHttpClient noneAuthorisedClient = new OkHttpClient().newBuilder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .build();

    final private static OkHttpClient client = new OkHttpClient().newBuilder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build();


    public static WebServices neshanWebServices() {
        if (neshanApiRetrofit == null) {
            neshanApiRetrofit = new Retrofit.Builder()
                    .client(noneAuthorisedClient)
                    .baseUrl(BuildConfig.ROUTING_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return neshanApiRetrofit.create(WebServices.class);
    }


    public static MockWebServices mockWebServices() {
        if (mockRetrofit == null) {
            mockRetrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(BuildConfig.MOCK_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return mockRetrofit.create(MockWebServices.class);
    }

    public static boolean validResponse(Response<?> response) {
        return response != null && response.code() / 100 == 2;
    }
}
