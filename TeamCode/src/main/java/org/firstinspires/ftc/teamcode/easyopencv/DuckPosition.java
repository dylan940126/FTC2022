package org.firstinspires.ftc.teamcode.easyopencv;

import com.acmerobotics.dashboard.config.Config;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

@Config
public class DuckPosition extends OpenCvPipeline {
    private int duck1_count = 0;
    private int duck2_count = 0;
    private Position position = Position.NOTHING;
    public static Rect duck1 = new Rect(100, 430, 100, 100);
    public static Rect duck2 = new Rect(310, 410, 100, 100);
    private static Scalar hsv_low = new Scalar(14, 24, 0), hsv_high = new Scalar(42, 255, 255);
    private Mat mat_hsv = new Mat(), mat_binary = new Mat(), mask = new Mat();

    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, mat_hsv, Imgproc.COLOR_RGB2HSV);
        Core.inRange(mat_hsv, hsv_low, hsv_high, mat_binary);
        duck1_count = Core.countNonZero(mat_binary.submat(duck1));
        duck2_count = Core.countNonZero(mat_binary.submat(duck2));
        if (getDuck1_count() > 700)
            if (getDuck2_count() > 700)
                position = getDuck1_count() > getDuck2_count() ? Position.LEFT : Position.RIGHT;
            else
                position = Position.LEFT;
        else if (getDuck2_count() > 700)
            position = Position.RIGHT;
        else
            position = Position.NOTHING;
        Imgproc.rectangle(input, duck1, new Scalar(0, 255, 0), 1);
        Imgproc.rectangle(input, duck2, new Scalar(0, 255, 0), 1);
        return input;
    }

    public int getDuck1_count() {
        return duck1_count;
    }

    public int getDuck2_count() {
        return duck2_count;
    }

    public Position getPosition() {
        return position;
    }

    enum Position {
        LEFT,
        RIGHT,
        NOTHING;
    }
}