package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@Config
@TeleOp
public class ServoTest extends OpMode {
    public static double position = 0;
    private Servo servo;

    @Override
    public void init() {
        servo = hardwareMap.get(Servo.class, "ship");
    }

    @Override
    public void loop() {
        servo.setPosition(position);
    }
}
