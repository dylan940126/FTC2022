package org.firstinspires.ftc.team11047;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous
public class BlueZone extends RedZone {

    @Override
    public void runOpMode() {
        red = -1;
        super.runOpMode();
    }
}
