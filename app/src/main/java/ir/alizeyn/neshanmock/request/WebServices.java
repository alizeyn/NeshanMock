package ir.alizeyn.neshanmock.request;



import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by alizeyn on 5/23/18.
 */

public interface WebServices {

    @GET
    @Headers("Api-Key: service.lZqQdHs0pisfXSAoko3OzuYMRZfiwD0RCqIP8aQx")
    Call<Route> getRoute(@QueryMap Map<String, String> routingParams);
}
