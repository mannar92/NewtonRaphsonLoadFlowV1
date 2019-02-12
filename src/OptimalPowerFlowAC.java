import Analysis.AdmittanceMatrix;
import Analysis.PowerFlowEquations;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLNumericArray;
import constants.Constants;
import networkModel.Branch;
import networkModel.Bus;
import networkModel.Generation;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class OptimalPowerFlowAC {
    //data file directory
    public static String filePath;
    public static int periods;
    public static String matFilePath;

    //arrays of the netwok model from the .mat file
    public static MLNumericArray busMLArray;
    public static MLNumericArray generationMLArray;
    public static MLNumericArray branchMLArray;
    public static MLNumericArray generationCostMLArray;
    public static double[][] busData;
    public static double[][] generationData;
    public static double[][] generationCostData;
    public static double[][] branchData;

    public static void main(String[] Args) throws IOException {

        handleUserInput();

        long timer = System.nanoTime();

        parseMatData();

        //  create arrays of objects representing the elements of the GB power system (Generators, buses, lines)
        Bus[] bus = createBusObjectArray();
        Generation[] generation = createGenerationObjectArray();
        Branch[] branch = createBranchObjectArray();

        printNumberOfElements(bus.length, generation.length, branch.length);


        System.out.println("Bus 2 Reactive Power Demand (MVAr): "+bus[1].getReactivePowerDemand());
        System.out.println("Bus 29 Reactive Power Demand (MVAr): "+bus[28].getReactivePowerDemand());

        System.out.println("Generation 2 max Reactive Power Output (MVAr): " + generation[1].getMaxReactivePowerOutput());
        System.out.println("Generation 51 max Reactive Power Output (MVAr): " + generation[50].getMaxReactivePowerOutput());

        System.out.println("Branch 1 reactance: "+branch[0].getReactance());
        System.out.println("Branch 1 resistance: "+branch[0].getResistance());
        System.out.println("Branch 99 reactance: "+branch[98].getReactance());
        System.out.println("Branch 99 resistance: "+branch[98].getResistance());
        System.out.println();

        AdmittanceMatrix yMatrix = new AdmittanceMatrix(bus, branch);

        double[][] testConductance = yMatrix.getConductance();
        double[][] testSusceptance = yMatrix.getSusceptance();

        System.out.println("Ymatrix element 00 " + yMatrix.getyMatrixElement(1,1));
        System.out.println("Real of element 00 " + testConductance[0][0]);
        System.out.println("Real of element 01 " + testConductance[0][1]);
        System.out.println("Real of element 02 " + testConductance[0][2]);
        System.out.println("Real of element 03 " + testConductance[0][3]);
        System.out.println("Imaginary of element 00 " + testSusceptance[0][0]);
        System.out.println("Imaginary of element 01 " + testSusceptance[0][1]);
        System.out.println("Imaginary of element 02 " + testSusceptance[0][2]);
        System.out.println("Imaginary of element 03 " + testSusceptance[0][3]);
        System.out.println();

        PowerFlowEquations[] equationSet = new PowerFlowEquations[bus.length];
        for (int i=0; i<bus.length; i++ ){
            equationSet[i] = new PowerFlowEquations(yMatrix, bus, branch, generation, i);
        }

        simulationTimer(System.nanoTime()-timer);
    }   // end main

    private static void printNumberOfElements(int x, int y, int z) {
        System.out.println("\nNumber of Bus objects detected: "+ x);
        System.out.println("Number of Generation objects detected: "+ y);
        System.out.println("Number of Branch (Line) objects detected: "+ z + "\n");
    }

    private static Branch[] createBranchObjectArray() {
        int numberOfBranches = branchData.length;
        Branch[] branch = new Branch[numberOfBranches];
        for (int i=0; i<numberOfBranches; i++){
            branch[i] = new Branch(branchData[i]);
        }
        return branch;
    }

    private static Generation[] createGenerationObjectArray() {
        int numberOfGenerations = generationData.length;
        Generation[] generation = new Generation[numberOfGenerations];
        for (int i=0; i<numberOfGenerations; i++){
            generation[i] = new Generation(generationData[i]);
        }
        return generation;
    }

    private static Bus[] createBusObjectArray() {
        int numberOfBuses= busData.length; //29 busses
        Bus[] bus = new Bus[numberOfBuses];
        for (int i=0; i<numberOfBuses;i++){
            bus[i] = new Bus(busData[i]);
        }
        return bus;
    }

    //reads .MAT file from the specified folder as a map of MLArray objects
    public static void parseMatData() throws IOException {

        File matFile = new File(filePath+File.separator+ matFilePath);
        MatFileReader reader = new MatFileReader(matFile);

        //gets MLNumericArray objects from the reader
        getReaderData(reader);

        //print MLArray dimensions
        System.out.println();
        System.out.println("busMLArray Dimensions: " + busMLArray.getDimensions()[0] + " x " + busMLArray.getDimensions()[1]);
        System.out.println("generationMLArray Dimensions: " + generationMLArray.getDimensions()[0] + " x " +  generationMLArray.getDimensions()[1]);
        System.out.println("generationCostMLArray Dimensions: " + generationCostMLArray.getDimensions()[0] + " x " + generationCostMLArray.getDimensions()[1]);
        System.out.println("branchMLArray Dimensions: " + branchMLArray.getDimensions()[0] + " x " + branchMLArray.getDimensions()[1]);
        System.out.println();

        //convert MLNumericArray to double[][]
        busData = convertMLNumericToDouble(busMLArray);
        generationData = convertMLNumericToDouble(generationMLArray);
        generationCostData = convertMLNumericToDouble(generationCostMLArray);
        branchData = convertMLNumericToDouble(branchMLArray);

        //print double array dimensions to verify correct conversion
        System.out.println();
        System.out.println("busData Dimensions: " + busData.length + " x " + busData[0].length);
        System.out.println("generationData Dimensions: " + generationData.length + " x " + generationData[0].length);
        System.out.println("generationCostData Dimensions: " + generationCostData.length + " x " + generationCostData[0].length);
        System.out.println("branchData Dimensions: " + branchData.length + " x " + branchData[0].length);
        System.out.println();

        //print data matrices
        System.out.println();
        System.out.println("BUS DATA");
        printMatrix(busData);
        System.out.println();
        System.out.println("GENERATION DATA");
        printMatrix(generationData);
        System.out.println();
        System.out.println("BRANCH DATA");
        printMatrix(branchData);
        System.out.println();
        System.out.println("GENERATION COST DATA");
        printMatrix(generationCostData);
        System.out.println();
    }

    private static double[][] convertMLNumericToDouble(MLNumericArray mlArray) {
        if (mlArray!=null){
            int row = mlArray.getM();
            int column = mlArray.getN();

            double[][] array = new double[row][column];

            for (int i=0;i<row;i++){
                for(int j=0;j<column;j++){
                    array[i][j]= (Double) mlArray.get(i,j);
                }
            }
            return array;
        }else{
            System.out.println("MLNumericArray is empty!");
            return null;
        }
    }

    private static void printMatrix(double[][] matrix) {
        try{
            double rows = matrix.length;
            double columns = matrix[0].length;
            String str = "|\t";

            for(int i=0;i<rows;i++){
                for(int j=0;j<columns;j++){
                    str += matrix[i][j] + "\t";
                }

                System.out.println(str + "|");
                str = "|\t";
            }

        }catch(Exception e){System.out.println("Matrix is empty!!");}
    }

    //gets the 2D double arrays from the .mat file
    public static void getReaderData(MatFileReader reader) {
        busMLArray = (MLNumericArray) reader.getContent().get("bus");
        generationMLArray = (MLNumericArray) reader.getContent().get("gen");
        branchMLArray = (MLNumericArray) reader.getContent().get("branch");
        generationCostMLArray = (MLNumericArray) reader.getContent().get("gencost");
    }

    private static void handleUserInput() {
        //print user instructions
        Scanner reader = new Scanner (System.in);
        System.out.println("=====================================================================");
        System.out.println("======================= AC Optimal Power Flow =======================");
        System.out.println("=====================================================================");
        System.out.println("User Directory: "+ Constants.DEFAULT_FOLDER_PATH);
        System.out.println("\n1. For using the default folder userInput, " +
                "please enter \"default\".");
        System.out.println("\tdefault folder name is \"input\" in the project userInput");
        System.out.println("2. OR for  creating a new folder, " +
                "please enter the name of the folder below");
        System.out.println("3. OR enter \"quit\" to exit.");
        String userInput = reader.nextLine();

        //check if the userInput given is acceptable
        if(userInput.equalsIgnoreCase("default")){
            System.out.println("\nThe default folder input is chosen!");
            filePath=Constants.DEFAULT_FOLDER_PATH+ File.separator +Constants.DEFAULT_FOLDER_NAME;
        } else if (userInput.equalsIgnoreCase("quit")){
            System.out.println("\nThe programme is terminated!");
            reader.close();
            System.exit(1);
        } else{
            filePath=Constants.DEFAULT_FOLDER_PATH+ File.separator + userInput;
            System.out.println("You chose "+filePath);
            File newFolder = new File((filePath));
            if (!newFolder.exists()){
                if (newFolder.mkdir()){
                    System.out.println("Directory is created!");
                } else {
                    System.out.println("Failed to create userInput!");
                }
            }
        }

        //determine the number of simulation periods
        System.out.println("\nPlease enter the number of simulation periods for the AC OPF:");
        try{
            periods = reader.nextInt();
        }
        catch (InputMismatchException a) {
            System.out.println("Wrong format, input is expected to be an integer!");
        }

        //determine the M-file containing the network data
        System.out.println("\nPlease enter the name of the .M file containing the network data:");
        matFilePath = reader.next();

        System.out.println("\nNumber of simulation periods: "+periods);
        System.out.println("Directory: "+filePath);
        System.out.println("MATLAB file:"+ matFilePath);
        System.out.println("FilePath:"+filePath+File.separator+matFilePath);
        System.out.println("=====================================================================");
        reader.close();
    }

    private static void simulationTimer(long timer) {
        timer = timer / 1000000;
        int hours = (int) (timer / 3600000);
        int minutes = (int) (timer / 60000 - 60 * hours);
        int seconds = (int) (timer / 1000 - 3600 * hours - 60 * minutes);
        int milliSeconds = (int) (timer - 3600000 * hours - 60000 * minutes - 1000 * seconds);
        System.out.println();
        System.out.print("The program has been running for approximately: ");
        System.out.print(hours + " h : ");
        System.out.print(minutes + " m : ");
        System.out.print(seconds + " s : ");
        System.out.print(milliSeconds + " ms\n\n");
    }
}

