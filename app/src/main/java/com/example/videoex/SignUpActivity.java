package com.example.videoex;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase; // 네트워크 연결
    User user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //초기화
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.signUpButton:
                    signUp();
                    break;
            }
        }
    };

    private void signUp() {
        final String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();
        final String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
        final String phone = ((EditText) findViewById(R.id.phoneEditText)).getText().toString();

        //User 클래스를 이용하여 빈 객체 만든다
        final User user = new User();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("UserList"); //userList 라는 키를 가진 값들을 참조한다.

        if (TextUtils.isEmpty(name)) {
            startToast("이름을 입력해주세요");
        } else if (TextUtils.isEmpty(email)) {
            startToast("e-mail을 입력해주세요");
        } else if (TextUtils.isEmpty(phone)) {
            startToast("휴대폰 번호를 입력해주세요");
        }

        //휴대폰 번호 중복체크
        if (phone.length() > 0){
            if(phone.length() < 10){
                startToast("핸드폰 번호를 확인해주세요.");
            }else {
                Query query = FirebaseDatabase.getInstance().getReference().child("UserList").orderByChild("phone").equalTo(phone);
                query.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot datasnapshot){
                        boolean phoneIsExist = datasnapshot.exists();
                        System.out.print(phoneIsExist);
                        if(phoneIsExist){
                            Toast.makeText(getApplicationContext(), "이미 가입된 휴대폰 번입니다\n다 확인해 주세요.",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (name.length() > 0 && email.length() > 0 && phone.length() > 0) {

                                user.setName(name);
                                user.setEmail(email);
                                user.setPhone(phone);
//                                user.setUrl();

                                mDatabase.child(phone).setValue(user);  // 유저 휴대시폰 번호으로 UserList 하위 경로 생성 정보 저장
                                startToast("등록이 완료되었습니다.");

                                startVideoActivity(phone);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }

//        // email 중복체크
//        if (email.length() > 0){
//            Query query = FirebaseDatabase.getInstance().getReference().child("UserList").orderByChild("email").equalTo(email);
//            query.addListenerForSingleValueEvent(new ValueEventListener(){
//                @Override
//                public void onDataChange(DataSnapshot datasnapshot){
//                    boolean eamilIsExist = datasnapshot.exists();
//                    System.out.print(eamilIsExist);
//                    if(eamilIsExist){
//                        Toast.makeText(getApplicationContext(), "이미 존재하는 이메일입니다\n이메일을 확인해 주세요.",Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        if (name.length() > 0 && email.length() > 0 && phone.length() > 0) {
//
//                            user.setName(name);
//                            user.setEmail(email);
//                            user.setPhone(phone);
//
//                            mDatabase.child(phone).setValue(user);  // 유저 휴대폰 번호으로 UserList 하위 경로 생성 정보 저장
//                            startToast("등록이 완료되었습니다.");
//
//                            startVideoActivity();
//                        }
//                    }
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                }
//            });
//        }

    }

//    private void updateUI(FirebaseUser user) {
//        String keyid = mDatabase.push().getKey();
//        mDatabase.child(keyid).setValue(user); //adding user info to database
//        Intent loginIntent = new Intent(this, notloginMainActivity.class);
//        startActivity(loginIntent);
//    }

    //리스너에서 토스트가 안되가지고 함수로 만들어줌
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void startVideoActivity(String phone) {
        //인텐트 객체 생성
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("phone",phone); //휴대폰 번호 넘길 것 "매개변수명", 데이터
        startActivity(intent);
    }


}
