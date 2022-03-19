package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.custommodules.DriverBase;

@TeleOp
@Config
public class MovingTest extends LinearOpMode {
    public static double position = 5370, time = 0, speed = 0.4, circle_time = 1;
    public DriverBase driverBase = new DriverBase(this);

    @Override
    public void runOpMode() throws InterruptedException {
        driverBase.initDevices();
        waitForStart();
        while (opModeIsActive()) {
            double x = gamepad1.left_stick_x, y = -gamepad1.left_stick_y, z = -gamepad1.right_stick_x;
            if (gamepad1.left_bumper) {
                driverBase.chassis.resetPosition(0, 0, 0);
                time = getRuntime();
            }
            if (gamepad1.a) {
                driverBase.chassis.drive(0, 0.3, Math.cos((getRuntime() - time) * 2 * Math.PI / circle_time), speed);
                telemetry.addData("132", Math.cos((getRuntime() - time) * 2 * Math.PI / circle_time));
            } else
                driverBase.chassis.drive(x, y, z, Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z)));
        }
    }
}