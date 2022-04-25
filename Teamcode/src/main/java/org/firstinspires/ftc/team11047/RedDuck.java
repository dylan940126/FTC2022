package org.firstinspires.ftc.team11047;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team11047.custommodules.Robot;

@Autonomous
public class RedDuck extends Robot {

    @Override
    public void runOpMode() {
        initRobot();
        switchToDockPipeline();
        waitForStart();
        int height = duckPipeline.getLevel();
        switchToGoalPipeline();
        hubPipeline.setToRed();
        resetPosition(0, -23, 0);
        move_to(-38, -41, Math.toRadians(-90), 1);
        setHeight(height);
        while (opModeIsActive() && !isPourable()) ;
        pour();
        sleep(500);
        noPour();
        while (opModeIsActive() && !isCollectable())
            setHeight(0);
        move_to(-10, -49, Math.toRadians(-90), 0.5);
        setSpinnerRed();
        double time = getRuntime();
        while (opModeIsActive() && getRuntime() - time < 3)
            drive(1, 1, 0, 0.03);
        drive(0, 0, 0, 0);
        sleep(2000);
        setSpinnerStop();
        move_to(-23, -52, Math.toRadians(0), 1);
    }
}
