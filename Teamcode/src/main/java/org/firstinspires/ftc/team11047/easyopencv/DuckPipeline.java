package org.firstinspires.ftc.team11047.easyopencv;

import com.acmerobotics.dashboard.config.Config;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

@Config
public class DuckPipeline extends OpenCvPipeline {
    private int duck1_count = 0, duck2_count = 0;
    private int position = 0;
    public static Rect duck1 = new Rect(0, 120, 120, 200);
    public static Rect duck2 = new Rect(120, 120, 120, 200);
    public static Scalar hsv_low = new Scalar(0, 164, 0), hsv_high = new Scalar(113, 255, 255);
    private final Mat mat_hsv = new Mat();
    private final Mat mat_binary = new Mat();

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, mat_hsv, Imgproc.COLOR_RGB2HSV);
        Core.inRange(mat_hsv, hsv_low, hsv_high, mat_binary);
        duck1_count = Core.countNonZero(mat_binary.submat(duck1));
        duck2_count = Core.countNonZero(mat_binary.submat(duck2));
        if (duck1_count > 2000)
            if (duck2_count > 2000)
                position = duck1_count > duck2_count ? 1 : 2;
            else
                position = 1;
        else if (duck2_count > 2000)
            position = 2;
        else
            position = 3;
        Imgproc.rectangle(input, duck1, new Scalar(0, 255, 0), 1);
        Imgproc.rectangle(input, duck2, new Scalar(0, 255, 0), 1);
        return input;
    }

    public int getLevel() {
        return position;
    }
}