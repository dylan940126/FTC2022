package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ServoTest extends OpMode {
    private Servo servo;

    @Override
    public void init() {
        servo = hardwareMap.get(Servo.class, "s1");
    }

    @Override
    public void loop() {
        if (gamepad1.left_bumper)
            servo.setPosition(0);
        else if (gamepad1.right_bumper)
            servo.setPosition(1);
    }
}
