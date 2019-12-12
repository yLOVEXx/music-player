package team.fzo.puppas.mini_player.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;
import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.utils.HttpUtils;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher {

    private EditText mUsername;
    private EditText mPassword1;
    private EditText mPassword2;
    private Button mBtRegisterSubmit;
    private ImageView mIvRegisterUsernameDel;
    private ImageView mIvRegisterPwd1Del;
    private ImageView mIvRegisterPwd2Del;
    private LinearLayout mLlRegisterUsername;
    private LinearLayout mLlRegisterPwd1;
    private LinearLayout mLlRegisterPwd2;
    private String mInputUserName;
    private String mInputPwdFirst;
    private String mInputPwdSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);

        //text
        mPassword1 = findViewById(R.id.et_register_pwd_input);
        mPassword2 = findViewById(R.id.et_register_pwd_input2);
        mUsername = findViewById(R.id.et_register_username);
        //button
        mBtRegisterSubmit = findViewById(R.id.bt_register_submit);
        //delete
        mIvRegisterUsernameDel = findViewById(R.id.iv_register_username_del);
        mIvRegisterPwd1Del = findViewById(R.id.iv_register_pwd_del);
        mIvRegisterPwd2Del = findViewById(R.id.iv_register_pwd_del2);

        //LinerLayout
        mLlRegisterUsername = findViewById(R.id.ll_register_two_username);
        mLlRegisterPwd1 = findViewById(R.id.ll_register_two_pwd);
        mLlRegisterPwd2 = findViewById(R.id.ll_register_two_pwd2);

        findViewById(R.id.ib_navigation_back).setOnClickListener(this);

        //注册点击事件
        mPassword1.setOnClickListener(this);
        mPassword2.setOnClickListener(this);
        mUsername.setOnClickListener(this);
        mBtRegisterSubmit.setOnClickListener(this);
        mIvRegisterUsernameDel.setOnClickListener(this);
        mIvRegisterPwd1Del.setOnClickListener(this);
        mIvRegisterPwd2Del.setOnClickListener(this);
        //注册其他时间
        mUsername.setOnFocusChangeListener(this);
        mUsername.addTextChangedListener(this);
        mPassword1.setOnFocusChangeListener(this);
        mPassword1.addTextChangedListener(this);
        mPassword2.setOnFocusChangeListener(this);
        mPassword2.addTextChangedListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s){
        mInputUserName = mUsername.getText().toString().trim();
        mInputPwdFirst = mPassword1.getText().toString().trim();
        mInputPwdSecond = mPassword2.getText().toString().trim();


        //是否显示清除按钮
        if (mInputUserName.length() > 0) {
            mIvRegisterUsernameDel.setVisibility(View.VISIBLE);
        } else {
            mIvRegisterUsernameDel.setVisibility(View.INVISIBLE);
        }
        if (mInputPwdFirst.length() > 0) {
            mIvRegisterPwd1Del.setVisibility(View.VISIBLE);
        } else {
            mIvRegisterPwd1Del.setVisibility(View.INVISIBLE);
        }
        if (mInputPwdSecond.length() > 0) {
            mIvRegisterPwd2Del.setVisibility(View.VISIBLE);
        } else {
            mIvRegisterPwd2Del.setVisibility(View.INVISIBLE);
        }



        //登录按钮是否可用
        if (!TextUtils.isEmpty(mInputUserName) && !TextUtils.isEmpty(mInputPwdFirst) && !TextUtils.isEmpty(mInputPwdSecond)) {
            mBtRegisterSubmit.setBackgroundResource(R.drawable.bg_login_submit);
            mBtRegisterSubmit.setTextColor(getResources().getColor(R.color.white));
        } else {
            mBtRegisterSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            mBtRegisterSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
        }
    }

    //用户名密码焦点改变
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        if (id == R.id.et_register_username) {
            if (hasFocus) {
                mLlRegisterUsername.setActivated(true);
                mLlRegisterPwd1.setActivated(false);
                mLlRegisterPwd2.setActivated(false);
            }
        }
        else if(id == R.id.et_register_pwd_input){
            if (hasFocus) {
                mLlRegisterPwd1.setActivated(true);
                mLlRegisterUsername.setActivated(false);
                mLlRegisterPwd2.setActivated(false);
            }
        }
        else {
            if (hasFocus) {
                mLlRegisterPwd2.setActivated(true);
                mLlRegisterUsername.setActivated(false);
                mLlRegisterPwd1.setActivated(false);
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_navigation_back:
                finish();
                MainActivity.mNavigationView.setCheckedItem(R.id.nav_1);
                break;

            case R.id.et_register_username:
                mPassword1.clearFocus();
                mPassword2.clearFocus();
                mUsername.setFocusableInTouchMode(true);
                mUsername.requestFocus();
                break;

            case R.id.et_register_pwd_input:
                mUsername.clearFocus();
                mPassword2.clearFocus();
                mPassword1.setFocusableInTouchMode(true);
                mPassword1.requestFocus();
                break;

            case R.id.et_register_pwd_input2:
                mUsername.clearFocus();
                mPassword1.clearFocus();
                mPassword2.setFocusableInTouchMode(true);
                mPassword2.requestFocus();
                break;


            case R.id.iv_register_username_del:
                //清空用户名
                mUsername.setText(null);
                break;

            case R.id.iv_register_pwd_del:
                //清空密码1
                mPassword1.setText(null);
                break;

            case R.id.iv_register_pwd_del2:
                //清空密码2
                mPassword2.setText(null);
                break;

            case R.id.bt_register_submit:
                if (mInputPwdFirst.length() < 6) {
                    Toast.makeText(this, "密码位数小于6位，请重修输入", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (mInputPwdFirst.length() > 16) {
                    Toast.makeText(this, "密码位数超出限制，请重新输入", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!mInputPwdFirst.equals(mInputPwdSecond)) {
                    Toast.makeText(this, "两次密码输入有误，请重修输入！", Toast.LENGTH_SHORT).show();
                    break;
                }

                String address = "http://120.55.170.121:8080/register";
                String username = mUsername.getText().toString();
                String password = mPassword1.getText().toString();
                HttpUtils.sendHttpRequestByPost(address, username, password,
                        new okhttp3.Callback(){
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                String responseData = Objects.requireNonNull(response.body()).string();
                                Log.d("aaa", responseData);
                            }

                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                            }
                        });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
            //此处写退向后台的处理
            MainActivity.mNavigationView.setCheckedItem(R.id.nav_1);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}