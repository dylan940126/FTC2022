package org.firstinspires.ftc.teamcode.custommodules;

public class Switch {
    private boolean last_state, current_state, press, on;

    public Switch(boolean start_on) {
        last_state = current_state = false;
        reset(start_on);
    }

    public boolean isOn() {
        return on;
    }

    public boolean isJustPress() {
        return press;
    }

    public void reset(boolean on) {
        press = false;
        this.on = on;
    }

    public void refresh(boolean state) {
        last_state = current_state;
        current_state = state;
        press = !last_state && current_state;
        on ^= press;
    }
}