package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mods.DriverBase;

@TeleOp
@Config
public class MovingTest extends DriverBase {
    public static double x = 0, y = 0, turn = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        initDevices();
        waitForStart();
        while (true) {
            double d_forward, d_right, d_turn;
            d_forward = -gamepad1.left_stick_y * 0.7;
            d_right = gamepad1.left_stick_x * 0.7;
            d_turn = -gamepad1.right_stick_x * 0.5;
            chassis.drive(d_right, d_forward, d_turn, Math.max(Math.max(Math.abs(d_forward), Math.abs(d_right)), Math.abs(d_turn)));
            if (gamepad1.left_bumper)
                chassis.resetPosition();
            if (gamepad1.a)
                chassis.move_to(x,y,turn,1);
        }
    }
}
