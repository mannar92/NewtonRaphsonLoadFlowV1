package networkModel;

import constants.Constants;

public final class Bus {

    private int busIndex;
    private double busID;
    private double busType;
    private double realPowerDemand;
    private double reactivePowerDemand;
    private double shuntConductance;
    private double shuntSuceptance;
    private double busArea;
    private double voltageMagnitude;
    private double voltageAngle;
    private double baseVoltage;
    private double lossZone;
    private double maxVoltage;
    private double minVoltage;
    private double realPowerGeneration;
    private double reactivePowerGeneration;

    private int busCounter =0;

    public Bus(double [] busParameters) {
        busIndex = busCounter;
        busID=busParameters[0];
        busType=busParameters[1];
        realPowerDemand=busParameters[2]/ Constants.MVABASE;
        reactivePowerDemand=busParameters[3]/Constants.MVABASE;
        shuntConductance=busParameters[4];
        shuntSuceptance=busParameters[5];
        busArea=busParameters[6];
        voltageMagnitude=busParameters[7];
        voltageAngle=busParameters[8];
        baseVoltage=busParameters[9];
        lossZone=busParameters[10];
        maxVoltage=busParameters[11];
        minVoltage=busParameters[12];
        busCounter++;
    }

    public double getBusID(){
        return busID;
    }

    public double getBusType() {
        return busType;
    }

    public double getRealPowerDemand() {
        return realPowerDemand;
    }

    public double getReactivePowerDemand() {
        return reactivePowerDemand;
    }

    public double getShuntConductance() {
        return shuntConductance;
    }

    public double getBusArea() {
        return busArea;
    }

    public double getVoltageMagnitude() {
        return voltageMagnitude;
    }

    public double getBaseVoltage() {
        return baseVoltage;
    }

    public double getVoltageAngle() {
        return voltageAngle;
    }

    public int getBusIndex() {
        return busIndex;
    }

    public double getShuntSuceptance() {
        return shuntSuceptance;
    }

    public double getMinVoltage() {
        return minVoltage;
    }

    public double getMaxVoltage() {
        return maxVoltage;
    }

    public double getLossZone() {
        return lossZone;
    }

    public boolean isGenerationBus() {
        return busType == 2;
    }

    public boolean isLoadBus() {
        return busType == 1;
    }

    public boolean isSlackBus(){
        return  busType == 3;
    }

    public void setRealPowerDemand(double realPowerDemand) {
        this.realPowerDemand = realPowerDemand;
    }

    public void setReactivePowerDemand(double reactivePowerDemand) {
        this.reactivePowerDemand = reactivePowerDemand;
    }

    public void setVoltageMagnitude(double voltageMagnitude) {
        this.voltageMagnitude = voltageMagnitude;
    }

    public void setVoltageAngle(double voltageAngle) {
        this.voltageAngle = voltageAngle;
    }

    public void setRealPowerGeneration(double realPowerGeneration) {
        this.realPowerGeneration = realPowerGeneration;
    }

    public void setReactivePowerGeneration(double reactivePowerGeneration) {
        this.reactivePowerGeneration = reactivePowerGeneration;
    }
}
