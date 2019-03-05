package networkModel;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import constants.Constants;

public class Generation {

    public int genCounter=0;
    private final int genIndex;
    //  see MATPOWER documentaiton for further information.
    private double busID;
    private double realPowerOutput;
    private double reactivePowerOutput;
    private double maxReactivePowerOutput;
    private double minReactivePowerOutput;
    private double voltageMagnitudeSetpoint;
    private double baseMVA;
    private double genStatus;
    private double maxRealPowerOutput;
    private double minRealPowerOutput;

    //  the variables bellow are set to zero by default, except stated otherwise.
    //  PC1 and PC2 are the lower and upper real power output of PQ capability curve (MW) respectively.
    private double pc1;
    private double pc2;

    //  minimum and maximum reactive power output at PC1 and PC2.
    private double minQC1;
    private double maxQC1;
    private double minQC2;
    private double maxQC2;

    //  Ramp rate for 10 mins, for 30 mins, and for ractive power (2 sec timescale) (MVAr/min)
    private double rampAGC;
    private double rampRate10;
    private double rampRate30;
    private double rampRateQ;

    private double areaParticipationFactor;

    public Generation(double [] genParameters) {
        genIndex = genCounter;
        baseMVA = genParameters[6];
        busID = genParameters[0];
        realPowerOutput = genParameters[1]/ Constants.MVABASE;
        reactivePowerOutput = genParameters[2]/ Constants.MVABASE;
        maxReactivePowerOutput = genParameters[3];
        minReactivePowerOutput = genParameters[4];
        voltageMagnitudeSetpoint = genParameters[5];
        genStatus = genParameters[7];
        maxRealPowerOutput = genParameters[9];
        minRealPowerOutput = genParameters[9];
        pc1 = genParameters[10];
        pc2 = genParameters[11];
        minQC1 = genParameters[12];
        maxQC1 = genParameters[13];
        minQC2 = genParameters[14];
        maxQC2 = genParameters[15];
        rampAGC = genParameters[16];
        rampRate10 = genParameters[17];
        rampRate30 = genParameters[18];
        rampRateQ = genParameters[19];
        areaParticipationFactor = genParameters[20];
        genCounter++;
    }

    public double getBusID() {
        return busID;
    }

    public double getRealPowerOutput() {
        return realPowerOutput/100;
    }

    public double getReactivePowerOutput() {
        return reactivePowerOutput;
    }

    public double getMaxReactivePowerOutput() {
        return maxReactivePowerOutput;
    }

    public double getMinReactivePowerOutput() {
        return minReactivePowerOutput;
    }

    public double getVoltageMagnitudeSetpoint() {
        return voltageMagnitudeSetpoint;
    }

    public double getBaseMVA() {
        return baseMVA;
    }

    public double getGenStatus() {
        return genStatus;
    }

    public double getMaxRealPowerOutput() {
        return maxRealPowerOutput;
    }

    public double getMinRealPowerOutput() {
        return minRealPowerOutput;
    }

    public double getPc1() {
        return pc1;
    }

    public double getPc2() {
        return pc2;
    }

    public double getMinQC1() {
        return minQC1;
    }

    public double getMaxQC1() {
        return maxQC1;
    }

    public double getMinQC2() {
        return minQC2;
    }

    public double getMaxQC2() {
        return maxQC2;
    }

    public double getRampAGC() {
        return rampAGC;
    }

    public double getRampRate10() {
        return rampRate10;
    }

    public double getRampRate30() {
        return rampRate30;
    }

    public double getRampRateQ() {
        return rampRateQ;
    }

    public double getAreaParticipationFactor() {
        return areaParticipationFactor;
    }
}
