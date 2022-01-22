package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

/*
 * This sample demonstrates how to stream frames from Vuforia to the dashboard. Make sure to fill in
 * your Vuforia key below and select the 'Camera' preset on top right of the dashboard. This sample
 * also works for UVCs with slight adjustments.
 */
@Autonomous
public class VuforiaStreamOpMode extends LinearOpMode {

    // TODO: fill in
    public static final String VUFORIA_LICENSE_KEY = "ASyWHFT/////AAABmXoIOcO9jk9qg7s0UJfKTKoRXju+K0nE0R74CwqkL4P5IEClhhNjMV7jzoRFuZJqLMP25kjL7LajiOZDmFwqLBw4YLYprlWS8SkM2Hnw1lPk4AgQbudqVJzk65cmDvoCdY3u+jOaT/xhwPexTpzLC9Bz8FDjDSdbezTke47j9PD3pAQXLQ8MewH2b8vBZLdzdatLsvPfrSaWN75MsARyn65hGk+QUX8fVPQcq+5C6yYaWs6RfExFaJrnvnm+lnipQD0J8bk3XfBPmKYvgR/Fl/+8Jsxmmc6KntkWXHIxqc1ulxoQsfWUjd6dceKYZm2Rs70l6nP9+vNkVR9qRpy+nIO8SPxfDLhUjRXlKRYKSRgi";

    @Override
    public void runOpMode() throws InterruptedException {
        // gives Vuforia more time to exit before the watchdog notices
        msStuckDetectStop = 2500;

        VuforiaLocalizer.Parameters vuforiaParams = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        vuforiaParams.vuforiaLicenseKey = VUFORIA_LICENSE_KEY;
        vuforiaParams.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");
        vuforiaParams.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        VuforiaLocalizer vuforia = ClassFactory.getInstance().createVuforia(vuforiaParams);

        FtcDashboard.getInstance().startCameraStream(vuforia, 0);

        waitForStart();

        while (opModeIsActive()) ;
    }
}