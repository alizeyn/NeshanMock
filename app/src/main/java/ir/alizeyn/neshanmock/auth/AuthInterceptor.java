package ir.alizeyn.neshanmock.auth;

import android.content.Context;

import java.io.IOException;

import ir.alizeyn.neshanmock.core.BaseApplication;
import ir.alizeyn.neshanmock.util.LoginHelper;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(authorizeRequest(request));
        return response;
    }

    private Request authorizeRequest(Request request) {
        Context context = BaseApplication.getBaseApplicationContext();
        User user = LoginHelper.getUser(context);
        if (user != null) {
            String token = user.getToken();
            Headers headers = request.headers().newBuilder()
                    .add("Authorization", "Bearer " + token)
                    .build();
            return request.newBuilder().headers(headers).build();
        } else return request;
    }
}
