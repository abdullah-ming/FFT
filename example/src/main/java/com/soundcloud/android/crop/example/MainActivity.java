package com.soundcloud.android.crop.example;

import com.soundcloud.android.crop.Crop;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Hashtable;

public class MainActivity extends Activity {

    private ImageView resultView;
    private EditText editText;
    private Bitmap bitmap=null;
    private Button button1;
    private Button button2;
    private ImageFFT trans;
    public FFT_2D fft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trans = new ImageFFT();
        if(bitmap==null)
        {
            resultView = (ImageView) findViewById(R.id.result_image);
        }

        button1 = (Button) findViewById(R.id.button_1);
        button2 = (Button) findViewById(R.id.button_2);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmap!=null)
                {
                    int[][] matrix = null;
                    try {
                        matrix = greyProcessing(bitmap);
                    }
                    catch (Exception e)
                    {

                    }
                    int Y = matrix.length;
                    int X = matrix[0].length;
                    Complex[][] x = new Complex[Y][X];
                    // original data
                    for (int i = 0; i < Y; i++)
                        for(int j=0;j<X;j++){
                            x[i][j] = new Complex(matrix[i][j], 0);
                            System.out.println("略略略"+x[i][j]);
                        }

                    long starTime=System.currentTimeMillis();
                    long Time=0;
                    // FFT of original data
                    Complex[][] y = fft.fft2d(x);

                    double[][] dfft = new double[Y][X];
                    Bitmap bi = Bitmap.createBitmap(X, Y, Bitmap.Config.RGB_565);
                    for (int i = 0; i < Y; i++)
                        for(int j=0;j<X;j++){
//                            System.out.println("略略略"+y[i][j]);
                            dfft[i][j] = y[i][j].abs();
//                            System.out.println(dfft[i][j]);
                            bi.setPixel(j,i,(int)dfft[i][j]);
                        }

                    Bitmap FFTImage = null;
                    Bitmap iFFTImage = null;
                    Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                    intent.putExtra("FFT_bitmap",bi);
                    view.setSaveEnabled(false);

                    startActivity(intent);


                }
                else {
                    Toast.makeText(MainActivity.this, "Please click the upper right corner to insert the picture.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                int[][] matrix = null;
                try {
                    matrix = greyProcessing(bitmap);
                }
                catch (Exception e)
                {

                }
                int Y = matrix.length;
                int X = matrix[0].length;
                Complex[][] x = new Complex[Y][X];
                // original data
                for (int i = 0; i < Y; i++)
                    for(int j=0;j<X;j++)
                        x[i][j] = new Complex(matrix[i][j], 0);
                long starTime=System.currentTimeMillis();
                long Time=0;
                // FFT of original data
//                Complex[][] y = fft.fft2d(x);

                Complex[][] z = fft.ifft2d(fft.fft2d(x));

                double[][] difft = new double[Y][X];
                Bitmap bi2 = Bitmap.createBitmap(X, Y, Bitmap.Config.RGB_565);
                for (int i = 0; i < Y; i++)
                    for(int j=0;j<X;j++){
                        difft[i][j] = z[i][j].abs();
                        bi2.setPixel(j,i,(int)difft[i][j]);
                    }

                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                intent.putExtra("iFFT_bitmap",bi2);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_select) {
            resultView.setImageDrawable(null);
            Crop.pickImage(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);

        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri=Crop.getOutput(result);
            if(uri != null){
                bitmap = getBitmapFormUri(uri);
            }
            resultView.setImageBitmap(bitmap);

        }else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private Bitmap getBitmapFormUri(Uri uri)
    {
        try
        {
            // 读取uri所在的图片
            return decodeUri(this,uri);
        }
        catch (Exception e)
        {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap decodeUri(Context context, Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //只读取图片尺寸
        resolveUri(context, uri, options);

        //计算实际缩放比例
        //inSampleSize
        //这个值是一个int，当它小于1的时候，将会被当做1处理，如果大于1，那么就会按照比例（1
                // inSampleSize）缩小bitmap的宽和高、

        //降低分辨率，大于1时这个值将会被处置为2的倍数。

       // 例如，width=100，height=100，inSampleSize=2，那么就会将bitmap处理为，width=50，height=50，宽高降为1
                // 2，像素数降为1 / 4。

        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;//读取图片内容
        options.inPreferredConfig = Bitmap.Config.RGB_565; //根据情况进行修改
        Bitmap bitmap = null;
        try {
            bitmap = resolveUriForBitmap(context, uri, options);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static void resolveUri(Context context, Uri uri, BitmapFactory.Options options) {
        if (uri == null) {
            return;
        }

        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) ||
                ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
                Log.w("resolveUri", "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.w("resolveUri", "Unable to close content: " + uri, e);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            Log.w("resolveUri", "Unable to close content: " + uri);
        } else {
            Log.w("resolveUri", "Unable to close content: " + uri);
        }
    }
    private static Bitmap resolveUriForBitmap(Context context, Uri uri, BitmapFactory.Options options) {
        if (uri == null) {
            return null;
        }

        Bitmap bitmap = null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme) ||
                ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(stream, null, options);
            } catch (Exception e) {
                Log.w("resolveUriForBitmap", "Unable to open content: " + uri, e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.w("resolveUriForBitmap", "Unable to close content: " + uri, e);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            Log.w("resolveUriForBitmap", "Unable to close content: " + uri);
        } else {
            Log.w("resolveUriForBitmap", "Unable to close content: " + uri);
        }

        return bitmap;
    }
    public int[][] greyProcessing(Bitmap bi)throws Exception {
        int[] rgb = new int[3];
//        BufferedImage bi2 = null;
        int width = bi.getWidth();
        int height = bi.getHeight();
        int minx = 0;
        int miny = 0;
//        int alpha = 0xFF << 24;
        int Gray[][] = new int[height][width];
//        int[] pixels = new int[bi.getWidth()*bi.getHeight()];//保存所有的像素的数组，图片宽×高
//        bi.getPixels(pixels,0,bi.getWidth(),0,0,bi.getWidth(),bi.getHeight());
        //System.out.println("width="+width+",height="+height+".");
        //System.out.println("minx="+minx+",miny="+miny+".");
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //得到每点的像素值
                int col = bi.getPixel(i, j);
                int alpha = col & 0xFF000000;
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                // 增加了图像的亮度
                red = (int) (1.1 * red + 30);
                green = (int) (1.1 * green + 30);
                blue = (int) (1.1 * blue + 30);
                //对图像像素越界进行处理
                if (red >= 255)
                {
                    red = 255;
                }

                if (green >= 255) {
                    green = 255;
                }

                if (blue >= 255) {
                    blue = 255;
                }
                // 新的ARGB
                int newColor = alpha | (red << 16) | (green << 8) | blue;
                Gray[i][j] = newColor&0xff;
                System.out.println(Gray[i][j]);
            }
        }
        return Gray;
    }


}
