package org.firstinspires.ftc.team11047;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team11047.custommodules.Robot;

@Autonomous
public class BlueDuck extends Robot {
    @Override
    public void runOpMode() throws InterruptedException {
        initRobot();
        switchToDockPipeline();
        waitForStart();
        int height = duckPipeline.getLevel();
        switchToGoalPipeline();
        hubPipeline.setToBlue();
        resetPosition(0, -23, 0);
        move_to(36, -41, Math.toRadians(90), 1);
        setHeight(height);
        while (opModeIsActive() && !isPourable()) ;
        pour();
        sleep(500);
        noPour();
        while (opModeIsActive() && !isCollectable())
            setHeight(0);
        move_to(10, -46, Math.toRadians(180), 1);
        setSpinnerBlue();
        double time = getRuntime();
        while (opModeIsActive() && getRuntime() - time < 3)
            drive(+1, 0, 0, 0.03);
        drive(0, 0, 0, 0);
        sleep(2000);
        setSpinnerStop();
        move_to(27, -51, Math.toRadians(0), 1);
    }
}
