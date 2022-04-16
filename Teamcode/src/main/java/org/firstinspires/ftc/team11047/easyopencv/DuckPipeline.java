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
    private int duck1_count = 0, duck2_count = 0, duck3_count = 0;
    private int position = 0;
    public static Rect duck1 = new Rect(0, 120, 80, 200);
    public static Rect duck2 = new Rect(80, 120, 80, 200);
    public static Rect duck3 = new Rect(160, 120, 80, 200);
    private static Scalar hsv_low = new Scalar(0, 164, 0), hsv_high = new Scalar(113, 255, 255);
    private final Mat mat_hsv = new Mat();
    private final Mat mat_binary = new Mat();

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, mat_hsv, Imgproc.COLOR_RGB2HSV);
        Core.inRange(mat_hsv, hsv_low, hsv_high, mat_binary);
        duck1_count = Core.countNonZero(mat_binary.submat(duck1));
        duck2_count = Core.countNonZero(mat_binary.submat(duck2));
        duck3_count = Core.countNonZero(mat_binary.submat(duck3));
        Math.max(duck1_count, Math.max(duck2_count, duck3_count));
        if (duck1_count > duck2_count)
            if (duck1_count > duck3_count)
                position = duck1_count > 2000 ? 1 : 0;
            else
                position = duck3_count > 2000 ? 3 : 0;
        else if (duck2_count > duck3_count)
            position = duck2_count > 2000 ? 2 : 0;
        else
            position = duck3_count > 2000 ? 3 : 0;
        Imgproc.rectangle(input, duck1, new Scalar(0, 255, 0), 1);
        Imgproc.rectangle(input, duck2, new Scalar(0, 255, 0), 1);
        Imgproc.rectangle(input, duck3, new Scalar(0, 255, 0), 1);
        return input;
    }

    public int getLevel() {
        return position;
    }
}