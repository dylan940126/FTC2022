package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.custommodules.DriverBase;

@Autonomous
public class BlueDuck extends LinearOpMode {
    DriverBase driverBase = new DriverBase(this);

    @Override
    public void runOpMode() throws InterruptedException {
        driverBase.initDevices();
        driverBase.chassis.resetPosition(0, 0, 0);
        waitForStart();
        driverBase.turntable.stopCollect();
        driverBase.turntable.setHeight(2);
        driverBase.chassis.move_to(-1.5, 30, 0, 0.5);
        driverBase.turntable.setExtendSpeed(1);
        sleep(2000);
        driverBase.turntable.setContainer(true);
        sleep(1000);
        driverBase.turntable.setExtendSpeed(-1);
        driverBase.turntable.setContainer(false);
        sleep(2000);
        driverBase.turntable.setHeight(0);
        driverBase.chassis.move_to(-4, 68.5, Math.toRadians(15), 1);
        driverBase.turntable.setSpinner(-0.3);
        sleep(5000);
        driverBase.turntable.setSpinner(0);
        driverBase.chassis.move_to(-29, 74, 0, 0.5);
    }
}
