package org.firstinspires.ftc.team11047.custommodules;

public class MyMath {
    public static double distanceToPower(double distance) {
        return Math.copySign(Math.sqrt(Math.abs(distance)), distance);
    }
}
