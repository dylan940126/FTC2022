package org.firstinspires.ftc.teamcode.mods;

public class PID {
    private double p, i, d, p_Proportion, i_Proportion, i_Decline, d_Proportion, previous_error = 0;
    private boolean previousSign = false;

    public PID(double p_Proportion, double i_Proportion, double i_Decline, double d_Proportion) {
        reconfigure(p_Proportion, i_Proportion, i_Decline, d_Proportion);
        reset();
    }

    public double revise(double currentValue, double targetValue) {
        double error = targetValue - currentValue;
        p = error * p_Proportion;
        i = i * i_Decline + error * i_Proportion;
        if (previousSign ^ error > 0) {
            i = 0;
            previousSign = error > 0;
        }
        d = (error - previous_error) * d_Proportion;
        previous_error = error;
        return p + i - d;
    }

    public void reconfigure(double p_Proportion, double i_Proportion, double i_Decline, double d_Proportion) {
        this.p_Proportion = p_Proportion;
        this.i_Proportion = i_Proportion;
        this.i_Decline = i_Decline;
        this.d_Proportion = d_Proportion;
    }

    public void reset() {
        p = 0;
        i = 0;
        d = 0;
    }

    public double getP() {
        return p;
    }

    public double getI() {
        return i;
    }

    public double getD() {
        return d;
    }
}
