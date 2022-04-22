package org.firstinspires.ftc.team11047.custommodules;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.team11047.easyopencv.HubPipeline;
import org.firstinspires.ftc.team11047.ftc2022.DuckPipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

public abstract class Robot extends RobotFrame {
    public FtcDashboard dashboard;
    public HubPipeline hubPipeline;
    public DuckPipeline duckPipeline;
    public WebcamName webcam_Name;
    public OpenCvWebcam webcam;
    private boolean camera_ready = false;

    public void initRobot() {
        initFrame();
        initDashboard();
        init_Camera();
        resetPosition(0, 0, 0);
        new Thread(() -> {
            while (!isStopRequested()) {
                refreshPosition();
                telemetry.update();
            }
        }).start();
        waitForCamera();
    }

    public void initDashboard() {
        dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
    }

    public void init_Camera() {
        hubPipeline = new HubPipeline();
        duckPipeline = new DuckPipeline();
        webcam_Name = hardwareMap.get(WebcamName.class, "Webcam");
        webcam = OpenCvCameraFactory.getInstance().createWebcam(webcam_Name);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.setPipeline(duckPipeline);
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
                camera_ready = true;
            }

            @Override
            public void onError(int errorCode) {
            }
        });
    }

    public void switchToGoalPipeline() {
        webcam.setPipeline(hubPipeline);
    }

    public void switchToDockPipeline() {
        webcam.setPipeline(duckPipeline);
    }

    public void close_camera() {
        if (webcam != null)
            webcam.stopStreaming();
        if (dashboard != null)
            dashboard.stopCameraStream();
    }

    public void waitForCamera() {
        while (!isStopRequested() && !camera_ready) ;
    }
}