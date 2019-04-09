package ir.alizeyn.neshanmock.request;



import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

/**
 * Created by alizeyn on 5/23/18.
 */

public interface WebServices {

    @GET("direction?")
    @Headers("Api-Key: service.lZqQdHs0pisfXSAoko3OzuYMRZfiwD0RCqIP8aQx")
    Call<RouteResponse> getRoute(@QueryMap Map<String, String> routingParams);
}
