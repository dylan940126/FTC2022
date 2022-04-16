package org.firstinspires.ftc.team11047;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team11047.custommodules.Robot;

@Autonomous
public class GoBack extends Robot {

    @Override
    public void runOpMode() throws InterruptedException {
        initRobot();
        switchToDockPipeline();
        waitForStart();
        setHeight(duckPipeline.getLevel());
        drive(0, -1, 0, 0.7);
        sleep(530);
        drive(0, 0, 0, 0);
        pour();
        sleep(700);
        noPour();
        resetPosition(0, 0, 0);
        setHeight(0);
        move_to(-1, 45, 0, 1);
        move_to(15, 45, 0, 1);
    }
}
