package org.firstinspires.ftc.team11047;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.team11047.custommodules.DriverBase;

@Autonomous
public class RedDuck extends LinearOpMode {
    DriverBase driverBase = new DriverBase(this);

    @Override
    public void runOpMode() {
        driverBase.initDevices();
        driverBase.switchToDockPipeline();
        while (!isStarted()) {
            telemetry.addData("position", driverBase.duckPipeline.getLevel());
            telemetry.update();
        }
        waitForStart();
        int height = driverBase.duckPipeline.getLevel();
        driverBase.switchToGoalPipeline();
        driverBase.hubPipeline.setToRed();
        driverBase.chassis.resetPosition(0, -23, 0);
        driverBase.chassis.move_to(-42, -41, Math.toRadians(-90), 1);
        driverBase.turntable.setHeight(height);
        while(opModeIsActive()&&!driverBase.turntable.isPourable());
        driverBase.turntable.pour();
        sleep(500);
        driverBase.turntable.noPour();
        while(opModeIsActive()&&!driverBase.turntable.isCollectable())
            driverBase.turntable.setHeight(0);
        driverBase.chassis.move_to(-11, -48, Math.toRadians(-90), 1);
        driverBase.turntable.setSpinnerRed();
        sleep(5000);
        driverBase.turntable.setSpinnerStop();
        driverBase.chassis.move_to(-27, -48, Math.toRadians(0), 1);
        driverBase.chassis.move_to(0, 0, Math.toRadians(0), 1);
    }
}
