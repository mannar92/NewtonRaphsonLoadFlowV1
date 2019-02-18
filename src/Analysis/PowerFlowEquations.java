package Analysis;

import networkModel.Branch;
import networkModel.Bus;
import networkModel.Generation;

public class PowerFlowEquations {

    private int numOfBuses;
    private double realPowerInj;
    private double reactivePowerInj;
    private double[] power;
    private double[] powerSpecified;

    public PowerFlowEquations(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                              Branch[] branch, Generation[] generations) {

        numOfBuses = bus.length;
        power = new double[bus.length*2];
        powerSpecified = new double[bus.length*2];

        for (int busID=0; busID<bus.length; busID++) {

            int type = (int) bus[busID].getBusType();

            //  bus type (1 = PQ, 2 = PV, 3 = ref, 4 = isolated)
            switch (type) {
                case 1:
                    System.out.println("Bus " + busID + " is a PQ bus(Load Bus) !");
                    powerSpecified[busID] = bus[busID].getRealPowerDemand();
                    powerSpecified[busID + bus.length] = bus[busID].getReactivePowerDemand();
                    System.out.println("Bus " + busID + " realPowerInj = " + realPowerInj);
                    System.out.println("Bus " + busID + " reactivePowerInj = " + reactivePowerInj);
                    break;
                case 2:
                    System.out.println("Bus " + busID + " is a PV bus(Generation Bus) !");
                    double voltageMagnitudeK = bus[busID].getVoltageMagnitude(); //  per unit value

                    for (Generation generation : generations) {
                        if (busID == generation.getBusID()) {
                            realPowerInj = realPowerInj + generation.getRealPowerOutput();//   Pk injected
                        }
                    }
                    System.out.println("Bus " + busID + " realPowerInj = " + realPowerInj);
                    power[busID] = realPowerInj;


                    setReactivePowerInjEquation(admittanceMatrix, bus, branch, generations,
                            realPowerInj, voltageMagnitudeK, busID);
                    break;
                case 3:
                    System.out.println("Bus " + busID + " is the reference bus(Slack Bus) !");
                    voltageMagnitudeK = bus[busID].getVoltageMagnitude();
                    double angle = bus[busID].getVoltageAngle();

                    setReactivePowerInjEquation(admittanceMatrix, bus, branch, generations,
                            realPowerInj, voltageMagnitudeK, busID);
                    setRealPowerInjEquation(admittanceMatrix, bus, branch, generations,
                            voltageMagnitudeK, angle, busID);
                    break;
                case 4:
                    System.out.println("Bus " + busID + " is an isolated bus!");
                    break;
            }

        }
    }


    private void setRealPowerInjEquation(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                                         Branch[] branch, Generation[] generations,
                                         double realPowerInj, double reactivePowerInj,
                                         int busID) {

        for (int j=0; j<branch.length; j++){
            int i = ((int)branch[j].getToBusID()-1);
            double vk = bus[busID].getVoltageMagnitude();
            double vi = bus[i].getVoltageMagnitude();
            double angleKI = bus[busID].getVoltageAngle()-bus[i].getVoltageAngle();
            double conductanceKI = admittanceMatrix.getConductance()[busID][i];
            double susceptanceKI = admittanceMatrix.getSusceptance()[busID][i];

            realPowerInj += vk*vi*(conductanceKI*Math.cos(angleKI) + susceptanceKI*Math.sin(angleKI));
        }
        System.out.println("Bus " + busID + " realPowerInj = " + realPowerInj);
        power[busID] = realPowerInj;
    }

    private void setReactivePowerInjEquation(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                                             Branch[] branch, Generation[] generations,
                                             double realPowerInj, double voltageMagnitudeK,
                                             int busID) {

        for (int j=0; j<branch.length; j++){
            int i = ((int)branch[j].getToBusID()-1);
            double vk = bus[busID].getVoltageMagnitude();
            double vi = bus[i].getVoltageMagnitude();
            double angleKI = bus[busID].getVoltageAngle()-bus[i].getVoltageAngle();
            double conductanceKI = admittanceMatrix.getConductance()[busID][i];
            double susceptanceKI = admittanceMatrix.getSusceptance()[busID][i];

            reactivePowerInj += vk*vi*(conductanceKI*Math.sin(angleKI) - susceptanceKI*Math.cos(angleKI));
        }
        System.out.println("Bus " + busID + " reactivePowerInj = " + reactivePowerInj);
        power[busID+bus.length] = realPowerInj;
    }


    public double getReactivePowerInj() {
        return reactivePowerInj;
    }

    public double getRealPowerInj() {
        return realPowerInj;
    }

    public double[] getPower() {
        return power;
    }

    public double getRealPowerInjAtBus (int busID){
        return power[busID];
    }

    public double getReactivePowerInjAtBus (int busID){
        return power[numOfBuses + busID];
    }
}
