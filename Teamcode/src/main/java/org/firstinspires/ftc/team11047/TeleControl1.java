package org.firstinspires.ftc.team11047;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.team11047.custommodules.DriverBase;
import org.firstinspires.ftc.team11047.custommodules.MyMath;
import org.firstinspires.ftc.team11047.custommodules.Switch;

@Config
@TeleOp
public class TeleControl1 extends LinearOpMode {
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
            if (gamepad1.left_stick_button)
                driverBase.chassis.resetPosition(0, 0, 0);
//            吸球2
            take.refresh(gamepad1.a);
            if (gamepad1.y || driverBase.turntable.isCarry())
                driverBase.turntable.backFlow();
            else if (take.isOn() && driverBase.turntable.isCollectable())
                driverBase.turntable.collect();
            else driverBase.turntable.stopCollect();
//            傾倒1
            if (gamepad1.b)
                driverBase.turntable.pour();
            else
                driverBase.turntable.noPour();
//            鴨子
            if (gamepad1.start)
                driverBase.turntable.setSpinnerRed();
            else if (gamepad1.back)
                driverBase.turntable.setSpinnerBlue();
            else {
                duck_power = getRuntime();
                driverBase.turntable.setSpinnerStop();
            }
//            手臂2
            d_forward = 0;
            raise_level.refresh(gamepad1.left_bumper);
            if (height == -1 && gamepad1.right_trigger != 0) {
                driverBase.turntable.setExtendSpeed(-gamepad1.left_stick_y * 0.3);
            } else if (gamepad1.left_bumper) {
                height = 4;
                d_forward = MyMath.distanceToPower(driverBase.hubPipeline.getX()) / 30;
                driverBase.turntable.setExtendSpeed(1);
            } else if (gamepad1.right_bumper || height == 0)
                height = 0;
            else {
                height = -1;
                driverBase.turntable.setExtendSpeed(0);
            }
            driverBase.turntable.setHeight(height);
//            底盤1
            d_forward += -gamepad1.left_stick_y;
            d_right = gamepad1.left_stick_x;
            d_turn = -gamepad1.right_stick_x;
            driverBase.chassis.drive(d_right, d_forward, d_turn, Math.max(Math.max(Math.abs(d_forward), Math.abs(d_right)), Math.abs(d_turn))
                    * ((height * gamepad1.right_trigger) == 0 ? 0.8 : 0.5));
//            杯子
            driverBase.turntable.shipPosition(gamepad1.right_trigger);
//            旋轉盤
//            driverBase.turntable.setTurn(-gamepad2.left_stick_x * 0.05);
//            if (gamepad1.dpad_left)
//                driverBase.turntable.setTurn(0.3);
//            else if (gamepad1.dpad_right)
//                driverBase.turntable.setTurn(-0.3);
//            else
//                driverBase.turntable.setTurn(0);
//            歸零
            if (gamepad1.x)
                driverBase.turntable.resetRaise();
        }
    }
}
