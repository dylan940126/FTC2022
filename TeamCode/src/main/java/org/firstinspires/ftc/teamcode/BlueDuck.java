package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.custommodules.DriverBase;
import org.firstinspires.ftc.teamcode.custommodules.MyMath;

@Autonomous
public class BlueDuck extends LinearOpMode {
    DriverBase driverBase = new DriverBase(this);

    @Override
    public void runOpMode() throws InterruptedException {
        driverBase.initDevices();
        driverBase.turntable.setHeight(driverBase.duckPosition.getPosition());
        driverBase.switchToGoalPipeline();
        driverBase.chassis.drive(0.1, -1, 0.1, 0.9);
        sleep(500);
        driverBase.chassis.drive(0, 0, 0, 0);
        while (Math.abs(driverBase.redGoalPipeline.getX()) >= 5 && opModeIsActive())
            driverBase.chassis.drive(0.3, driverBase.redGoalPipeline.getX(), 0, 0.3 + Math.abs(MyMath.distanceToPower(driverBase.redGoalPipeline.getX()) / 50));
        driverBase.chassis.drive(0, 0, 0, 0);
        driverBase.turntable.pour();
        sleep(700);
        driverBase.turntable.noPour();
        driverBase.turntable.setHeight(0);
        driverBase.chassis.move_to(-4, 68.5, Math.toRadians(15), 1);
        driverBase.turntable.setSpinner(-0.3);
        sleep(5000);
        driverBase.turntable.setSpinner(0);
        driverBase.chassis.move_to(-29, 74, 0, 0.5);
    }
}
