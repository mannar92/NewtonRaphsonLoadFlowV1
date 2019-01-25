package Analysis;

import networkModel.Branch;
import networkModel.Bus;
import networkModel.Generation;

public class PowerFlowEquations {

    private double realPowerInj;
    private double reactivePowerInj;
    private double voltageMagnitude;
    private double angle;

    public PowerFlowEquations(AdmittanceMatrix admittanceMatrix, Bus bus,
                              Branch[] branch, Generation[] generations) {

        int k = (int) bus.getBusID();
        int type = (int) bus.getBusType();

        //  bus type (1 = PQ, 2 = PV, 3 = ref, 4 = isolated)
        switch (type){
            case 1:
                System.out.println("Bus " + k + " is a PQ bus(Load Bus) !");
                realPowerInj = bus.getRealPowerDemand();
                reactivePowerInj = bus.getReactivePowerDemand();
                break;
            case 2:
                System.out.println("Bus " + k + " is a PV bus(Generation Bus) !");
                voltageMagnitude = bus.getVoltageMagnitude(); //  per unit value

                for (Generation generation : generations) {
                    if (k == generation.getBusID()) {
                        realPowerInj = realPowerInj + generation.getRealPowerOutput();//   Pk injected
                    }
                }
                break;
            case 3:
                System.out.println("Bus " + k + " is the reference bus(Slack Bus) !");
                voltageMagnitude = bus.getVoltageMagnitude();
                angle = bus.getVoltageAngle();
                break;
            case 4:
                System.out.println("Bus " + k + " is an isolated bus(Slack Bus) !");
                break;
        }
        setRealPowerInjEquation(admittanceMatrix, branch, generations,
                realPowerInj,reactivePowerInj, voltageMagnitude, angle);
        setReactivePowerInjEquation(admittanceMatrix, branch, generations,
                realPowerInj,reactivePowerInj, voltageMagnitude, angle);

    }


    private void setRealPowerInjEquation(AdmittanceMatrix admittanceMatrix, Branch[] branch,
                                         Generation[] generations, double realPowerInj,
                                         double reactivePowerInj, double voltageMagnitude,
                                         double angle) {



    }

    private void setReactivePowerInjEquation(AdmittanceMatrix admittanceMatrix, Branch[] branch,
                                             Generation[] generations, double realPowerInj,
                                             double reactivePowerInj, double voltageMagnitude,
                                             double angle) {
    }


}
