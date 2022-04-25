package org.firstinspires.ftc.team11047.custommodules;

public class MyMath {
    public static double distanceToPowerAndCut(double distance) {
        return Math.copySign(Math.min(Math.sqrt(Math.abs(distance)), 1), distance);
    }

    public static double distanceToPower(double distance) {
        return Math.copySign(Math.sqrt(Math.abs(distance)), distance);
    }
}
