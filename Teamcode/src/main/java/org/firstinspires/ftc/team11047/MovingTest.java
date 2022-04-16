package org.firstinspires.ftc.team11047;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.team11047.custommodules.Robot;
import org.firstinspires.ftc.team11047.custommodules.MyMath;

@TeleOp
@Config
public class MovingTest extends Robot {
    public static double position = 5370, time = 0, speed = 0.4, circle_time = 1;

    @Override
    public void runOpMode() throws InterruptedException {
        initRobot();
        waitForStart();
        switchToGoalPipeline();
        while (opModeIsActive()) {
            double x = gamepad1.left_stick_x, y = -gamepad1.left_stick_y, z = -gamepad1.right_stick_x;
            if (gamepad1.left_bumper) {
                resetPosition(0, 0, 0);
                time = getRuntime();
            }
            telemetry.addData("count", hubPipeline.getX());
            if (gamepad1.a) {
                drive(0.1 * Math.abs(hubPipeline.getX()) * -1, hubPipeline.getX() * -1, 0,
                        1.1 * Math.abs(MyMath.distanceToPower(hubPipeline.getX()) / 30));
            } else {
                drive(x, y, z, Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z)));
            }
        }
    }
}