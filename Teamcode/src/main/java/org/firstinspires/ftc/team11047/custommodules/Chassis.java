package org.firstinspires.ftc.team11047.custommodules;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Config
public abstract class Chassis extends LinearOpMode {
    public Wheel lf, lb, rf, rb;
    public ModernRoboticsI2cGyro gyro;

    public double max_accelerate = 50,
            terminate_power = 0,
            code_per_inch_right = 55,
            code_per_inch_forward = 42.6,
            turn_weight = 10,
            trace_kp = 0.2;
    public int buffer_size = 2;
    private double last_refresh_time, loop_time = 0;
    public double current_x, current_y, current_direction, target_x, target_y, target_direction;
    private int last_direction;

    public void initChassis() {
        lf = new Wheel(hardwareMap.dcMotor.get("lf"), DcMotor.Direction.REVERSE);
        lb = new Wheel(hardwareMap.dcMotor.get("lb"), DcMotor.Direction.REVERSE);
        rf = new Wheel(hardwareMap.dcMotor.get("rf"), DcMotor.Direction.FORWARD);
        rb = new Wheel(hardwareMap.dcMotor.get("rb"), DcMotor.Direction.FORWARD);
        gyro = hardwareMap.get(ModernRoboticsI2cGyro.class, "gyro");
        gyro.calibrate();
        last_direction = gyro.getHeading();
        last_refresh_time = getRuntime();
        while (!gyro.isCalibrating()) ;
    }

    public void drive(double right_speed, double forward_speed, double turn_speed, double speed) {
        double forwardLeft_Power = right_speed + forward_speed - turn_speed;
        double rearLeft_Power = -right_speed + forward_speed - turn_speed;
        double forwardRight_Power = -right_speed + forward_speed + turn_speed;
        double rearRight_Power = right_speed + forward_speed + turn_speed;
        if (speed > 1)
            speed = 1;
        else if (speed < 0)
            speed = 0;
        double k = speed / Math.max(Math.abs(forwardLeft_Power),
                Math.max(Math.abs(rearLeft_Power),
                        Math.max(Math.abs(forwardRight_Power),
                                Math.abs(rearRight_Power))));
        lf.setPower(forwardLeft_Power * k);
        lb.setPower(rearLeft_Power * k);
        rf.setPower(forwardRight_Power * k);
        rb.setPower(rearRight_Power * k);
    }

    public void move(double x_Speed, double y_Speed, double turnSpeed, double speed) {
        double rightSpeed = x_Speed * Math.cos(current_direction) + y_Speed * Math.sin(current_direction);
        double forwardSpeed = -x_Speed * Math.sin(current_direction) + y_Speed * Math.cos(current_direction);
        drive(rightSpeed, forwardSpeed, turnSpeed, speed);
    }

    public interface Period {
        void execute();
    }

    public void move_to(double target_X, double target_Y, double target_Direction, double maxSpeed) {
        move_to(target_X, target_Y, target_Direction, maxSpeed, 0, null);
    }

    public void move_to(double target_X, double target_Y, double target_Direction, double maxSpeed, double quit, Period period) {
        double total_X = target_X - current_x;
        double total_Y = target_Y - current_y;
        double total_Direction = target_Direction - current_direction;
        double distance = Math.sqrt(total_X * total_X + total_Y * total_Y + total_Direction * total_Direction * turn_weight * turn_weight);
        double startTime = getRuntime();
        double totalTime = Math.min(Math.sqrt(distance / max_accelerate / 2), distance / maxSpeed / 2) * Math.PI;
        double incomplete_Rate = 0;
        double error = distance;
        if (period == null)
            period = () -> {
            };
        while (opModeIsActive()
                && (getRuntime() - startTime) < totalTime + 1
                && (incomplete_Rate != 0 || error > quit)) {
            period.execute();
            incomplete_Rate = (getRuntime() - startTime) <= totalTime ? (Math.cos(Math.PI * (getRuntime() - startTime) / totalTime) + 1) / 2 : 0;
            this.target_x = target_X - total_X * incomplete_Rate;
            this.target_y = target_Y - total_Y * incomplete_Rate;
            this.target_direction = target_Direction - total_Direction * incomplete_Rate;
            double error_X = this.target_x - current_x;
            double error_Y = this.target_y - current_y;
            double error_direction = this.target_direction - current_direction;
            error_direction *= turn_weight;
            error = Math.sqrt(error_X * error_X + error_Y * error_Y + error_direction * error_direction);
            move(error_X, error_Y / code_per_inch_forward * code_per_inch_right, error_direction,
                    MyMath.distanceToPower(error) * trace_kp);
        }
        lf.setPower(0);
        lb.setPower(0);
        rf.setPower(0);
        rb.setPower(0);
    }

    public void resetPosition(double x, double y, double direction) {
        current_x = x;
        current_y = y;
        current_direction = direction;
    }

    public void refreshPosition() {
        loop_time = getRuntime() - last_refresh_time;
        last_refresh_time = getRuntime();
        lf.refresh();
        lb.refresh();
        rf.refresh();
        rb.refresh();
        int now_direction = gyro.getHeading();

        double d_right = (lf.getSpeed() - lb.getSpeed() - rf.getSpeed() + rb.getSpeed()) / 4 * loop_time / code_per_inch_right;
        double d_forward = (lf.getSpeed() + lb.getSpeed() + rf.getSpeed() + rb.getSpeed()) / 4 * loop_time / code_per_inch_forward;
        double d_turn = now_direction - last_direction;
        if (d_turn < -180)
            d_turn += 360;
        else if (d_turn > 180)
            d_turn -= 360;
        last_direction = now_direction;
        current_x += d_right * Math.cos(current_direction) - d_forward * Math.sin(current_direction);
        current_y += d_right * Math.sin(current_direction) + d_forward * Math.cos(current_direction);
        current_direction += Math.toRadians(d_turn);
        telemetry.addData("currentX", current_x);
        telemetry.addData("currentY", current_y);
        telemetry.addData("currentDirection", current_direction);
    }

    public class Wheel {
        private final DcMotor wheel;
        private double currentSpeed, previousSpeed;
        private final int[] position = new int[buffer_size];
        private int index = -1;

        public Wheel(DcMotor wheel, DcMotorSimple.Direction direction) {
            this.wheel = wheel;
            wheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            wheel.setDirection(direction);
            wheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            wheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            reset();
        }

        public void reset() {
            last_refresh_time = getRuntime();
            int temp = wheel.getCurrentPosition();
            for (int i = 0; i < buffer_size; ++i)
                position[i] = temp;
            currentSpeed = previousSpeed = 0;
        }

        public void refresh() {
            index = (index + 1) % buffer_size;
            position[index] = wheel.getCurrentPosition();
            previousSpeed = currentSpeed;
            currentSpeed = (position[index] - position[(index + 1) % buffer_size]) / loop_time / (buffer_size - 1);
        }

        public void setPower(double power) {
            wheel.setPower(Math.abs(power) <= terminate_power ? 0 : power);
        }

        public int getPosition() {
            return position[index];
        }

        public double getSpeed() {
            return currentSpeed;
        }

        public double getAcceleration() {
            return (currentSpeed - previousSpeed) / loop_time;
        }
    }
}
