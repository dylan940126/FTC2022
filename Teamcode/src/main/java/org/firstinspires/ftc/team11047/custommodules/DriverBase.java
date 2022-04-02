package org.firstinspires.ftc.team11047.custommodules;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.team11047.easyopencv.DuckPipeline;
import org.firstinspires.ftc.team11047.easyopencv.HubPipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.concurrent.TimeUnit;

public class DriverBase {
    public Chassis chassis;
    public TurnTable turntable;
    public FtcDashboard dashboard;
    public HubPipeline hubPipeline;
    public DuckPipeline duckPipeline;
    public WebcamName webcam_Name;
    public OpenCvWebcam webcam;
    private boolean camera_ready = false;
    private final LinearOpMode opMode;

    public DriverBase(LinearOpMode opMode) {
        this.opMode = opMode;
    }

    public void initDevices() {
        initDashboard();
        init_Camera();
        chassis = new Chassis(opMode);
        chassis.resetPosition(0, 0, 0);
        turntable = new TurnTable(opMode);
        new Thread(() -> {
            while (!opMode.isStopRequested()) {
                chassis.refreshPosition();
                opMode.telemetry.update();
            }
        }).start();
        waitForCamera();
    }

    public void initDashboard() {
        dashboard = FtcDashboard.getInstance();
        opMode.telemetry = new MultipleTelemetry(opMode.telemetry, dashboard.getTelemetry());
    }

    public void init_Camera() {
        hubPipeline = new HubPipeline();
        duckPipeline = new DuckPipeline();
        webcam_Name = opMode.hardwareMap.get(WebcamName.class, "Webcam 1");
        webcam = OpenCvCameraFactory.getInstance().createWebcam(webcam_Name);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.getExposureControl().setMode(ExposureControl.Mode.Manual);
                webcam.getExposureControl().setExposure(35, TimeUnit.SECONDS);
                webcam.setViewportRenderer(OpenCvCamera.ViewportRenderer.GPU_ACCELERATED);
                webcam.startStreaming(320, 240, OpenCvCameraRotation.SIDEWAYS_LEFT);
                if (dashboard != null)
                    dashboard.startCameraStream(webcam, 15);
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
        while (!opMode.isStopRequested() && !camera_ready) ;
    }
}