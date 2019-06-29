package ir.alizeyn.neshanmock.request;

import java.util.List;

import ir.alizeyn.neshanmock.mock.MockShareModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MockWebServices {

    @POST("signUp")
    Call<String> signUp(String username, String password);

    @GET("login")
    Call<String> login(String username, String password);

    @POST("saveMock")
    Call<Void> saveMock(@Body MockShareModel mockShareModel);

    @GET("getAllMocks")
    Call<List<MockShareModel>> getMocks();

    @GET("getMock")
    Call<MockShareModel> getMock(long mockId);
}
