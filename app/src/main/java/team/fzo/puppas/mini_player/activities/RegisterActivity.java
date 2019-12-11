package team.fzo.puppas.mini_player.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;
import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.utils.HttpUtils;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUsername;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);

        mUsername = findViewById(R.id.et_register_username);
        mPassword = findViewById(R.id.et_register_pwd_input);
        findViewById(R.id.ib_navigation_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.d("aaa", "asd");
        switch (view.getId()) {
            case R.id.ib_navigation_back:
                finish();
                break;

            case R.id.bt_register_submit:

                String address = "192.168.137.1:8080/register";
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                HttpUtils.sendHttpRequestByPost(address, username, password,
                        new okhttp3.Callback(){
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                String responseData = Objects.requireNonNull(response.body()).string();
                            }

                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                            }
                        });
        }
    }
}