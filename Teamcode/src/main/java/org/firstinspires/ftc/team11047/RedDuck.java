package org.firstinspires.ftc.team11047;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team11047.custommodules.Robot;

@Autonomous
public class RedDuck extends Robot {

    @Override
    public void runOpMode() {
        initRobot();
        switchToDockPipeline();
        while (!isStarted()) {
            telemetry.addData("position", duckPipeline.getLevel());
            telemetry.update();
        }
        waitForStart();
        int height = duckPipeline.getLevel();
        switchToGoalPipeline();
        hubPipeline.setToRed();
        resetPosition(0, -23, 0);
        move_to(-42, -41, Math.toRadians(-90), 1);
        setHeight(height);
        while (opModeIsActive() && !isPourable()) ;
        pour();
        sleep(500);
        noPour();
        while (opModeIsActive() && !isCollectable())
            setHeight(0);
        move_to(-10, -49, Math.toRadians(-90), 0.5);
        setSpinnerRed();
        sleep(5000);
        setSpinnerStop();
        move_to(-27, -51, Math.toRadians(0), 1);
        move_to(0, -23, Math.toRadians(0), 1);
    }
}
