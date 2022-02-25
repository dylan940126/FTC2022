package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.custommodules.DriverBase;
import org.firstinspires.ftc.teamcode.easyopencv.DuckPosition;

@TeleOp
public class DuckPositionTest extends LinearOpMode {
    public DriverBase driverBase;

    @Override
    public void runOpMode() throws InterruptedException {
        driverBase = new DriverBase(this);
        driverBase.initDevices();
        DuckPosition duckPosition = new DuckPosition();
        driverBase.set_pipeline(duckPosition);
        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("duck1", duckPosition.getDuck1_count());
            telemetry.addData("duck2", duckPosition.getDuck2_count());
            telemetry.addData("position", duckPosition.getPosition());
            telemetry.update();
        }
    }
}
