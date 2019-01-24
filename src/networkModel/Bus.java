package networkModel;

public final class Bus {

    private final int busIndex;
    private final double busID;
    private final double busType;
    private final double realPowerDemand;
    private final double reactivePowerDemand;
    private final double shuntConductance;
    private final double shuntSuceptance;
    private final double busArea;
    private final double voltageMagnitude;
    private final double voltageAngle;
    private final double baseVoltage;
    private final double lossZone;
    private final double maxVoltage;
    private final double minVoltage;

    private int busCounter =0;

    public Bus(double [] busParameters) {
        busIndex = busCounter;
        busID=busParameters[0];
        busType=busParameters[1];
        realPowerDemand=busParameters[2];
        reactivePowerDemand=busParameters[3];
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
}
