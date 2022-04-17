package org.firstinspires.ftc.team11047.ftc2022;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class DuckPipeline extends OpenCvPipeline {

    /*
     * These are our variables that will be
     * modifiable from the variable tuner.
     *
     * Scalars in OpenCV are generally used to
     * represent color. So our values in the
     * lower and upper Scalars here represent
     * the Y, Cr and Cb values respectively.
     *
     * YCbCr, like most color spaces, range
     * from 0-255, so we default to those
     * min and max values here for now, meaning
     * that all pixels will be shown.
     */
//    public Scalar lower = new Scalar(24, 0, 223);
//    public Scalar upper = new Scalar(70, 255, 255);
    public Scalar lower = new Scalar(0, 0, 0);
    public Scalar upper = new Scalar(20, 255, 255);


    /*
     * A good practice when typing EOCV pipelines is
     * declaring the Mats you will use here at the top
     * of your pipeline, to reuse the same buffers every
     * time. This removes the need to call mat.release()
     * with every Mat you create on the processFrame method,
     * and therefore, reducing the possibility of getting a
     * memory leak and causing the app to crash due to an
     * "Out of Memory" error.
     */
    private Mat ycrcbMat = new Mat();
    private Mat binaryMat = new Mat();
    private Mat maskedInputMat = new Mat();
    private Mat leftROI = new Mat();
    private Mat midROI = new Mat();

    Point rect = new Point(65, 45);
    Rect left = new Rect(0, 0, (int) rect.x, (int) rect.y);
    Rect mid = new Rect(0, 0, (int) rect.x, (int) rect.y);

    int position = 0; // left = 0, mid = 1, right = 2;

    @Override
    public Mat processFrame(Mat input) {
        /*
         * Converts our input mat from RGB to YCrCb.
         * EOCV ALWAYS returns RGB mats, so you'd
         * always convert from RGB to the color
         * space you want to use.
         *
         * Takes our "input" mat as an input, and outputs
         * to a separate Mat buffer "ycrcbMat"
         */

        int img_w = input.cols();
        int img_h = input.rows();

        left.y = 0;
        mid.y = (int) (img_h * 0.59 - rect.x / 2);

        left.x = (int) (img_w * 0.12);
        mid.x = (int) (img_w * 0.12);

        Imgproc.cvtColor(input, ycrcbMat, Imgproc.COLOR_RGB2HSV);

        /*
         * This is where our thresholding actually happens.
         * Takes our "ycrcbMat" as input and outputs a "binary"
         * Mat to "binaryMat" of the same size as our input.
         * "Discards" all the pixels outside the bounds specified
         * by the scalars above (and modifiable with EOCV-Sim's
         * live variable tuner.)
         *
         * Binary meaning that we have either a 0 or 255 value
         * for every pixel.
         *
         * 0 represents our pixels that were outside the bounds
         * 255 represents our pixels that are inside the bounds
         */
        Core.inRange(ycrcbMat, lower, upper, binaryMat);

        /*
         * Release the reusable Mat so that old data doesn't
         * affect the next step in the current processing
         */
        maskedInputMat.release();

        /*
         * Now, with our binary Mat, we perform a "bitwise and"
         * to our input image, meaning that we will perform a mask
         * which will include the pixels from our input Mat which
         * are "255" in our binary Mat (meaning that they're inside
         * the range) and will discard any other pixel outside the
         * range (RGB 0, 0, 0. All discarded pixels will be black)
         */
        Core.bitwise_and(input, input, maskedInputMat, binaryMat);

        /*
         * The Mat returned from this method is the
         * one displayed on the viewport.
         *
         * To visualize our threshold, we'll return
         * the "masked input mat" which shows the
         * pixel from the input Mat that were inside
         * the threshold range.
         */
        leftROI = new Mat(binaryMat, left);
        midROI = new Mat(binaryMat, mid);
        int left_white = Core.countNonZero(leftROI);
        int mid_white = Core.countNonZero(midROI);


        int max_white = 1300;
        if (left_white > max_white) {
            position = 0;
        } else if (mid_white > max_white) {
            position = 1;
        } else
            position = 2;
        Imgproc.rectangle(maskedInputMat, left, new Scalar(0, 255, 0), 1);
        Imgproc.rectangle(maskedInputMat, mid, new Scalar(0, 255, 0), 1);
        return maskedInputMat;
    }

    public int getLevel() {
        return position + 1;
    }

}