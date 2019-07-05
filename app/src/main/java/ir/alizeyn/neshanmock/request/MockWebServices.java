package ir.alizeyn.neshanmock.request;

import java.util.List;

import ir.alizeyn.neshanmock.auth.AuthRes;
import ir.alizeyn.neshanmock.mock.MockShareModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MockWebServices {

    @POST("users")
    @FormUrlEncoded
    Call<AuthRes> signUp(@Field("username") String username, @Field("password") String password);

    @GET("users/token")
    Call<AuthRes> login(@Query("username") String username, @Query("password") String password);

    @POST("/users/self/mocks")
    Call<Void> saveMock(@Body MockShareModel mockShareModel);

    @GET("getAllMocks")
    Call<List<MockShareModel>> getMocks();

    @GET("getMock")
    Call<MockShareModel> getMock(long mockId);
}
