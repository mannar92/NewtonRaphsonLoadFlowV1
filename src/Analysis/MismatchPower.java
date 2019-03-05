package Analysis;

import networkModel.Branch;
import networkModel.Bus;
import networkModel.Generation;

public class MismatchPower {

    private int numOfBuses;
    private double[] realPower;
    private double[] reactivePower;
    private double[] realPowerInj;
    private double[] reactivePowerInj;
    private double[] realPowerSpecified;
    private double[] reactivePowerSpecified;
    private double[] powerSpecified;
    private double[] powerInj;
    private double[] deltaPower;

    public MismatchPower (AdmittanceMatrix admittanceMatrix, Bus[] bus,
                          Generation[] generations, Branch[] branches){

        numOfBuses = bus.length;

        realPower = new double[numOfBuses];
        reactivePower = new double[numOfBuses];

        realPowerInj = new double[numOfBuses];
        reactivePowerInj = new double[numOfBuses];
        realPowerSpecified = new double[numOfBuses];
        reactivePowerSpecified = new double[numOfBuses];
        powerInj = new double[numOfBuses*2];
        powerSpecified = new double[numOfBuses*2];
        deltaPower = new double[numOfBuses*2];

        zeroArrays();

        for (int k=0; k<numOfBuses; k++){
            if (bus[k].isGenerationBus()){
                //realPower[k] = setRealPowerGeneration(bus[k], generations, branches);
                realPowerSpecified[k] = setRealPowerSpecified(admittanceMatrix, bus[k], generations, branches);
                //reactivePowerInj[k] = setReactivePowerInj(admittanceMatrix, bus, bus[k], generations, branches);
            } else if (bus[k].isLoadBus()){
                realPowerSpecified[k] = setRealPowerSpecified(admittanceMatrix, bus[k], generations, branches);
                reactivePowerSpecified[k] = setReactivePowerSpecified(admittanceMatrix, bus[k], generations, branches);
            } else if (bus[k].isSlackBus()){
                //realPowerInj[k] = setRealPowerInj(admittanceMatrix, bus, bus[k], generations, branches);
               // reactivePowerInj[k] = setReactivePowerInj(admittanceMatrix, bus, bus[k], generations, branches);
            } else {
                break;
            }
        }
        /* Original version
        realPowerInj = setRealPowerInj(admittanceMatrix, bus, generations, branches);
        reactivePowerInj = setReactivePowerInj(admittanceMatrix, bus, generations, branches);
        realPowerSpecified = setRealPowerSpecified(admittanceMatrix, bus, generations, branches);
        reactivePowerSpecified = setReactivePowerSpecified(admittanceMatrix, bus, generations, branches);
        */

        for (int k=0; k<numOfBuses; k++) {
            powerInj[k] = realPowerInj[k];
            powerInj[k+numOfBuses] = reactivePowerInj[k];
            powerSpecified[k] = realPowerSpecified[k];
            powerSpecified[k+numOfBuses] = reactivePowerSpecified[k];
            deltaPower[k] = powerSpecified[k] - powerInj[k];
            deltaPower[k+numOfBuses] = powerSpecified[k+numOfBuses] - powerInj[k+numOfBuses];
        }
    }

    private double setRealPowerGeneration(Bus busK, Generation[] generations, Branch[] branches) {
        int busIndex = (int) busK.getBusID()-1;
        for(Generation generation: generations){
            if(busK.getBusID()==generation.getBusID()) {
                realPower[busIndex] += generation.getRealPowerOutput();
            }
            // set real power generation to the corresponding bus
            busK.setRealPowerGeneration(realPowerSpecified[busIndex]);

            realPowerSpecified[busIndex] -= busK.getRealPowerDemand();
        }


        return realPowerSpecified[busIndex];
    }

    private void zeroArrays() {
        for (int k=0; k<numOfBuses; k++){
            realPowerInj[k] = 0.0;
            reactivePowerInj[k] = 0.0;
            realPowerSpecified[k] = 0.0;
            reactivePowerSpecified[k] = 0.0;
        }
    }

