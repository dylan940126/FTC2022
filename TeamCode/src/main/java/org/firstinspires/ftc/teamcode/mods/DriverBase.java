package org.firstinspires.ftc.teamcode.mods;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Config
public abstract class DriverBase extends LinearOpMode {
    public double max_Acceleration = 0.05, critical_Speed = 0.04;
    private double current_X, current_Y, current_Direction;
    protected Wheel forward_Left, rear_Left, forward_Right, rear_Right;
    protected DcMotor left_Raise, right_Raise, turn_Raise, suck;
    protected FtcDashboard dashboard;
    protected TelemetryPacket packet;

    public void init_Devices() {
        dashboard = FtcDashboard.getInstance();
        packet = new TelemetryPacket();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
        forward_Left = new Wheel(hardwareMap.dcMotor.get("lf"), DcMotor.Direction.FORWARD);
        rear_Left = new Wheel(hardwareMap.dcMotor.get("lb"), DcMotor.Direction.FORWARD);
        forward_Right = new Wheel(hardwareMap.dcMotor.get("rf"), DcMotor.Direction.REVERSE);
        rear_Right = new Wheel(hardwareMap.dcMotor.get("rb"), DcMotor.Direction.REVERSE);
        reset_Position();
        left_Raise = hardwareMap.dcMotor.get("lraise");
        left_Raise.setDirection(DcMotor.Direction.REVERSE);
        left_Raise.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right_Raise = hardwareMap.dcMotor.get("rraise");
        right_Raise.setDirection(DcMotor.Direction.FORWARD);
        right_Raise.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right_Raise.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right_Raise.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turn_Raise = hardwareMap.dcMotor.get("traise");
        turn_Raise.setDirection(DcMotor.Direction.FORWARD);
        turn_Raise.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        turn_Raise.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turn_Raise.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        suck = hardwareMap.dcMotor.get("suck");
        suck.setDirection(DcMotor.Direction.FORWARD);
        suck.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public void drive(double rightSpeed, double forwardSpeed, double turnSpeed, double speed) {
        double forwardLeft_Power = rightSpeed + forwardSpeed - turnSpeed;
        double rearLeft_Power = -rightSpeed + forwardSpeed - turnSpeed;
        double forwardRight_Power = -rightSpeed + forwardSpeed + turnSpeed;
        double rearRight_Power = rightSpeed + forwardSpeed + turnSpeed;
        if (speed > 1)
            speed = 1;
        else if (speed < 0)
            speed = 0;
        double k = speed / Math.max(Math.abs(forwardLeft_Power),
                Math.max(Math.abs(rearLeft_Power),
                        Math.max(Math.abs(forwardRight_Power),
                                Math.abs(rearRight_Power))));
        forward_Left.setPower(forwardLeft_Power * k);
        rear_Left.setPower(rearLeft_Power * k);
        forward_Right.setPower(forwardRight_Power * k);
        rear_Right.setPower(rearRight_Power * k);
        refresh_Position();
    }

    public void move(double x_Speed, double y_Speed, double turnSpeed, double speed) {
        double dir_temp = (current_Direction + turnSpeed / 2) * Math.PI / 180;
        double rightSpeed = x_Speed * Math.cos(dir_temp) + y_Speed * Math.sin(dir_temp);
        double forwardSpeed = -x_Speed * Math.sin(dir_temp) + y_Speed * Math.cos(dir_temp);
        drive(rightSpeed, forwardSpeed, turnSpeed, speed);
    }

    public void move_to(double target_X, double target_Y, double target_Direction, double maxSpeed) {
        double total_X = target_X - current_X;
        double total_Y = target_Y - current_Y;
        double total_Direction = target_Direction - current_Direction;
        double distance = Math.hypot(total_X, total_Y);
        double startTime = getRuntime();
        double totalTime = Math.min(Math.sqrt(distance / max_Acceleration / 2), distance / maxSpeed / 2) * Math.PI;
        while (opModeIsActive() && (getRuntime() - startTime) < totalTime + 2) {
            double incomplete_Rate = (Math.PI / totalTime * Math.cos((getRuntime() - startTime)) + 1) / 2;
            double error_X = target_X - total_X * incomplete_Rate - current_X;
            double error_Y = target_Y - total_Y * incomplete_Rate - current_Y;
            double error_direction = target_Direction - total_Direction * incomplete_Rate - current_Direction;
            double speed = Math.hypot(error_X, error_Y);
            move(error_X, error_Y, error_direction * 0.08, speed * 0.08);
            telemetry.addData("error_X", error_X);
            telemetry.addData("error_Y", error_Y);
            telemetry.addData("error_direction", error_direction);
            telemetry.addData("speed", speed);
        }
        forward_Left.setPower(0);
        rear_Left.setPower(0);
        forward_Right.setPower(0);
        rear_Right.setPower(0);
    }

    public void reset_Position() {
        current_X = 0;
        current_Y = 0;
        current_Direction = 0;
    }

    private void refresh_Position() {
        forward_Left.refresh();
        rear_Left.refresh();
        forward_Right.refresh();
        rear_Right.refresh();
        double rightSpeed = (forward_Left.getSpeed() - rear_Left.getSpeed() - forward_Right.getSpeed() + rear_Right.getSpeed()) / 244.5;
        double forwardSpeed = (forward_Left.getSpeed() + rear_Left.getSpeed() + forward_Right.getSpeed() + rear_Right.getSpeed()) / 224.663;
        double turnSpeed = (-forward_Left.getSpeed() - rear_Left.getSpeed() + forward_Right.getSpeed() + rear_Right.getSpeed()) / 39.655;
        double dir_temp = (current_Direction + turnSpeed / 2) * Math.PI / 180;
        current_X += rightSpeed * Math.cos(dir_temp) - forwardSpeed * Math.sin(dir_temp);
        current_Y += rightSpeed * Math.sin(dir_temp) + forwardSpeed * Math.cos(dir_temp);
        current_Direction += turnSpeed;
        packet.put("currentX", current_X);
        packet.put("currentY", current_Y);
        packet.put("currentDirection", current_Direction);
        packet.put("rightSpeed", rightSpeed);
        packet.put("forwardSpeed", forwardSpeed);
        packet.put("turnSpeed", turnSpeed);
        dashboard.sendTelemetryPacket(packet);
    }

    public class Wheel {
        private final DcMotor wheel;
        private int currentPosition, previousPosition, speed;

        public Wheel(DcMotor wheel, DcMotorSimple.Direction direction) {
            this.wheel = wheel;
            wheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            wheel.setDirection(direction);
            wheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            reset();
        }

        public void reset() {
            previousPosition = currentPosition = getPosition();
            speed = 0;
        }

        public void refresh() {
            previousPosition = currentPosition;
            currentPosition = getPosition();
            speed = currentPosition - previousPosition;
        }

        public void setPower(double power) {
            wheel.setPower(Math.abs(power) < critical_Speed ? 0 : power);
        }

        public int getPosition() {
            return wheel.getCurrentPosition();
        }

        public double getSpeed() {
            return speed;
        }
    }
}
