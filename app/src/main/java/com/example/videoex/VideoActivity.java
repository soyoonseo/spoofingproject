package com.example.videoex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoActivity extends AppCompatActivity {
    private Uri videouri;
    private static final int REQUEST_CODE = 101;
    private StorageReference videoref;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//        videoref =storageRef.child("/videos" + "/userIntro.3gp"); // child() 메서드를 사용하여 트리에서 하위 위치를 가리키는 참조를 만든다.

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra("android.intent.extra.durationLimit",20);
        startActivityForResult(intent, REQUEST_CODE); //startActivityForResult 새로운 액티비티 호출

        // 파일명 : 현재 시간 + 회원전화번호
        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
        Date time = new Date();
        String time1 = format1.format(time);
        videoref =storageRef.child("/videos/" + time1 + filename);
    }

//    public void upload(View view) {
//        if (videouri != null) {
//            UploadTask uploadTask = videoref.putFile(videouri);
//
//            uploadTask.addOnFailureListener(new OnFailureListener () {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(VideoActivity.this,
//                            "Upload failed: " + e.getLocalizedMessage(),
//                            Toast.LENGTH_LONG).show();
//
//                }
//            }).addOnSuccessListener(
//                    new OnSuccessListener<UploadTask.TaskSnapshot> () {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Toast.makeText(VideoActivity.this, "Upload complete",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    }).addOnProgressListener (new OnProgressListener<UploadTask.TaskSnapshot> () {
//                @Override
//                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                    updateProgress(snapshot);
//                }
//            });        } else {
//            Toast.makeText(VideoActivity.this, "Nothing to upload",
//                    Toast.LENGTH_LONG).show();
//        }
//    }

    public void updateProgress(UploadTask.TaskSnapshot taskSnapshot) {

        @SuppressWarnings("VisibleForTests") long fileSize =
                taskSnapshot.getTotalByteCount();

        @SuppressWarnings("VisibleForTests")
        long uploadBytes = taskSnapshot.getBytesTransferred();

        long progress = (100 * uploadBytes) / fileSize;

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbar);
        progressBar.setProgress((int) progress);
    }

//    public void record(View view) {
//        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        startActivityForResult(intent, REQUEST_CODE); //startActivityForResult 새로운 액티비티 호출
//    }


    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);

        videouri = data.getData ();
        //회원별 파일 저장
//        String userUid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
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
                                    Toast.makeText(VideoActivity.this, "등록이 완료되었습니다.\n 승인을 기다려 주세요.",
                                            Toast.LENGTH_LONG).show();
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