import java.lang.Math;

public class MathToolkit {

    public double sum;

    //  sigma notation
    public double sigma (double x, int n){
        sum=0;
        for (int i=0;i<=n;i++){
            sum += x;
        }
        return sum;
    }

}
