package org.firstinspires.ftc.teamcode.mods;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.concurrent.TimeUnit;

public abstract class DriverBase extends LinearOpMode {
    public Chassis chassis;
    public Turntable turntable;
    public FtcDashboard dashboard;
    public WebcamName webcam_Name;
    public OpenCvWebcam webcam;

    public void initDevices() {
        chassis = new Chassis();
        turntable = new Turntable();
        initDashboard();
//        init_Camera();
    }

    public void initDashboard() {
        dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
    }

    public void init_Camera() {
        webcam_Name = hardwareMap.get(WebcamName.class, "Webcam 1");
        webcam = OpenCvCameraFactory.getInstance().createWebcam(webcam_Name);
        webcam.openCameraDevice();
        webcam.getExposureControl().setMode(ExposureControl.Mode.Manual);
        webcam.getExposureControl().setExposure(30000, TimeUnit.MICROSECONDS);
        webcam.setViewportRenderer(OpenCvCamera.ViewportRenderer.GPU_ACCELERATED);
    }

    public class Chassis {
        private double max_acceleration, critical_speed;
        private Wheel forward_left, rear_left, forward_right, rear_right;
        private double current_x, current_y, current_direction;

        public Chassis() {
            max_acceleration = 25;
            critical_speed = 0.04;
            forward_left = new Wheel(hardwareMap.dcMotor.get("lf"), DcMotor.Direction.FORWARD);
            rear_left = new Wheel(hardwareMap.dcMotor.get("lb"), DcMotor.Direction.FORWARD);
            forward_right = new Wheel(hardwareMap.dcMotor.get("rf"), DcMotor.Direction.REVERSE);
            rear_right = new Wheel(hardwareMap.dcMotor.get("rb"), DcMotor.Direction.REVERSE);
            resetPosition();
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
            forward_left.setPower(forwardLeft_Power * k);
            rear_left.setPower(rearLeft_Power * k);
            forward_right.setPower(forwardRight_Power * k);
            rear_right.setPower(rearRight_Power * k);
            refreshPosition();
        }

        public void move(double x_Speed, double y_Speed, double turnSpeed, double speed) {
            double dir_temp = (current_direction + turnSpeed / 2) * Math.PI / 180;
            double rightSpeed = x_Speed * Math.cos(dir_temp) + y_Speed * Math.sin(dir_temp);
            double forwardSpeed = -x_Speed * Math.sin(dir_temp) + y_Speed * Math.cos(dir_temp);
            drive(rightSpeed, forwardSpeed, turnSpeed, speed);
        }

        public void move_to(double target_X, double target_Y, double target_Direction, double maxSpeed) {
            double total_X = target_X - current_x;
            double total_Y = target_Y - current_y;
            double total_Direction = target_Direction - current_direction;
            double distance = Math.sqrt(total_X * total_X + total_Y * total_Y + total_Direction * total_Direction);
            double startTime = getRuntime();
            double totalTime = Math.min(Math.sqrt(distance / max_acceleration / 2), distance / maxSpeed / 2) * Math.PI;
            double incomplete_Rate = 0;
            while (opModeIsActive() && (getRuntime() - startTime) < totalTime + 2) {
                if ((getRuntime() - startTime) <= totalTime)
                    incomplete_Rate = (Math.cos(Math.PI / totalTime * (getRuntime() - startTime)) + 1) / 2;
                double error_X = target_X - total_X * incomplete_Rate - current_x;
                double error_Y = target_Y - total_Y * incomplete_Rate - current_y;
                double error_direction = target_Direction - total_Direction * incomplete_Rate - current_direction;
                error_direction *= 0.1;
                double speed = Math.sqrt(error_X * error_X + error_Y * error_Y + error_direction * error_direction);
                move(error_X, error_Y, error_direction, speed * 0.08);
                telemetry.addData("error_X", error_X);
                telemetry.addData("error_Y", error_Y);
                telemetry.addData("error_direction", error_direction);
                telemetry.addData("speed", speed);
            }
            forward_left.setPower(0);
            rear_left.setPower(0);
            forward_right.setPower(0);
            rear_right.setPower(0);
        }

        public void resetPosition() {
            current_x = 0;
            current_y = 0;
            current_direction = 0;
        }

