package com.example.videoex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //초기화
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.gotoLoginButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoSignUpButton).setOnClickListener(onClickListener);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//    }

    // 뒤로 가기가 필요없으니까 (여기가 메인이기 때문에) 만들어줌
    //***********우리 프로젝트에서는 메인에서 이 기능을 쓰면 좋을 것 같음
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(1); // 그냥 시스템 꺼버림
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.gotoSignUpButton:
                    startSignUpActivity();
                    finish();
                    break;
                case R.id.gotoLoginButton:
                    startOTPActivity();
                    finish();
                    break;
            }
        }
    };

    //리스너에서 토스트가 안되가지고 함수로 만들어줌
    private void startToast(String msg){
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }
    //리스너에서 intent 사용이 안되가지고 함수로 만들어줌
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 로그인 하고 메인 화면에서 뒤로가기 누르면 앱 종료
        startActivity(intent);
    }
    private void startSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
    private void startOTPActivity() {
        Intent intent = new Intent(this, SendOTPActivity.class);
        startActivity(intent);
        finish();
    }
}