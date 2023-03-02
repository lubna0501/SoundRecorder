package com.example.soundrecorder;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

@SuppressLint("WrongConstant")
public class MainActivity extends AppCompatActivity {
    private Button play,stop,pause,record;
    private MediaRecorder myAudioRecorder;
    private  String outputfile;
    MediaPlayer mediaPlayer;

    String [] permission = {Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    int permissioncode = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectxml();
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(permission,permissioncode);
            }
        }

        outputfile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/recording.3gp";
        myAudioRecorder = new MediaRecorder();

        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(getpathdirectory());
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                    Toast.makeText(MainActivity.this, "Recording Started", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAudioRecorder.stop();
                myAudioRecorder.release();
                Toast.makeText(getApplicationContext(), "Recording Stoped", Toast.LENGTH_SHORT).show();
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer =new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(getpathdirectory());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(getApplicationContext(), "Playing started", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
                else {
                    mediaPlayer.start();
                }

            }
        });
    }

    private String getpathdirectory() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicdirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicdirectory,"Recordfile "+".mp3");
        return file.getPath();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!Utils.isPermissionGranted(this)){
            new AlertDialog.Builder(this)
                    .setTitle("All Files permission")
                    .setMessage("Due to android 11 the file permission required by the app at runtime")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            takepermission();
                        }
                    })
                    .setNegativeButton("Deny ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_dialer)
                    .show();

        }else {
            Toast.makeText(getApplicationContext(), "permisson already granted here", Toast.LENGTH_SHORT).show();
        }
    }

    private void takepermission() {



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                requestpermissionlanucher.launch(intent);
            }catch (Exception e){
                e.printStackTrace();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                requestpermissionlanucher.launch(intent);

            }
        }
        else {
            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
                if (ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
                    requestPermissions(permission,permissioncode);
                }
            }
        }
    }
    ActivityResultLauncher   requestpermissionlanucher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()== Activity.RESULT_OK){
                        Toast.makeText(getApplicationContext(), "permission granted ", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private void connectxml() {
        play = findViewById(R.id.playbtn);
        stop = findViewById(R.id.stopbtn);
        pause = findViewById(R.id.pausebtn);
        record= findViewById(R.id.recordbtn);
    }

}