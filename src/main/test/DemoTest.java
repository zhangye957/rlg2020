import com.itdr.common.ServerResponse;
import com.itdr.utils.BigDecimalUtil;
import org.junit.Test;

import java.math.BigDecimal;

public class DemoTest {

    @Test
    public void test1(){
        double a= 0.1;
        double b= 0.2;
        System.out.println(a*b);

        BigDecimal c= new BigDecimal(a);
        BigDecimal d= new BigDecimal(b);

        System.out.println(c.multiply(d));

        BigDecimal mul = BigDecimalUtil.mul(a,b);
        System.out.println(mul);
    }
}
