package org.firstinspires.ftc.teamcode.easyopencv;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

@Config
public class LevelPipeline extends OpenCvPipeline {
    private int duck1_count = 0, duck2_count = 0;
    private int position;
    public static Rect duck1 = new Rect(75, 140, 100, 100);
    public static Rect duck2 = new Rect(190, 140, 100, 100);
    public static Scalar hsv_low = new Scalar(0, 164, 0), hsv_high = new Scalar(113, 255, 255);
    private Mat mat_hsv = new Mat(), mat_binary = new Mat(), mask = new Mat();

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