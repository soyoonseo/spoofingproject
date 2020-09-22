package com.example.videoex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    private Uri videouri;
    private static final int REQUEST_CODE = 101;
    private StorageReference videoref;
    private String filename;
    private String _phone;


    private DatabaseReference mDatabase; // 네트워크 연결

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        Intent signUp_intent = getIntent();
        _phone = signUp_intent.getStringExtra("phone");
        //비디오 화면 띄워주
        startVideo();
        //이름 네이밍
        create_Video_Name(storageRef);



    }

    private void create_Video_Name(StorageReference storageRef ) {
        // 파일명 : 현재 시간 + 회원전화번호
        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
        Date time = new Date();
        String time1 = format1.format(time);
        videoref =storageRef.child("/videos/" + time1 + _phone);
    }

    private void startVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra("android.intent.extra.durationLimit",5);
        startActivityForResult(intent, REQUEST_CODE); //startActivityForResult 새로운 액티비티 호출
    }

    public void updateProgress(UploadTask.TaskSnapshot taskSnapshot) {

        @SuppressWarnings("VisibleForTests") long fileSize =
                taskSnapshot.getTotalByteCount();

        @SuppressWarnings("VisibleForTests")
        long uploadBytes = taskSnapshot.getBytesTransferred();

        long progress = (100 * uploadBytes) / fileSize;

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbar);
        progressBar.setProgress((int) progress);
    }


    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);

        videouri = data.getData ();
        Log.e(TAG,"videoUri:"+videouri);

        filename = videouri.getLastPathSegment();
        if (requestCode == REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                Toast.makeText (this, "Video saved to:\n" +
                        videouri, Toast.LENGTH_LONG).show ();
                // upload 메소드
                if (videouri != null) {
                    UploadTask uploadTask = videoref.putFile(videouri);

                    uploadTask.addOnFailureListener(new OnFailureListener () {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(VideoActivity.this,
                                    "Upload failed: " + e.getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();

                        }
                    }).addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot> () {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    Query query;
                                    Toast.makeText(VideoActivity.this, "등록이 완료되었습니다.\n 승인을 기다려 주세요.",
                                            Toast.LENGTH_LONG).show();
                                    final String _url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                    Log.e(TAG, "url : "+_url);


                                    //check the leaders and their scores, get the score of the current user
                                    final DatabaseReference leadersRef = FirebaseDatabase.getInstance().getReference("UserList");
                                    final Query query = leadersRef.orderByChild("phone").equalTo(_phone);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                System.out.println("************************여기 왔다");
                                                for (DataSnapshot child: snapshot.getChildren()) {

                                                    //get the key of the child node that has to be updated
                                                    String postkey = child.getRef().getKey();

                                                    //update score
                                                    String url = _url;
                                                    leadersRef.child(postkey).child("url").setValue(url);
                                                    Toast.makeText(VideoActivity.this,"url입력 성공",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                    // 비디오 정보 User database에 저장
                                    //final VideoUpload videoUpload = new VideoUpload(_url);
                                    //Log.e(TAG, "videoUpload: "+videoUpload);
                                    //
//                                    try {
//                                         query = FirebaseDatabase.getInstance().getReference().child("UserList").orderByChild("phone").equalTo(_phone);
//
//                                    }catch (Exception e) {
//                                        Toast.makeText(VideoActivity.this, e.getMessage(),
//                                                Toast.LENGTH_LONG).show();
//                                    }
//                                    query.addListenerForSingleValueEvent(new ValueEventListener(){
//                                        @Override
//                                        public void onDataChange(DataSnapshot datasnapshot){
////                                            mDatabase.child(_phone).setValue(videoUpload);
//
//                                            DatabaseReference hopperRef = mDatabase.child(_phone);
//                                            Map<String, Object> hopperUpdates = new HashMap<>();
//                                            hopperUpdates.put("url", videoUpload);
//                                            hopperRef.updateChildren(hopperUpdates);
//                                            //hopperRef.updateChildrenAsync(hopperUpdates);
//
//
//                                        }
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//                                        }
//                                    });



                                }
                            }).addOnProgressListener (new OnProgressListener<UploadTask.TaskSnapshot> () {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            updateProgress(snapshot);
                        }
                    });        } else {
                    Toast.makeText(VideoActivity.this, "Nothing to upload",
                            Toast.LENGTH_LONG).show();
                }


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText (this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show ();
            } else {
                Toast.makeText (this, "Failed to record video",
                        Toast.LENGTH_LONG).show ();
            }
        }
    }
}