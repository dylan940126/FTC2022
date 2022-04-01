package org.firstinspires.ftc.team11047.easyopencv;

import com.acmerobotics.dashboard.config.Config;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.openftc.easyopencv.OpenCvPipeline;

@Config
public class HubPipeline extends OpenCvPipeline {
    private int count = 0;
    private final int duck1_count = 0;
    private final int duck2_count = 0;
    private double x = 0;
    public Scalar hsv_low = new Scalar(0, 65.2, 0), hsv_high = new Scalar(10.4, 255, 254.5);
    private final Mat mat_hsv = new Mat();
    private final Mat mat_binary = new Mat();
    private final Mat mask = new Mat();

    public HubPipeline() {
        setToRed();
    }

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, mat_hsv, Imgproc.COLOR_RGB2HSV);
        Core.inRange(mat_hsv, hsv_low, hsv_high, mat_binary);
//        Imgproc.cvtColor(mat_binary, input, Imgproc.COLOR_GRAY2RGB);
        count = Core.countNonZero(mat_binary);
        if (isDetected()) {
            Moments moments = Imgproc.moments(mat_binary);
            double temp = moments.m10 / moments.m00;
            Imgproc.line(input, new Point(temp, 320), new Point(temp, 0), new Scalar(0, 255, 0), 3, 1);
            temp -= 120;
            if (x == 0 || Math.abs(x - temp) < 70) {//濾波
                x = temp;
            }
        } else
            x = 0;
        return input;
    }

    public double getX() {
        return x;
    }

    public boolean isDetected() {
        return count > 1500;
    }

    public int getCount() {
        return count;
    }

    public void setToBlue() {
        hsv_low = new Scalar(110, 65.2, 0);
        hsv_high = new Scalar(120, 255, 254.5);
    }

    public void setToRed() {
        hsv_low = new Scalar(0, 65.2, 0);
        hsv_high = new Scalar(10.4, 255, 254.5);
    }
}
