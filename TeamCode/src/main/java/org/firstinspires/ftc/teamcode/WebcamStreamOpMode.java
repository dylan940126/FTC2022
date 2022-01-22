package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mods.DriverBase;
import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.concurrent.TimeUnit;

/*
 * This sample demonstrates how to stream frames from Vuforia to the dashboard. Make sure to fill in
 * your Vuforia key below and select the 'Camera' preset on top right of the dashboard. This sample
 * also works for UVCs with slight adjustments.
 */
@TeleOp
@Config
public class WebcamStreamOpMode extends DriverBase {
    public static int exposure = 1;

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        initDevices();
        Pipeline pipeline = new Pipeline();
//        Supported resolutions are [640x480], [160x120], [176x144], [320x176], [320x240], [352x288], [432x240], [544x288], [640x360], [752x416], [800x448], [800x600], [864x480], [960x544], [960x720], [1024x576], [1184x656], [1280x720], [1280x960]
        webcam.setPipeline(pipeline);
        webcam.startStreaming(640, 360, OpenCvCameraRotation.SIDEWAYS_LEFT);
        waitForStart();
        dashboard.startCameraStream(webcam, 30);
        telemetry.addData("max", webcam.getExposureControl().getMaxExposure(TimeUnit.MICROSECONDS));
        telemetry.addData("min", webcam.getExposureControl().getMinExposure(TimeUnit.MICROSECONDS));
        telemetry.update();
        while (opModeIsActive()) {
            webcam.getExposureControl().setExposure((long) exposure, TimeUnit.MICROSECONDS);
        }
    }

    class Pipeline extends OpenCvPipeline {
        @Override
        public Mat processFrame(Mat input) {
//            Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2BGR);
            return input;
        }
    }
}