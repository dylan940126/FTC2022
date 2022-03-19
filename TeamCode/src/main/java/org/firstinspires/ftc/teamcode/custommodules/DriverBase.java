package org.firstinspires.ftc.teamcode.custommodules;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.easyopencv.DuckPosition;
import org.firstinspires.ftc.teamcode.easyopencv.RedGoalPipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

public class DriverBase {
    public Chassis chassis;
    public TurnTable turntable;
    public FtcDashboard dashboard;
    public RedGoalPipeline redGoalPipeline;
    public DuckPosition duckPosition;
    public WebcamName webcam_Name;
    public OpenCvWebcam webcam;
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
    }

    public void initDashboard() {
        dashboard = FtcDashboard.getInstance();
        opMode.telemetry = new MultipleTelemetry(opMode.telemetry, dashboard.getTelemetry());
    }

    public void init_Camera() {
        redGoalPipeline = new RedGoalPipeline();
        duckPosition = new DuckPosition();
        webcam_Name = opMode.hardwareMap.get(WebcamName.class, "Webcam 1");
        webcam = OpenCvCameraFactory.getInstance().createWebcam(webcam_Name);
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.setViewportRenderer(OpenCvCamera.ViewportRenderer.GPU_ACCELERATED);
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
                if (dashboard != null)
                    dashboard.startCameraStream(webcam, 30);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }

    public void switchToGoalPipeline() {
        webcam.setPipeline(redGoalPipeline);
    }

    public void switchToDockPipeline() {
        webcam.setPipeline(duckPosition);
    }

    public void close_camera() {
        if (webcam != null)
            webcam.stopStreaming();
        if (dashboard != null)
            dashboard.stopCameraStream();
    }
}