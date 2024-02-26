package com.example.lab13;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.Objects;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;


public class MainActivity extends AppCompatActivity {
    LinearLayout viewContainer;
    public static Obj obj;
    Button chooseObjB;
    MyGLSurfaceView myGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // load default obj from the app's file system
        try {
            obj = ObjReader.read(getResources().openRawResource(R.raw.plane));
        } catch (IOException e) {throw new RuntimeException(e);}

        viewContainer = findViewById(R.id.ViewContainer);
        myGLSurfaceView = new MyGLSurfaceView(this);
        viewContainer.addView(myGLSurfaceView);

        chooseObjB = findViewById(R.id.chooseObjB);
        chooseObjB.setOnClickListener((v)->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setDataAndType(Uri.parse("/download"), "*/*");
            startActivityForResult(Intent.createChooser(intent, "choose file"),0);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            // load obj from the device's file system
            try {
                assert fileUri != null;
                obj = ObjReader.read(Objects.requireNonNull(getContentResolver().openInputStream(fileUri)));
                obj = ObjUtils.convertToRenderable(obj);

                myGLSurfaceView = new MyGLSurfaceView(this);
                viewContainer.removeAllViews();
                viewContainer.addView(myGLSurfaceView);

                Toast.makeText(this, "good", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}