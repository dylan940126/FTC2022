package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.mods.DriverBase;

@Config
@Autonomous
public class MovingTest extends DriverBase {
    public static double x = 0, y = 0, dir = 0;

    @Override
    public void runOpMode() {
        init_Devices();
        waitForStart();
        while (opModeIsActive())
            while (gamepad1.a)
                move_to(x, y, dir, 1);
    }

    ;
}

