package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.custommodules.DriverBase;
import org.firstinspires.ftc.teamcode.custommodules.Switch;

@Config
@TeleOp
public class TeleControl2 extends LinearOpMode {
    int height = 0;
    public double d_forward, d_right, d_turn, duck_power;
    public Switch take;
    public Switch raise_level;
    public DriverBase driverBase;

    @Override
    public void runOpMode() {
        driverBase = new DriverBase(this);
        driverBase.initDevices();
        take = new Switch(false);
        raise_level = new Switch(false);
        driverBase.switchToGoalPipeline();
        waitForStart();
        while (opModeIsActive()) {
//            歸零
            if (gamepad1.b)
                driverBase.chassis.resetPosition(0, 0, 0);
//            吸球2
            take.refresh(gamepad1.right_bumper);
            if (gamepad1.left_bumper || driverBase.turntable.isCarry())
                driverBase.turntable.backFlow();
            else if (take.isOn() && driverBase.turntable.isCollectable())
                driverBase.turntable.collect();
            else driverBase.turntable.stopCollect();
//            傾倒1
            if (gamepad2.a)
                driverBase.turntable.pour();
            else
                driverBase.turntable.noPour();
//            鴨子
            if (gamepad2.b)
                driverBase.turntable.setSpinner(Math.min(getRuntime() - duck_power, 1) * 0.4);
            else if (gamepad2.x)
                driverBase.turntable.setSpinner(Math.min(getRuntime() - duck_power, 1) * -0.4);
            else {
                duck_power = getRuntime();
                driverBase.turntable.setSpinner(0);
            }
//            手臂2
            raise_level.refresh(gamepad2.left_bumper);
            if (height == -1 && gamepad2.right_trigger != 0) {
                driverBase.turntable.setExtendSpeed(-gamepad2.left_stick_y * 0.3);
            } else if (gamepad2.left_bumper) {
                height = 4;
                driverBase.turntable.setExtendSpeed(1);
            } else if (gamepad2.right_bumper || height == 0)
                height = 0;
            else {
                height = -1;
                driverBase.turntable.setExtendSpeed(0);
            }
            driverBase.turntable.setHeight(height);
//            底盤1
            d_forward = -gamepad1.left_stick_y;
            d_right = gamepad1.left_stick_x;
            d_turn = -gamepad1.right_stick_x;
            driverBase.chassis.drive(d_right, d_forward, d_turn, Math.max(Math.max(Math.abs(d_forward), Math.abs(d_right)), Math.abs(d_turn))
                    * ((height * gamepad2.right_trigger) == 0 ? 0.8 : 0.5));
//            杯子
            driverBase.turntable.shipPosition(gamepad2.right_trigger);
//            旋轉盤
//            driverBase.turntable.setTurn(-gamepad2.left_stick_x * 0.05);
//            if (gamepad1.dpad_left)
//                driverBase.turntable.setTurn(0.3);
//            else if (gamepad1.dpad_right)
//                driverBase.turntable.setTurn(-0.3);
//            else
//                driverBase.turntable.setTurn(0);
//            歸零
            if (gamepad2.y)
                driverBase.turntable.resetRaise();
        }
    }
}
