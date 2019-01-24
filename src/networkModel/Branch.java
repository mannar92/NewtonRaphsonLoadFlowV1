package networkModel;

public class Branch {

    public int lineCounter=0;
    private final int lineIndex;
    //  see MATPOWER documentation for further information on the variables of this class.
    private double fromBusID;
    private double toBusID;

    //  below values are in p.u.
    private double resistance;
    private double reactance;
    private double totalLineChargingSusceptance;

    // MVA rating A, B and C for long term, short term and emergency rating respectively, ste to 0 for unlimited.
    private double rateA;
    private double rateB;
    private double rateC;

    //  transformer
    private double tap;
    private double shiftAngle;

    //  1 = in-service; 0 = out-of-service.
    private double branchStatus;

    //  The voltage angle difference is taken to be unbounded below if
    //  minAngleDifference < 􀀀360 and unbounded above if maxAngleDifference > 360.
    //  If both parameters are zero, the voltage angle
    //  difference is unconstrained.The voltage angle difference is taken to be unbounded below if
    //  minAngleDifference < 􀀀360 and unbounded above if maxAngleDifference > 360.
    //  If both parameters are zero, the voltage angle difference is unconstrained.
    private double minAngleDifference;
    private double maxAngleDifference;

    public Branch(double [] branchParameters) {
        lineIndex = lineCounter;
        fromBusID = branchParameters[0];
        toBusID = branchParameters[1];
        resistance = branchParameters[2];
        reactance = branchParameters[3];
        totalLineChargingSusceptance = branchParameters[4];
        rateA = branchParameters[5];
        rateB = branchParameters[6];
        rateC = branchParameters[7];
        tap = branchParameters[8];
        shiftAngle = branchParameters[9];
        branchStatus = branchParameters[10];
        minAngleDifference = branchParameters[11];
        maxAngleDifference = branchParameters[12];
        lineCounter++;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public double getFromBusID() {
        return fromBusID;
    }

    public double getToBusID() {
        return toBusID;
    }

    public double getResistance() {
        return resistance;
    }

    public double getReactance() {
        return reactance;
    }

    public double getTotalLineChargingSusceptance() {
        return totalLineChargingSusceptance;
    }

    public double getRateA() {
        return rateA;
    }

    public double getRateB() {
        return rateB;
    }

    public double getRateC() {
        return rateC;
    }

    public double getTap() {
        return tap;
    }

    public double getShiftAngle() {
        return shiftAngle;
    }

    public double getBranchStatus() {
        return branchStatus;
    }

    public double getMinAngleDifference() {
        return minAngleDifference;
    }

    public double getMaxAngleDifference() {
        return maxAngleDifference;
    }
}
