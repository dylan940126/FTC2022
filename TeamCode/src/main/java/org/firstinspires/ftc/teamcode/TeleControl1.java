package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.custommodules.DriverBase;
import org.firstinspires.ftc.teamcode.custommodules.MyMath;
import org.firstinspires.ftc.teamcode.custommodules.Switch;

@Config
@TeleOp
public class TeleControl1 extends LinearOpMode {
    int height = 0;
    public boolean direction = false;
    public double d_forward, d_right, d_turn;
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
            take.refresh(gamepad1.a);
            if (gamepad1.b || driverBase.turntable.isCarry())
                driverBase.turntable.backFlow();
            else if (take.isOn() && driverBase.turntable.isCollectable())
                driverBase.turntable.collect();
            else driverBase.turntable.stopCollect();
//            傾倒1
            if (gamepad1.right_bumper)
                driverBase.turntable.pour();
            else
                driverBase.turntable.noPour();
//            手臂2
            raise_level.refresh(gamepad1.left_bumper);
            if (raise_level.isJustPress())
                switch (height) {
                    case 0:
                    case 2:
                        height = 3;
                        break;
                    case 3:
                        height = 2;
                        break;
                }
            else if (gamepad1.left_trigger > 0.5) {
                take.reset(true);
                height = 0;
            }
            if (driverBase.turntable.isCarry())
                take.reset(false);
            driverBase.turntable.setHeight(height);
            if (height != 0)
                d_forward = MyMath.distanceToPower(driverBase.redGoalPipeline.getX()) / 70;
            else
                d_forward = 0;
//            底盤1
            d_forward += -gamepad1.left_stick_y * (height == 0 ? 0.8 : 0.5);
            d_right = gamepad1.left_stick_x * (height == 0 ? 0.8 : 0.5);
            d_turn = -gamepad1.right_stick_x * (height == 0 ? 0.5 : 0.3);
            driverBase.chassis.drive(d_right, d_forward, d_turn, Math.max(Math.max(Math.abs(d_forward), Math.abs(d_right)), Math.abs(d_turn)));
//            鴨子
            driverBase.turntable.setSpinner(gamepad1.right_trigger - gamepad1.left_trigger);
        }
    }


}
