package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.custommodules.DriverBase;
import org.firstinspires.ftc.teamcode.custommodules.MyMath;

@Autonomous
public class RedZone extends LinearOpMode {
    DriverBase driverBase = new DriverBase(this);
    double time;

    @Override
    public void runOpMode() {
        driverBase.initDevices();
        driverBase.switchToDockPipeline();
        while (!isStarted()) {
            telemetry.addData("position", driverBase.levelPipeline.getLevel());
            telemetry.update();
        }
        resetStartTime();
        driverBase.turntable.setHeight(driverBase.levelPipeline.getLevel());
        driverBase.switchToGoalPipeline();
        driverBase.hubPipeline.setToRed();
        driverBase.chassis.drive(0.1, -1, 0.1, 0.9);
        sleep(500);
        driverBase.chassis.drive(0, 0, 0, 0);
        while (Math.abs(driverBase.hubPipeline.getX()) >= 5 && opModeIsActive())
            driverBase.chassis.drive(0.3, driverBase.hubPipeline.getX(), 0, 0.3 + Math.abs(MyMath.distanceToPower(driverBase.hubPipeline.getX()) / 50));
        driverBase.chassis.drive(0, 0, 0, 0);
        driverBase.turntable.pour();
        sleep(700);
        driverBase.turntable.noPour();
        driverBase.turntable.setHeight(0);
        for (int i = 0; opModeIsActive(); ++i) {
            driverBase.chassis.resetPosition(0, -40, 0);
            driverBase.turntable.backFlow();
            driverBase.chassis.move_to(1, 0, 0, 1, () -> {
                driverBase.turntable.setHeight(0);
                return true;
            });
            driverBase.turntable.collect();
            driverBase.chassis.move_to(-((i % 2) * 8 + 3), 9 + i * 3, 0, 0.5);
            while (opModeIsActive() && !driverBase.turntable.isCarry())
                driverBase.chassis.drive(0, 0.2, Math.cos((getRuntime() - time) * 2 * Math.PI / 1.5), 0.1);
            driverBase.turntable.backFlow();
            driverBase.chassis.move_to(1.5, 0, 0.2, 1);
            if (getRuntime() > 26)
                break;
            if (driverBase.turntable.isCarry()) {
                driverBase.turntable.setHeight(3);
                driverBase.turntable.backFlow();
            }
            time = getRuntime();
            while (opModeIsActive() && getRuntime() - time < 1) {
                driverBase.chassis.drive(0.3, -1, 0.2, 1);
                if (driverBase.turntable.isCarry()) {
                    driverBase.turntable.setHeight(3);
                    driverBase.turntable.backFlow();
                }
            }
            driverBase.chassis.drive(0, 0, 0, 0);
            time = getRuntime();
            while (opModeIsActive() && getRuntime() - time < 1.5 && !driverBase.turntable.isCarry()) {
                driverBase.chassis.drive(0.3,
                        Math.abs(MyMath.distanceToPower(driverBase.hubPipeline.getX()) / 50), 0,
                        Math.abs(MyMath.distanceToPower(driverBase.hubPipeline.getX()) / 50) + 0.2);
                if (driverBase.turntable.isCarry()) {
                    driverBase.turntable.setHeight(3);
                    driverBase.turntable.backFlow();
                }
            }
            driverBase.chassis.drive(0, 0, 0, 0);
            if (!driverBase.turntable.isCarry())
                continue;
            time = getRuntime();
            while (opModeIsActive() && getRuntime() - time < 0.5) {
                driverBase.chassis.drive(0, driverBase.hubPipeline.getX(), 0, Math.abs(MyMath.distanceToPower(driverBase.hubPipeline.getX()) / 50));
                if (Math.abs(driverBase.hubPipeline.getX()) < 20 && driverBase.turntable.isPourable())
                    driverBase.turntable.pour();
                else
                    time = getRuntime();
            }
            driverBase.turntable.setHeight(0);
            driverBase.turntable.noPour();
        }
        while (opModeIsActive()) {
            driverBase.turntable.setHeight(0);
        }
    }
}
