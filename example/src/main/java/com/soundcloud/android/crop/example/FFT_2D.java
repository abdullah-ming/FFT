package com.soundcloud.android.crop.example;

/**
 * Created by abudu on 2018/11/4.
 */

public class FFT_2D {
    public static Complex[][] fft2d(Complex[][] x) {
        int N =x.length;
        for (int i = 0; i < N; i++) {
            Complex[] temp = new Complex[N];
            for (int j = 0; j < N; j++)
                temp[j] = x[i][j];
            Complex[] reslutTemp=FFT.fft(temp);
            for(int j=0;j<N;j++)
                x[i][j]=reslutTemp[j];
        }


        for (int i = 0; i < N; i++) {
            Complex[] temp = new Complex[N];
            for (int j = 0; j < N; j++)
                temp[j] = x[j][i];
            Complex[] resultTemp=FFT.fft(temp);
            for(int j=0;j<N;j++)
                x[j][i]=resultTemp[j];
        }

        Complex[][] y = new Complex[N][N];
        for(int i=0;i<N;i++)
            for(int j=0;j<N;j++)
                y[i][j]=x[i][j];
        return y;
    }
    public static Complex[][] ifft2d(Complex[][] x) {
        int N=x.length;
        for (int i = 0; i < N; i++) {
            Complex[] temp = new Complex[N];
            for (int j = 0; j < N; j++)
                temp[j] = x[i][j].conjugate();
            Complex[] resultTemp=FFT.fft(temp);
            for(int j=0;j<N;j++)
                x[i][j]=resultTemp[j];
        }


        for (int i = 0; i < N; i++) {
            Complex[] temp = new Complex[N];
            for (int j = 0; j < N; j++)
                temp[j] = x[j][i];
            Complex[] resultTemp=FFT.fft(temp);
            for(int j=0;j<N;j++)
                x[j][i]=resultTemp[j].conjugate();
        }

        Complex[][] y = new Complex[N][N];
        for(int i=0;i<N;i++)
            for(int j=0;j<N;j++)
                y[i][j]=x[i][j].times(1.0/(N*N));
        return y;
    }

    public static void show(Complex[][] x, String title) {
        int N = x.length;
        System.out.println(title);
        System.out.println("-------------------");
        for (int i = 0; i < N; i++) {
            for(int j=0;j<N;j++)
                System.out.print(x[i][j]+"  ");
            System.out.println();
        }
        System.out.println();
    }

    public static int compare(Complex[][] x,Complex[][] y){
        int N=x.length;
        for (int i = 0; i < N; i++) {
            for(int j=0;j<N;j++){
                if(x[i][j].re()==y[i][j].re()&&x[i][j].im()==y[i][j].im())
                    continue;
                else{
                    System.out.println("两数组不相等 "+i+" "+j);
                    return 0;
                }
            }

        }
        System.out.println("两数组相等");
        return 1;
    }


    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        Complex[][] x = new Complex[N][N];

        // original data
        for (int i = 0; i < N; i++)
            for(int j=0;j<N;j++)
                x[i][j] = new Complex(i, 0);
        show(x, "x");

        long starTime=System.currentTimeMillis();
        long Time=0;
        // FFT of original data
        Complex[][] y = fft2d(x);
        show(y, "y = fft(x)");

        long endTime=System.currentTimeMillis();
        Time=endTime-starTime;
        System.out.println(Time);

        // take inverse FFT
        Complex[][] z = ifft2d(y);
        show(z, "z = ifft(y)");
        endTime=System.currentTimeMillis();
        Time=endTime-starTime;
        System.out.println(Time);
    }

}

