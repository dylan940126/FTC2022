package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.custommodules.DriverBase;

@TeleOp
@Config
public class MovingTest extends LinearOpMode {
    public double position = 5370;
    public DriverBase driverBase = new DriverBase(this);

    @Override
    public void runOpMode() throws InterruptedException {
        driverBase.initDevices();
        waitForStart();
        while (opModeIsActive()) {
            double x = gamepad1.left_stick_x, y = -gamepad1.left_stick_y, z = -gamepad1.right_stick_x;
            driverBase.chassis.drive(x, y, z, Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z)));
            if (gamepad1.left_bumper)
                driverBase.chassis.resetPosition(0, 0, 0);
            if (gamepad1.a) {
                driverBase.chassis.move_to(0, 10, Math.PI / 2, 1);
                driverBase.chassis.move_to(10, 0, Math.PI / 2, 1);
                driverBase.chassis.move_to(0, 0, 0, 1);
            }
        }
    }
}