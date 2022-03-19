package org.firstinspires.ftc.teamcode.custommodules;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@Config
public class Chassis extends Thread {
    public final Wheel lf, lb, rf, rb;
    public final BNO055IMU imu;

    public static double max_accelerate = 100,
            terminate_power = 0,
            code_per_inch_right = 55.387,
            code_per_inch_forward = 50,
            turn_weight = 10,
            k_turn = 0.99580306216888673256011948485903,
            trace_kp = 0.18;
    private double last_refresh_time, loop_time = 0, current_x, current_y, current_direction;
    private Orientation last_direction;
    private final LinearOpMode opMode;

    public Chassis(LinearOpMode opMode) {
        this.opMode = opMode;
        lf = new Wheel(opMode.hardwareMap.dcMotor.get("lf"), DcMotor.Direction.REVERSE);
        lb = new Wheel(opMode.hardwareMap.dcMotor.get("lb"), DcMotor.Direction.REVERSE);
        rf = new Wheel(opMode.hardwareMap.dcMotor.get("rf"), DcMotor.Direction.FORWARD);
        rb = new Wheel(opMode.hardwareMap.dcMotor.get("rb"), DcMotor.Direction.FORWARD);
        imu = opMode.hardwareMap.get(BNO055IMU.class, "imu 1");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
        parameters.loggingEnabled = false;
        imu.initialize(parameters);
        last_direction = imu.getAngularOrientation();
        last_refresh_time = opMode.getRuntime();
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
        refreshPosition();
    }

    public void move(double x_Speed, double y_Speed, double turnSpeed, double speed) {
        double rightSpeed = x_Speed * Math.cos(current_direction) + y_Speed * Math.sin(current_direction);
        double forwardSpeed = -x_Speed * Math.sin(current_direction) + y_Speed * Math.cos(current_direction);
        drive(rightSpeed, forwardSpeed, turnSpeed, speed);
    }

    public void move_to(double target_X, double target_Y, double target_Direction, double maxSpeed) {
        double total_X = target_X - current_x;
        double total_Y = target_Y - current_y;
        double total_Direction = target_Direction - current_direction;
        double distance = Math.sqrt(total_X * total_X + total_Y * total_Y + total_Direction * total_Direction * turn_weight * turn_weight);
        double startTime = opMode.getRuntime();
        double totalTime = Math.min(Math.sqrt(distance / max_accelerate / 2), distance / maxSpeed / 2) * Math.PI;
        double incomplete_Rate = 0;
        double error = distance;
        while (opMode.opModeIsActive() && (opMode.getRuntime() - startTime) < totalTime + 1.5 && (incomplete_Rate > 0 || error > 1)) {
            incomplete_Rate = (opMode.getRuntime() - startTime) <= totalTime ? (Math.cos(Math.PI * (opMode.getRuntime() - startTime) / totalTime) + 1) / 2 : 0;
            double error_X = target_X - total_X * incomplete_Rate - current_x;
            double error_Y = target_Y - total_Y * incomplete_Rate - current_y;
            double error_direction = target_Direction - total_Direction * incomplete_Rate - current_direction;
            error_direction *= turn_weight;
            error = Math.sqrt(error_X * error_X + error_Y * error_Y + error_direction * error_direction);
            move(error_X, error_Y, error_direction, MyMath.distanceToPower(error) * trace_kp);
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
        loop_time = opMode.getRuntime() - last_refresh_time;
        last_refresh_time = opMode.getRuntime();
        lf.refresh();
        lb.refresh();
        rf.refresh();
        rb.refresh();
        Orientation now_direction = imu.getAngularOrientation();
        double d_right = (lf.getSpeed() - lb.getSpeed() - rf.getSpeed() + rb.getSpeed()) / 4 * loop_time / code_per_inch_right;
        double d_forward = (lf.getSpeed() + lb.getSpeed() + rf.getSpeed() + rb.getSpeed()) / 4 * loop_time / code_per_inch_forward;
        double d_turn = (now_direction.firstAngle - last_direction.firstAngle) * k_turn;
        if (d_turn < -Math.PI)
            d_turn += 2 * Math.PI;
        else if (d_turn > Math.PI)
            d_turn -= 2 * Math.PI;
        last_direction = now_direction;
        current_direction += d_turn;
        current_x += d_right * Math.cos(current_direction) - d_forward * Math.sin(current_direction);
        current_y += d_right * Math.sin(current_direction) + d_forward * Math.cos(current_direction);
        opMode.telemetry.addData("currentX", current_x);
        opMode.telemetry.addData("currentY", current_y);
        opMode.telemetry.addData("currentDirection", current_direction);
        opMode.telemetry.update();
    }

    public class Wheel {
        private final DcMotor wheel;
        public int currentPosition, previousPosition;
        private double currentSpeed, previousSpeed;

        public Wheel(DcMotor wheel, DcMotorSimple.Direction direction) {
            this.wheel = wheel;
            wheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            wheel.setDirection(direction);
            wheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            wheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            reset();
        }

        public void reset() {
            last_refresh_time = opMode.getRuntime();
            previousPosition = currentPosition = getPosition();
        }

        public void refresh() {
            previousPosition = currentPosition;
            previousSpeed = currentSpeed;
            currentPosition = wheel.getCurrentPosition();
            currentSpeed = (currentPosition - previousPosition) / loop_time;
        }

        public void setPower(double power) {
            wheel.setPower(Math.abs(power) <= terminate_power ? 0 : power);
        }

        public int getPosition() {
            return currentPosition;
        }

        public double getSpeed() {
            return currentSpeed;
        }

        public double getAcceleration() {
            return (currentSpeed - previousSpeed) / loop_time;
        }
    }
}
