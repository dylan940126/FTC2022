package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mods.Chassis;

@Config
@TeleOp
public class Telecontrol extends Chassis {
    public static double p = 0.01, i = 0.01, de_i = 0.5, d = 0.01;
    private double r_suck;
    private boolean l_gpa;
    private boolean l_gpb;
    private int r_turn_pos, r_height_pos;
    public static double x = 0, y = 0, dir = 0;

    @Override
    public void runOpMode() {
        init_Devices();
        waitForStart();
        while (opModeIsActive()) {
            double d_forward, d_right, d_turn;
            d_forward = -gamepad1.left_stick_y * 0.7;
            d_right = gamepad1.left_stick_x * 0.7;
            d_turn = -gamepad1.right_stick_x * 0.5;

            boolean gpa = gamepad2.a;
            boolean gpb = gamepad2.b;
            if (gpa && !l_gpa)
                r_suck = r_suck > 0 ? 0 : 0.5;
            else if (gpb && !l_gpb)
                r_suck = r_suck < 0 ? 0 : -0.3;
            l_gpa = gpa;
            l_gpb = gpb;

            double r_high;
            if (gamepad2.dpad_up)
                r_high = 0.3;
            else if (gamepad2.dpad_down)
                r_high = -0.3;
            else
                r_high = 0;

            double r_turn;
            if (r_turn_pos != 0)
                if (gamepad2.left_bumper)
                    r_turn = 0.15;
                else if (gamepad2.right_bumper)
                    r_turn = -0.15;
                else
                    r_turn = 0;
            else
                r_turn = (r_turn_pos - turn_Raise.getCurrentPosition()) / 200.0;

            left_Raise.setPower(r_high);
            right_Raise.setPower(r_high);
            turn_Raise.setPower(r_turn);
            suck.setPower(r_suck);
            drive(d_right, d_forward, d_turn, Math.max(Math.max(Math.abs(d_forward), Math.abs(d_right)), Math.abs(d_turn)));
        }
    }
}
