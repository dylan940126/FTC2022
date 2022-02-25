package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.custommodules.DriverBase;

@Autonomous
public class BlueZone extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        DriverBase driverBase = new DriverBase(this);
        driverBase.initDevices();
        waitForStart();
        while (opModeIsActive()) {
            driverBase.chassis.move_to(0, -16, 0, 0.5);
            driverBase.turntable.stopCollect();
            driverBase.turntable.setHeight(2);
            driverBase.turntable.setExtendSpeed(1);
            sleep(2000);
            driverBase.turntable.setContainer(true);
            sleep(1000);
            driverBase.turntable.setContainer(false);
            driverBase.turntable.setExtendSpeed(-1);
            sleep(2000);
            driverBase.chassis.move_to(0, 35, 0, 1);
            driverBase.turntable.setHeight(0);
            driverBase.chassis.move_to(5, 35, 0, 1);
            driverBase.turntable.Collect();
            while (opModeIsActive() && !driverBase.turntable.isCarry()) {
                driverBase.chassis.drive(3, 2, 0, 0.3);
                sleep(500);
                driverBase.chassis.drive(-3, 2, 0, 0.3);
                sleep(500);
            }
            driverBase.turntable.backFlow();
            driverBase.chassis.move_to(0, 35, 0, 1);
        }
    }
}
