package org.firstinspires.ftc.team11047;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team11047.custommodules.Robot;
import org.firstinspires.ftc.team11047.custommodules.MyMath;

@Autonomous
public class RedZone extends Robot {
    double time = 0, last_y = 50;
    int red = 1;

    @Override
    public void runOpMode() {
        initRobot();
        switchToDockPipeline();
        while (!isStarted()) {
            telemetry.addData("position", duckPipeline.getLevel());
            telemetry.update();
        }
        resetStartTime();
        setHeight(duckPipeline.getLevel());
        switchToGoalPipeline();
        if (red == 1)
            hubPipeline.setToRed();
        else
            hubPipeline.setToBlue();
        resetPosition(0, 23, 0);
        for (int cycle = 0; opModeIsActive(); ++cycle) {
//            going to aim
            while (opModeIsActive() && current_y > 5)
                drive(0.1 * red, -1, 0.1 * red, 1);
            while (opModeIsActive() && !hubPipeline.isDetected())
                drive(0.1 * red, -1, 0.1 * red, 0.5);
            drive(0, 0, 0, 0);
//            aim and pour
            for (int check = 0; check < 20; ++check) {
                while (opModeIsActive() && (Math.abs(hubPipeline.getX()) > 20 || !isPourable()))
                    drive(0.1 * Math.abs(hubPipeline.getX()) * red, hubPipeline.getX() * red, 0,
                            1.1 * Math.abs(MyMath.distanceToPower(hubPipeline.getX()) / 30));
            }
            drive(0, 0, 0, 0);
            pour();
            sleep(500);
            noPour();
            resetPosition(0, 0, 0);
            backFlow();
//            going to collect
            while (opModeIsActive() && current_y < last_y) {
                drive(0.1 * red, 1, -0.1 * red,
                        MyMath.distanceToPower(last_y - current_y) / 4);
                setHeight(0);
                collect();
            }
            drive(0, 0, 0, 0);
            resetPosition(0, current_y, 0);
            if (cycle % 2 == 1)
                while (opModeIsActive() && !isCarry() && Math.abs(0.8 * red - current_direction) > 0.1)
                    drive(0, 0.3,
                            0.8 * red - current_direction,
                            Math.abs(0.8 * red - current_direction));
            time = getRuntime();
            while (opModeIsActive() && !isCarry()) {
                if ((getRuntime() - time) % 1 < 0.5)
                    drive(0, -1, 0, 0.05);
                else
                    drive(0, 1,
                            0, 0.2);
            }
            last_y = current_y;
            backFlow();
            time = getRuntime();
            while (opModeIsActive() && (current_x * red < 0 || getRuntime() - time < 0.5))
                move(1 * red, -1, (0.2 * red - current_direction) * 1, 1);

            if (getRuntime() > 26)
                break;
            setHeight(3);
            backFlow();
        }
    }
}
