package org.firstinspires.ftc.team11047;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.team11047.custommodules.DriverBase;

@Autonomous
public class GoBack extends LinearOpMode {

    DriverBase driverBase = new DriverBase(this);

    @Override
    public void runOpMode() throws InterruptedException {
        driverBase.initDevices();
        driverBase.switchToDockPipeline();
        waitForStart();
        driverBase.turntable.setHeight(driverBase.duckPipeline.getLevel());
        driverBase.chassis.drive(0, -1, 0, 0.7);
        sleep(530);
        driverBase.chassis.drive(0, 0, 0, 0);
        driverBase.turntable.pour();
        sleep(700);
        driverBase.turntable.noPour();
        driverBase.chassis.resetPosition(0, 0, 0);
        driverBase.turntable.setHeight(0);
        driverBase.chassis.move_to(-1, 45, 0, 1);
        driverBase.chassis.move_to(15, 45, 0, 1);
    }
}