    private double setReactivePowerSpecified(AdmittanceMatrix admittanceMatrix, Bus busK,
                                               Generation[] generations, Branch[] branches) {
        int busIndex = (int) busK.getBusID()-1;

        for (Generation generation: generations){
            if(busK.getBusID()==generation.getBusID()){
                reactivePowerSpecified[busIndex] += generation.getRealPowerOutput();
            }
        }
            // set reactive power generation to the corresponding bus
            busK.setReactivePowerGeneration(reactivePowerSpecified[busIndex]);

            reactivePowerSpecified[busIndex] -= busK.getReactivePowerDemand();

        return reactivePowerSpecified[busIndex];
    }

    private double setRealPowerSpecified(AdmittanceMatrix admittanceMatrix, Bus busK,
                                           Generation[] generations, Branch[] branches) {
        int busIndex = (int) busK.getBusID()-1;
        for(Generation generation: generations){
            if(busK.getBusID()==generation.getBusID()) {
                realPowerSpecified[busIndex] += generation.getRealPowerOutput();
            }
            // set real power generation to the corresponding bus
            busK.setRealPowerGeneration(realPowerSpecified[busIndex]);

            realPowerSpecified[busIndex] -= busK.getRealPowerDemand();
        }


        return realPowerSpecified[busIndex];
    }

    private double setReactivePowerInj(AdmittanceMatrix admittanceMatrix, Bus[] bus, Bus busK,
                                         Generation[] generations, Branch[] branches) {
        int busIndex = (int) busK.getBusID()-1;
        double vk;          //  voltage magnitude at bus k
        double vi;          //  voltage magnitude at bus i
        double angleki;     //  angle difference between bus k and i
        double gki;         //  conductance at line between bus k and i
        double bki;         //  susceptance at line between bus k and i

        vk = busK.getVoltageMagnitude();

        for (int i=0; i<branches.length; i++) {
            int toBus = (int) branches[i].getToBusID() - 1;
            vi = bus[toBus].getVoltageMagnitude();
            angleki = bus[busIndex].getVoltageAngle() - bus[toBus].getVoltageAngle();
            gki = admittanceMatrix.getConductance()[busIndex][toBus];
            bki = admittanceMatrix.getSusceptance()[busIndex][toBus];

            reactivePowerInj[busIndex] += vk * vi * ((gki * Math.sin(angleki))
                    - (bki * Math.cos(angleki)));
        }
        return reactivePowerInj[busIndex];
    }

    private double setRealPowerInj(AdmittanceMatrix admittanceMatrix, Bus[] bus, Bus busK,
                                 Generation[] generations, Branch[] branches) {
        int busIndex = (int) busK.getBusID()-1;
        double vk;          //  voltage magnitude at bus k
        double vi;          //  voltage magnitude at bus i
        double angleki;     //  angle difference between bus k and i
        double gki;         //  conductance at line between bus k and i
        double bki;         //  susceptance at line between bus k and i

        vk = bus[busIndex].getVoltageMagnitude();

        for (int i=0; i<branches.length; i++){
            int toBus = (int)branches[i].getToBusID()-1;
            vi = bus[toBus].getVoltageMagnitude();
            angleki = bus[busIndex].getVoltageAngle() - bus[toBus].getVoltageAngle();
            gki = admittanceMatrix.getConductance()[busIndex][toBus];
            bki = admittanceMatrix.getSusceptance()[busIndex][toBus];

            realPowerInj[busIndex] += vk * vi * ((gki * Math.cos(angleki))
                    + (bki * Math.sin(angleki)));
            }
        return realPowerInj[busIndex];
    }

    public double[] getPowerSpecified() {
        return powerSpecified;
    }

    public double[] getPowerInj() {
        return powerInj;
    }

    public double[] getDeltaPower() {
        return deltaPower;
    }

    public void print() {
        try {
            System.out.println();
            System.out.println("=====================================================================");
            System.out.println("=======================    Mismatch Power     =======================");
            System.out.println("=====================================================================");

            double rows = deltaPower.length;
            //double columns = .length;
            String str = "|\t";

            for (int i = 0; i < rows; i++) {
                //for(int j=0;j<columns;j++){
                str += deltaPower[i] + "\t";
                //}

                System.out.println(str + "|");
                str = "|\t";
            }
        }catch(Exception e){System.out.println("Matrix is empty!!");}
    }
}
