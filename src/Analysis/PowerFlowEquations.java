package Analysis;

import networkModel.Branch;
import networkModel.Bus;
import networkModel.Generation;

public class PowerFlowEquations {

    private double realPowerInj;
    private double reactivePowerInj;
    private double voltageMagnitudeK;
    private double angle;

    public PowerFlowEquations(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                              Branch[] branch, Generation[] generations, int busID) {

        int type = (int) bus[busID].getBusType();

        //  bus type (1 = PQ, 2 = PV, 3 = ref, 4 = isolated)
        switch (type){
            case 1:
                System.out.println("Bus " + busID + " is a PQ bus(Load Bus) !");
                realPowerInj = bus[busID].getRealPowerDemand();
                reactivePowerInj = bus[busID].getReactivePowerDemand();
                System.out.println("Bus " + busID + " realPowerInj = " + realPowerInj);
                System.out.println("Bus " + busID + " reactivePowerInj = " + reactivePowerInj);
                break;
            case 2:
                System.out.println("Bus " + busID + " is a PV bus(Generation Bus) !");
                voltageMagnitudeK = bus[busID].getVoltageMagnitude(); //  per unit value

                for (Generation generation : generations) {
                    if (busID == generation.getBusID()) {
                        realPowerInj = realPowerInj + generation.getRealPowerOutput();//   Pk injected
                    }
                }
                System.out.println("Bus " + busID + " realPowerInj = " + realPowerInj);


                setReactivePowerInjEquation(admittanceMatrix, bus, branch, generations,
                        realPowerInj, voltageMagnitudeK, busID );
                break;
            case 3:
                System.out.println("Bus " + busID + " is the reference bus(Slack Bus) !");
                voltageMagnitudeK = bus[busID].getVoltageMagnitude();
                angle = bus[busID].getVoltageAngle();

                setReactivePowerInjEquation(admittanceMatrix, bus, branch, generations,
                        realPowerInj, voltageMagnitudeK, busID );
                setRealPowerInjEquation(admittanceMatrix, bus, branch, generations,
                        voltageMagnitudeK, angle, busID);
                break;
            case 4:
                System.out.println("Bus " + busID + " is an isolated bus!");
                break;
        }
       // setRealPowerInjEquation(admittanceMatrix, branch, generations,
         //       realPowerInj,reactivePowerInj, voltageMagnitudeK, angle);
        //setReactivePowerInjEquation(admittanceMatrix, branch, generations,
          //      realPowerInj,reactivePowerInj, voltageMagnitudeK, angle);

    }


    private void setRealPowerInjEquation(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                                         Branch[] branch, Generation[] generations,
                                         double realPowerInj, double reactivePowerInj,
                                         int busID) {
        int k = busID;

        for (int j=0; j<branch.length; j++){
            int i = ((int)branch[j].getToBusID()-1);
            double vk = bus[k].getVoltageMagnitude();
            double vi = bus[i].getVoltageMagnitude();
            double angleKI = bus[k].getVoltageAngle()-bus[i].getVoltageAngle();
            double conductanceKI = admittanceMatrix.getConductance()[k][i];
            double susceptanceKI = admittanceMatrix.getSusceptance()[k][i];

            realPowerInj += vk*vi*(conductanceKI*Math.cos(angleKI) + susceptanceKI*Math.sin(angleKI));
        }
        System.out.println("Bus " + k + " realPowerInj = " + realPowerInj);
    }

    private void setReactivePowerInjEquation(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                                             Branch[] branch, Generation[] generations,
                                             double realPowerInj, double voltageMagnitudeK,
                                             int busID) {
        int k = busID;

        for (int j=0; j<branch.length; j++){
            int i = ((int)branch[j].getToBusID()-1);
            double vk = bus[k].getVoltageMagnitude();
            double vi = bus[i].getVoltageMagnitude();
            double angleKI = bus[k].getVoltageAngle()-bus[i].getVoltageAngle();
            double conductanceKI = admittanceMatrix.getConductance()[k][i];
            double susceptanceKI = admittanceMatrix.getSusceptance()[k][i];

            reactivePowerInj += vk*vi*(conductanceKI*Math.sin(angleKI) - susceptanceKI*Math.cos(angleKI));
        }
        System.out.println("Bus " + k + " reactivePowerInj = " + reactivePowerInj);
    }


    public double getReactivePowerInj() {
        return reactivePowerInj;
    }

    public double getRealPowerInj() {
        return realPowerInj;
    }
}
