package ir.alizeyn.neshanmock.request;



import java.util.concurrent.TimeUnit;

import ir.alizeyn.neshanmock.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Mostafa J on 12/14/17.
 */

public class RequestFactory {


    private static Retrofit playerRetrofit = null;


//    private static HttpLoggingInterceptor loggingInterceptor;
//
//    static {
//        loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(Level.BODY);
//    }



    final private static OkHttpClient noneAuthorisedClient = new OkHttpClient().newBuilder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .build();



    public static WebServices webServices() {
        if (playerRetrofit == null) {
            playerRetrofit = new Retrofit.Builder()
                    .client(noneAuthorisedClient)
                    .baseUrl(BuildConfig.ROUTING_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return playerRetrofit.create(WebServices.class);
    }

    public static boolean validResponse(Response<?> response) {
        return response != null && response.code() / 100 == 2;
    }
}
