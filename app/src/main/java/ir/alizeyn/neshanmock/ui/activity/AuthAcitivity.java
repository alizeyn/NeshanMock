package ir.alizeyn.neshanmock.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.auth.AuthRes;
import ir.alizeyn.neshanmock.request.MockWebServices;
import ir.alizeyn.neshanmock.request.RequestFactory;
import ir.alizeyn.neshanmock.util.LoginHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthAcitivity extends AppCompatActivity {

    enum State {
        LOGIN, SIGN_UP
    }

    private TextView tvAuthTitle;
    private EditText etUsername;
    private EditText etPassword;
    private MaterialButton btnSignUp;
    private MaterialButton btnLogin;
    private MaterialButton btnGoToSignUp;
    private MaterialButton btnGoToLogin;

    private MockWebServices webServices;
    private State state = State.LOGIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webServices = RequestFactory.mockWebServices();
        initViews();
        listeners();
    }

    private void initViews() {
        setContentView(R.layout.activity_auth_acitivity);
        tvAuthTitle = findViewById(R.id.tvAuthTitle);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToSignUp = findViewById(R.id.btnGoToSignUp);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
    }

    private void listeners() {

        btnGoToSignUp.setOnClickListener(v -> {
            state = State.SIGN_UP;
            tvAuthTitle.setText("Sign Up");
            btnLogin.setVisibility(View.GONE);
            btnSignUp.setVisibility(View.VISIBLE);
            btnGoToSignUp.setVisibility(View.GONE);
            btnGoToLogin.setVisibility(View.VISIBLE);
        });

        btnGoToLogin.setOnClickListener(v -> {
            state = State.LOGIN;
            tvAuthTitle.setText("Login");
            btnLogin.setVisibility(View.VISIBLE);
            btnSignUp.setVisibility(View.GONE);
            btnGoToSignUp.setVisibility(View.VISIBLE);
            btnGoToLogin.setVisibility(View.GONE);
        });

        btnSignUp.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            Toast.makeText(this, "sign up", Toast.LENGTH_SHORT).show();
            Call<AuthRes> call = webServices.signUp(username, password);
            call.enqueue(new Callback<AuthRes>() {
                @Override
                public void onResponse(Call<AuthRes> call, Response<AuthRes> response) {
                    if (response.isSuccessful()) {
                        AuthRes token = response.body();
                        Log.i("alizeynres", "onResponse: " + response.code());
                        Log.i("alizeynres", "onResponse: " + response.body().getToken());
                        LoginHelper.signUp(AuthAcitivity.this, username, password, token.getToken());
                    }
                }

                @Override
                public void onFailure(Call<AuthRes> call, Throwable t) {
                    Log.i("alizeynres", "onFailure: " + t.getMessage());
                }
            });
        });

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
            Call<AuthRes> call = webServices.login(username, password);
            call.enqueue(new Callback<AuthRes>() {
                @Override
                public void onResponse(Call<AuthRes> call, Response<AuthRes> response) {
                    Log.i("alizeynres", "onResponse: " + response.code());
                    Log.i("alizeynres", "onResponse: " + response.body().getToken());
                    if (response.isSuccessful()) {
                        AuthRes authRes = response.body();
                        LoginHelper.signUp(AuthAcitivity.this, username, password, authRes.getToken());
                    }
                }

                @Override
                public void onFailure(Call<AuthRes> call, Throwable t) {
                    Log.i("alizeynres", "onFailure: " + t.getMessage());
                }
            });
        });
    }
}
