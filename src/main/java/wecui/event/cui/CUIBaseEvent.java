package wecui.event.cui;

import wecui.WorldEditCUI;

public abstract class CUIBaseEvent {

    protected WorldEditCUI controller;
    protected String[] args;

    public CUIBaseEvent(WorldEditCUI controller, String[] args) {
        this.controller = controller;
        this.args = args;
    }

    public abstract String run();

    public abstract CUIEventType getEventType();

    public String getEventName() {
        String name = this.getEventType().name().toLowerCase();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;
    }

    public boolean isValid() {
        int max = this.getEventType().getMaxParameters();
        int min = this.getEventType().getMinParameters();

        if (max == min) {
            if (this.args.length != max) {
                return false;
            }
        } else {
            if (this.args.length > max || this.args.length < min) {
                return false;
            }
        }

        return true;

    }

    public final String doRun() {
        if (controller == null || args == null) {
            throw new RuntimeException("Controller and parameters must both be set.");
        }

        if (!this.isValid()) {
            String message = "Invalid number of parameters. " + this.getEventName() + " event requires ";
            if (this.getEventType().getMaxParameters() == this.getEventType().getMinParameters()) {
                message += this.getEventType().getMaxParameters() + " parameters. ";
            } else {
                message += "between " + this.getEventType().getMinParameters() + " and " + this.getEventType().getMaxParameters() + " parameters. ";
            }

            message += "Received " + args.length + " parameters instead. ";
            for (String arg : args) {
                message += arg + " ";
            }

            throw new RuntimeException(message);
        } else {
            return this.run();
        }

    }

    public int getInt(int index) {
        return (int) Float.parseFloat(args[index]);
    }

    public String getString(int index) {
        return args[index];
    }
}
