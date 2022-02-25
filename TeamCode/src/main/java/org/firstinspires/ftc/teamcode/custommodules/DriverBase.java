package org.firstinspires.ftc.teamcode.custommodules;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

public class DriverBase {
    public Chassis chassis;
    public TurnTable turntable;
    public FtcDashboard dashboard;
    public WebcamName webcam_Name;
    public OpenCvWebcam webcam;
    private final LinearOpMode opMode;

    public DriverBase(LinearOpMode opMode) {
        this.opMode = opMode;
    }

    public void initDevices() {
        chassis = new Chassis(opMode);
        chassis.start();
        turntable = new TurnTable(opMode);
        initDashboard();
        init_Camera();
    }

    public void initDashboard() {
        dashboard = FtcDashboard.getInstance();
        opMode.telemetry = new MultipleTelemetry(opMode.telemetry, dashboard.getTelemetry());
    }

    public void init_Camera() {
        webcam_Name = opMode.hardwareMap.get(WebcamName.class, "Webcam 1");
        webcam = OpenCvCameraFactory.getInstance().createWebcam(webcam_Name);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.setViewportRenderer(OpenCvCamera.ViewportRenderer.GPU_ACCELERATED);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }

    public void set_pipeline(OpenCvPipeline pipeline) {
        webcam.startStreaming(640, 480, OpenCvCameraRotation.SIDEWAYS_RIGHT);
        if (webcam != null)
            webcam.setPipeline(pipeline);
        if (dashboard != null)
            dashboard.startCameraStream(webcam, 30);
    }

    public void close_camera() {
        if (webcam != null)
            webcam.stopStreaming();
        if (dashboard != null)
            dashboard.stopCameraStream();
    }
}