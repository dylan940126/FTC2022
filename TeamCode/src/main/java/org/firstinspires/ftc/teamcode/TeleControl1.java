package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.custommodules.DriverBase;
import org.firstinspires.ftc.teamcode.custommodules.Switch;
import org.firstinspires.ftc.teamcode.custommodules.TwoSwitchs;

@Config
@TeleOp
public class TeleControl1 extends LinearOpMode {
    public boolean direction = false;
    public Switch gpa;
    public TwoSwitchs raise_level;
    public DriverBase driverBase;

    @Override
    public void runOpMode() {
        driverBase = new DriverBase(this);
        driverBase.initDevices();
        gpa = new Switch(false);
        raise_level = new TwoSwitchs(3, 0);
        driverBase.initDevices();
        waitForStart();
        while (opModeIsActive()) {
//            底盤1
            double d_forward, d_right, d_turn;
            d_forward = -gamepad1.left_stick_y * (raise_level.getLevel() == 0 ? 0.7 : 0.3);
            d_right = gamepad1.left_stick_x * (raise_level.getLevel() == 0 ? 0.7 : 0.3);
            d_turn = -gamepad1.right_stick_x * (raise_level.getLevel() == 0 ? 0.7 : 0.3);
            driverBase.chassis.drive(d_right, d_forward, d_turn, Math.max(Math.max(Math.abs(d_forward), Math.abs(d_right)), Math.abs(d_turn)));
//            歸零
            if (gamepad1.b)
                driverBase.chassis.resetPosition(0, 0, 0);
//            吸球2
            gpa.refresh(gamepad1.a);
            if (driverBase.turntable.isCarry())
                gpa.reset(false);
            if (gamepad1.y)
                driverBase.turntable.backFlow();
            else if (gpa.isOn())
                driverBase.turntable.Collect();
            else driverBase.turntable.stopCollect();
//            轉盤1
            if (gamepad1.left_bumper)
                direction = false;
            if (gamepad1.right_bumper)
                direction = true;
            driverBase.turntable.setDirection(direction);
//            手臂2
            raise_level.refresh(gamepad1.dpad_up, gamepad1.dpad_down);
            driverBase.turntable.setHeight(raise_level.getLevel());
//            伸縮2
            driverBase.turntable.setExtendSpeed(-gamepad1.right_stick_y);
//            傾倒1
            driverBase.turntable.setContainer(gamepad1.x);
        }
    }


}
