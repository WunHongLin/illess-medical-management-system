package com.example.illess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

public class similarClass {

    private Double histVal;

    public boolean isMatch(Bitmap bitmap1, Bitmap bitmap2){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Mat mat1 = new Mat();
        Utils.bitmapToMat(bitmap1,mat1);
        Mat mat2 = new Mat();
        Utils.bitmapToMat(bitmap2,mat2);

        Mat mat_1 =new Mat();
        Mat mat_2 =new Mat();

        Imgproc.cvtColor(mat1, mat_1, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(mat2, mat_2, Imgproc.COLOR_BGR2HSV);

        Mat hist_1 =new Mat();
        Mat hist_2 =new Mat();

        MatOfFloat ranges =new MatOfFloat(0f,256f);
        MatOfInt histSize =new MatOfInt(100);

        Imgproc.calcHist(Arrays.asList(mat_1),new MatOfInt(0),new Mat(), hist_1, histSize, ranges);
        Imgproc.calcHist(Arrays.asList(mat_2),new MatOfInt(0),new Mat(), hist_2, histSize, ranges);

        histVal = Imgproc.compareHist(hist_1, hist_2, Imgproc.CV_COMP_CORREL);

        Log.v("accuracy：",Double.toString(histVal));

        if (histVal >0.6){
            return true;
        }

        return false;
    }

    public Double getAccuracy(){
        return histVal;
    }
}
