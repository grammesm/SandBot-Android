package com.alwaystinkering.sandbot.model.sequence;

public class Sequence {

    private String name;
    private String commands;
    private boolean autoRun;

    public Sequence(String name, String commands, boolean runAtStart) {
        this.name = name;
        this.commands = commands;
        this.autoRun = runAtStart;
    }

    public String getName() {
        return name;
    }

    public String getCommands() {
        return commands;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCommands(String commands) {
        this.commands = commands;
    }

    public boolean isAutoRun() {
        return autoRun;
    }

    public void setAutoRun(boolean autoRun) {
        this.autoRun = autoRun;
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();

        json.append("\"").append(name).append("\":{")
                .append("\"commands\":\"").append(commands.replace("\n", "\\n")).append("\",")
                .append("\"runAtStart\":").append(autoRun ? "1" : "0")
                .append("}");
        return json.toString();
    }
}
