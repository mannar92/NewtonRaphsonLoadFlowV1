package Analysis;

import networkModel.Branch;
import networkModel.Bus;
import networkModel.Generation;

public class MismatchPower {

    private int numOfBuses;
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

        realPowerInj = new double[numOfBuses];
        reactivePowerInj = new double[numOfBuses];
        realPowerSpecified = new double[numOfBuses];
        reactivePowerSpecified = new double[numOfBuses];
        powerInj = new double[numOfBuses*2];
        powerSpecified = new double[numOfBuses*2];
        deltaPower = new double[numOfBuses*2];

        realPowerInj = setRealPowerInj(admittanceMatrix, bus, generations, branches);
        reactivePowerInj = setReactivePowerInj(admittanceMatrix, bus, generations, branches);
        realPowerSpecified = setRealPowerSpecified(admittanceMatrix, bus, generations, branches);
        reactivePowerInj = setReactivePowerSpecified(admittanceMatrix, bus, generations, branches);

        for (int i=0; i<numOfBuses; i++) {
            powerInj[i] = realPowerInj[i];
            powerInj[i+numOfBuses] = reactivePowerInj[i];
            powerSpecified[i] = realPowerSpecified[i];
            powerSpecified[i+numOfBuses] = reactivePowerInj[i];
            deltaPower[i] = powerSpecified[i] - powerInj[i];
        }
    }

    private double[] setReactivePowerSpecified(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                                               Generation[] generations, Branch[] branches) {

        for (int k=0; k<numOfBuses; k++){
            for (Generation generation: generations){
                if(bus[k].getBusID()==generation.getBusID()){
                    reactivePowerSpecified[k] += generation.getRealPowerOutput();
                }
            }
            // set reactive power generation to the corresponding bus
            for (int i=0; i<bus.length; i++){
                bus[i].setReactivePowerGeneration(reactivePowerSpecified[i]);
            }
            reactivePowerSpecified[k] -= bus[k].getReactivePowerDemand();
        }
        return reactivePowerSpecified;
    }

    private double[] setRealPowerSpecified(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                                           Generation[] generations, Branch[] branches) {
        for (int k=0; k<numOfBuses; k++){
            for(Generation generation: generations){
                if(bus[k].getBusID()==generation.getBusID()) {
                    realPowerSpecified[k] += generation.getRealPowerOutput();
                }
            }
            // set real power generation to the corresponding bus
            for (int i=0; i<bus.length; i++){
                bus[i].setRealPowerGeneration(realPowerSpecified[i]);
            }
            realPowerSpecified[k] -= bus[k].getRealPowerDemand();
        }


        return realPowerSpecified;
    }

    private double[] setReactivePowerInj(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                                         Generation[] generations, Branch[] branches) {
        double vk;          //  voltage magnitude at bus k
        double vi;          //  voltage magnitude at bus i
        double angleki;     //  angle difference between bus k and i
        double gki;         //  conductance at line between bus k and i
        double bki;         //  susceptance at line between bus k and i

        for (int k=0; k<numOfBuses; k++){
            vk = bus[k].getVoltageMagnitude();

            for (int i=0; i<branches.length; i++){
                int toBus = (int)branches[i].getToBusID()-1;
                vi = bus[toBus].getVoltageMagnitude();
                angleki = bus[k].getVoltageAngle() - bus[i].getVoltageAngle();
                gki = admittanceMatrix.getConductance()[k][i];
                bki = admittanceMatrix.getSusceptance()[k][i];

                reactivePowerInj[k] += vk * vi * ((gki * Math.sin(angleki))
                        - (bki * Math.cos(angleki)));
            }
        }
        return reactivePowerInj;
    }

    private double[] setRealPowerInj(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                                 Generation[] generations, Branch[] branches) {
        double vk;          //  voltage magnitude at bus k
        double vi;          //  voltage magnitude at bus i
        double angleki;     //  angle difference between bus k and i
        double gki;         //  conductance at line between bus k and i
        double bki;         //  susceptance at line between bus k and i

        for (int k=0; k<numOfBuses; k++){
            vk = bus[k].getVoltageMagnitude();

            for (int i=0; i<branches.length; i++){
                int toBus = (int)branches[i].getToBusID()-1;
                vi = bus[toBus].getVoltageMagnitude();
                angleki = bus[k].getVoltageAngle() - bus[i].getVoltageAngle();
                gki = admittanceMatrix.getConductance()[k][i];
                bki = admittanceMatrix.getSusceptance()[k][i];

                realPowerInj[k] += vk * vi * ((gki * Math.cos(angleki))
                        + (bki * Math.sin(angleki)));
            }
        }
        return realPowerInj;
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
}
