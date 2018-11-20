package com.soundcloud.android.crop.example;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SecondActivity extends Activity {

    private Bitmap bitmap=null;
    private Bitmap bitmap2=null;
    private ImageView resultView;

    private Button button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        button1 = (Button) findViewById(R.id.secondButton);
        resultView = (ImageView) findViewById(R.id.result2_image);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        bitmap = (Bitmap)extras.get("FFT_bitmap");
        if(bitmap!=null){
            resultView.setImageBitmap(bitmap);
            Toast.makeText(SecondActivity.this, "FFT succeed!.",
                    Toast.LENGTH_SHORT).show();
        }
        bitmap2 = (Bitmap)extras.get("iFFT_bitmap");
        if(bitmap2!=null){
            resultView.setImageBitmap(bitmap2);
            Toast.makeText(SecondActivity.this, "iFFT succeed!.",
                    Toast.LENGTH_SHORT).show();
        }
//        resultView.setImageBitmap(bitmap);



        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                SecondActivity.this.finish();
            }
        });
    }
}
