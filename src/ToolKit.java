import java.lang.Math;

public class ToolKit {

    public double sum;

    //  sigma notation
    public double sigma (double x, int n){
        sum=0;
        for (int i=0;i<=n;i++){
            sum += x;
        }
        return sum;
    }

    public double roundDouble(double number, int decimal){
        return Math.round(number * Math.pow(10, (double)decimal))
                / Math.pow(10, (double) decimal);
    }

    public static void print() {
        System.out.println("TEST TEST TEST");
    }

}