        private void refreshPosition() {
            forward_left.refresh();
            rear_left.refresh();
            forward_right.refresh();
            rear_right.refresh();
            double rightSpeed = (forward_left.getSpeed() - rear_left.getSpeed() - forward_right.getSpeed() + rear_right.getSpeed()) / 244.5;
            double forwardSpeed = (forward_left.getSpeed() + rear_left.getSpeed() + forward_right.getSpeed() + rear_right.getSpeed()) / 224.663;
            double turnSpeed = (-forward_left.getSpeed() - rear_left.getSpeed() + forward_right.getSpeed() + rear_right.getSpeed()) / 39.655;
            double dir_temp = (current_direction + turnSpeed / 2) * Math.PI / 180;
            current_x += rightSpeed * Math.cos(dir_temp) - forwardSpeed * Math.sin(dir_temp);
            current_y += rightSpeed * Math.sin(dir_temp) + forwardSpeed * Math.cos(dir_temp);
            current_direction += turnSpeed;
            telemetry.addData("currentX", current_x);
            telemetry.addData("currentY", current_y);
            telemetry.addData("currentDirection", current_direction);
            telemetry.addData("rightSpeed", rightSpeed);
            telemetry.addData("forwardSpeed", forwardSpeed);
            telemetry.addData("turnSpeed", turnSpeed);
            telemetry.update();
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
                wheel.setPower(Math.abs(power) < critical_speed ? 0 : power);
            }

            public int getPosition() {
                return wheel.getCurrentPosition();
            }

            public double getSpeed() {
                return speed;
            }
        }
    }

    public class Turntable {
        private DcMotor raise, extend, intake;
        private CRServo turn;
        private Servo container, lock, ship;
        private boolean last_direction;
        private double last_turn_time;

        public Turntable() {
            raise = hardwareMap.dcMotor.get("la");
            raise.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            raise.setTargetPosition(0);
            raise.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            extend = hardwareMap.dcMotor.get("extend");
            extend.setDirection(DcMotor.Direction.FORWARD);
            extend.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            extend.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            extend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            intake = hardwareMap.dcMotor.get("intake");
            intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            ship = hardwareMap.servo.get("shipping");
            turn = hardwareMap.crservo.get("btm");
            setDirection(false);
            container = hardwareMap.servo.get("lifter");
            setContainer(false);
            lock = hardwareMap.servo.get("lock");
            last_direction = false;
            last_turn_time = getRuntime();
        }

        public void setDirection(boolean left) {
            if (last_direction ^ left) {
                lock.setPosition(0.5);
                turn.setPower(left ? 1 : -1);
                last_direction = left;
                last_turn_time = getRuntime();
            } else if (getRuntime() - last_turn_time > 1.5) {
                lock.setPosition(0.8);
                turn.setPower(0);
            }
        }

        public void Collect() {
            intake.setPower(1);
        }

        public void backFlow() {
            intake.setPower(-0.8);
        }

        public void stopCollect() {
            intake.setPower(0);
        }

        public void setHeight(int height) {
            switch (height) {
                case 0:
                    raise.setTargetPosition(0);
                    break;
                case 1:
                    raise.setTargetPosition(600);
                    break;
            }
            raise.setPower(0.5);
            raise.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        public void setExtendSpeed(double speed) {
            extend.setPower(0.3 * speed);
        }

        public void setContainer(boolean pour) {
            container.setPosition(pour ? 0.49 : 1);
        }
    }


    public class Switcher {
        private double press_time, release_time;
        private boolean last_state, current_state, press, release, on;

        public Switcher(boolean current_state, boolean start_on) {
            press_time = release_time = getRuntime();
            last_state = this.current_state = current_state;
            press = release = false;
            this.on = start_on;
        }

        public boolean isOn() {
            if (justPress()) {
                press_time = getRuntime();
                on = !on;
            }
            return on;
        }

        public boolean justPress() {
            return press;
        }

        public boolean justRelease() {
            return release;
        }

        public void refresh(boolean state) {
            last_state = current_state;
            current_state = state;
            press = !last_state && current_state;
            release = last_state && !current_state;
            if (press)
                press_time = getRuntime();
            if (release)
                release_time = getRuntime();
        }

        public double getPress_time() {
            return press_time;
        }

        public double getRelease_time() {
            return release_time;
        }

        public double getSwitched_time() {
            return Math.min(press_time, release_time);
        }
    }

    public class TwoSwitchers {
        private Switcher buttonUp, buttonDown;
        private int level, total;

        public TwoSwitchers(boolean up, boolean down, int total_levels, int start_level) {
            total = total_levels;
            this.level = start_level;
            buttonUp = new Switcher(up, false);
            buttonDown = new Switcher(down, false);
        }

        public int getLevel(boolean up, boolean down) {
            buttonUp.refresh(up);
            buttonDown.refresh(down);
            if (buttonUp.justPress())
                ++level;
            if (buttonDown.justPress())
                --level;
            if (level >= total)
                level = total - 1;
            else if (level < 0)
                level = 0;
            return level;
        }
    }
}
