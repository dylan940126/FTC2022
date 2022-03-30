package org.firstinspires.ftc.teamcode.easyopencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.openftc.easyopencv.OpenCvPipeline;

public class HubPipeline extends OpenCvPipeline {
    private int count = 0, duck1_count = 0, duck2_count = 0;
    private double x = 0;
    public Scalar hsv_low = new Scalar(0, 65.2, 0), hsv_high = new Scalar(10.4, 255, 254.5);
    private Mat mat_hsv = new Mat(), mat_binary = new Mat(), mask = new Mat();

    public HubPipeline() {
        setToRed();
    }

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, mat_hsv, Imgproc.COLOR_RGB2HSV);
        Core.inRange(mat_hsv, hsv_low, hsv_high, mat_binary);
        count = Core.countNonZero(mat_binary);
        if (count > 1000) {
            Moments moments = Imgproc.moments(mat_binary);
            x = moments.m10 / moments.m00 - 190;
        } else
            x = 0;
        return input;
    }

    public double getX() {
        return x;
    }

    public int getCount() {
        return count;
    }

    public void setToBlue() {
        hsv_low = new Scalar(0, 65.2, 0);
        hsv_high = new Scalar(10.4, 255, 254.5);
    }

    public void setToRed() {
        hsv_low = new Scalar(0, 65.2, 0);
        hsv_high = new Scalar(10.4, 255, 254.5);
    }
}
