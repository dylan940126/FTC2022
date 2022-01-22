package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mods.DriverBase;

@Config
@TeleOp
public class TeleControl extends DriverBase {
    public boolean direction = false;
    public Switcher gpa;
    public TwoSwitchers raise_level;

    @Override
    public void runOpMode() {
        gpa = new Switcher(gamepad1.a, false);
        raise_level = new TwoSwitchers(gamepad1.dpad_up, gamepad1.dpad_down, 2, 0);
        initDevices();
        waitForStart();
        while (opModeIsActive()) {
//            底盤
            double d_forward, d_right, d_turn;
            d_forward = -gamepad1.left_stick_y * 0.7;
            d_right = gamepad1.left_stick_x * 0.7;
            d_turn = -gamepad1.right_stick_x * 0.5;
            chassis.drive(d_right, d_forward, d_turn, Math.max(Math.max(Math.abs(d_forward), Math.abs(d_right)), Math.abs(d_turn)));
//            吸球
            gpa.refresh(gamepad1.a);
            if (gamepad1.y)
                turntable.backFlow();
            else if (gpa.isOn())
                turntable.Collect();
            else turntable.stopCollect();
//            轉盤
            if (gamepad1.left_bumper)
                direction = false;
            if (gamepad1.right_bumper)
                direction = true;
            turntable.setDirection(direction);
//            手臂
            turntable.setHeight(raise_level.getLevel(gamepad1.dpad_up, gamepad1.dpad_down));
//            伸縮
            turntable.setExtendSpeed(gamepad1.right_stick_y);
//            傾倒
            turntable.setContainer(gamepad1.x);
        }
    }


}
