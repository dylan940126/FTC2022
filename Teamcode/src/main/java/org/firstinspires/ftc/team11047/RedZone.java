package org.firstinspires.ftc.team11047;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.team11047.custommodules.DriverBase;
import org.firstinspires.ftc.team11047.custommodules.MyMath;

@Autonomous
public class RedZone extends LinearOpMode {
    DriverBase driverBase = new DriverBase(this);
    double time = 0, last_y = 50;
    int red = 1;

    @Override
    public void runOpMode() {
        driverBase.initDevices();
        driverBase.switchToDockPipeline();
        while (!isStarted()) {
            telemetry.addData("position", driverBase.duckPipeline.getLevel());
            telemetry.update();
        }
        resetStartTime();
        driverBase.turntable.setHeight(driverBase.duckPipeline.getLevel());
        driverBase.switchToGoalPipeline();
        if (red == 1)
            driverBase.hubPipeline.setToRed();
        else
            driverBase.hubPipeline.setToBlue();
        driverBase.chassis.resetPosition(0, 23, 0);
        for (int cycle = 0; opModeIsActive(); ++cycle) {
//            going to aim
            while (opModeIsActive() && driverBase.chassis.current_y > 5)
                driverBase.chassis.drive(0.1 * red, -1, 0.1 * red, 1);
            while (opModeIsActive() && !driverBase.hubPipeline.isDetected())
                driverBase.chassis.drive(0.1 * red, -1, 0.1 * red, 0.5);
            driverBase.chassis.drive(0, 0, 0, 0);
//            aim and pour
            for (int check = 0; check < 20; ++check) {
                while (opModeIsActive() && (Math.abs(driverBase.hubPipeline.getX()) > 20 || !driverBase.turntable.isPourable()))
                    driverBase.chassis.drive(0.1 * Math.abs(driverBase.hubPipeline.getX()) * red, driverBase.hubPipeline.getX() * red, 0,
                            1.1 * Math.abs(MyMath.distanceToPower(driverBase.hubPipeline.getX()) / 30));
            }
            driverBase.chassis.drive(0, 0, 0, 0);
            driverBase.turntable.pour();
            sleep(500);
            driverBase.turntable.noPour();
            driverBase.chassis.resetPosition(0, 0, 0);
            driverBase.turntable.backFlow();
//            going to collect
            while (opModeIsActive() && driverBase.chassis.current_y < last_y) {
                driverBase.chassis.drive(0.1 * red, 1, -0.1 * red,
                        MyMath.distanceToPower(last_y - driverBase.chassis.current_y) / 4);
                driverBase.turntable.setHeight(0);
                driverBase.turntable.collect();
            }
            driverBase.chassis.drive(0, 0, 0, 0);
            driverBase.chassis.resetPosition(0, driverBase.chassis.current_y, 0);
            if (cycle % 2 == 1)
                while (opModeIsActive() && !driverBase.turntable.isCarry() && Math.abs(0.8 * red - driverBase.chassis.current_direction) > 0.1)
                    driverBase.chassis.drive(0, 0.3,
                            0.8 * red - driverBase.chassis.current_direction,
                            Math.abs(0.8 * red - driverBase.chassis.current_direction));
            time = getRuntime();
            while (opModeIsActive() && !driverBase.turntable.isCarry()) {
                if ((getRuntime() - time) % 1 < 0.5)
                    driverBase.chassis.drive(0, -1, 0, 0.05);
                else
                    driverBase.chassis.drive(0, 1,
                            0, 0.2);
            }
            last_y = driverBase.chassis.current_y;
            driverBase.turntable.backFlow();
            time = getRuntime();
            while (opModeIsActive() && (driverBase.chassis.current_x * red < 0 || getRuntime() - time < 0.5))
                driverBase.chassis.move(1 * red, -1, (0.2 * red - driverBase.chassis.current_direction) * 1, 1);

            if (getRuntime() > 26)
                break;
            driverBase.turntable.setHeight(3);
            driverBase.turntable.backFlow();
        }
    }
}
