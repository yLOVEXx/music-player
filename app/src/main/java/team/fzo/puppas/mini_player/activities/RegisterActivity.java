package team.fzo.puppas.mini_player.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.view.View;
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
    private Button mBtRegistSubmit;
    private Button mBtRegistSubmit2;
    private ImageView mIvRegistUsernameDel;
    private ImageView mIvRegistPwd1Del;
    private ImageView mIvRegistPwd2Del;
    private LinearLayout mLlRegistUsername;
    private LinearLayout mLlRegistPwd1;
    private LinearLayout mLlRegistPwd2;
    private String mSUserName;
    private String mSPwd_First;
    private String mSPwd_Second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);

        //text
        mPassword1 = findViewById(R.id.et_register_pwd_input);
        mPassword2 = findViewById(R.id.et_register_pwd_input2);
        mUsername = findViewById(R.id.et_register_username);
        //button
        mBtRegistSubmit = findViewById(R.id.bt_register_submit);
        //delete
        mIvRegistUsernameDel = findViewById(R.id.iv_register_username_del);
        mIvRegistPwd1Del = findViewById(R.id.iv_register_pwd_del);
        mIvRegistPwd2Del = findViewById(R.id.iv_register_pwd_del2);

        //LinerLayout
        mLlRegistUsername = findViewById(R.id.ll_register_two_username);
        mLlRegistPwd1 = findViewById(R.id.ll_register_two_pwd);
        mLlRegistPwd2 = findViewById(R.id.ll_register_two_pwd2);

        findViewById(R.id.ib_navigation_back).setOnClickListener(this);

        //注册点击事件
        mPassword1.setOnClickListener(this);
        mPassword2.setOnClickListener(this);
        mUsername.setOnClickListener(this);
        mBtRegistSubmit.setOnClickListener(this);
        mIvRegistUsernameDel.setOnClickListener(this);
        mIvRegistPwd1Del.setOnClickListener(this);
        mIvRegistPwd2Del.setOnClickListener(this);
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
        mSUserName = mUsername.getText().toString().trim();
        mSPwd_First = mPassword1.getText().toString().trim();
        mSPwd_Second = mPassword2.getText().toString().trim();


        //是否显示清除按钮
        if (mSUserName.length() > 0) {
            mIvRegistUsernameDel.setVisibility(View.VISIBLE);
        } else {
            mIvRegistUsernameDel.setVisibility(View.INVISIBLE);
        }
        if (mSPwd_First.length() > 0) {
            mIvRegistPwd1Del.setVisibility(View.VISIBLE);
        } else {
            mIvRegistPwd1Del.setVisibility(View.INVISIBLE);
        }
        if (mSPwd_Second.length() > 0) {
            mIvRegistPwd2Del.setVisibility(View.VISIBLE);
        } else {
            mIvRegistPwd2Del.setVisibility(View.INVISIBLE);
        }



        //登录按钮是否可用
        if (!TextUtils.isEmpty(mSUserName) && !TextUtils.isEmpty(mSPwd_First) && !TextUtils.isEmpty(mSPwd_Second)) {
            mBtRegistSubmit.setBackgroundResource(R.drawable.bg_login_submit);
            mBtRegistSubmit.setTextColor(getResources().getColor(R.color.white));
        } else {
            mBtRegistSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            mBtRegistSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
        }
    }

    //用户名密码焦点改变
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        if (id == R.id.et_register_username) {
            if (hasFocus) {
                mLlRegistUsername.setActivated(true);
                mLlRegistPwd1.setActivated(false);
                mLlRegistPwd2.setActivated(false);
            }
        }
        else if(id == R.id.et_register_pwd_input){
            if (hasFocus) {
                mLlRegistPwd1.setActivated(true);
                mLlRegistUsername.setActivated(false);
                mLlRegistPwd2.setActivated(false);
            }
        }
        else {
            if (hasFocus) {
                mLlRegistPwd2.setActivated(true);
                mLlRegistUsername.setActivated(false);
                mLlRegistPwd1.setActivated(false);
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
                if (mSPwd_First.length() < 6) {
                    Toast.makeText(this, "密码位数小于6位，请重修输入", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (mSPwd_First.length() > 16) {
                    Toast.makeText(this, "密码位数超出限制，请重新输入", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!mSPwd_First.equals(mSPwd_Second)) {
                    Toast.makeText(this, "两次密码输入有误，请重修输入！", Toast.LENGTH_SHORT).show();
                    break;
                }

                String address = "192.168.137.1:8080/register";
                String username = mUsername.getText().toString();
                String password = mPassword1.getText().toString();
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