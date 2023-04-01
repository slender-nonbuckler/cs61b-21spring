package flik;
import org.junit.Test;
import static org.junit.Assert.*;

public class Filktest {
    @Test
    public void compare100(){
        int[] a = new int [200];
        for (int i =0;i<200;i++){
            a[i]=i;
        }
        for (int i =0;i<200;i++){
            assertTrue("should have the same value " + i, Flik.isSameNumber(a[i],i));
        }
    }
}
