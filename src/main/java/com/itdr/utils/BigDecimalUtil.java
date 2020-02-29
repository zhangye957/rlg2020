package com.itdr.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {
    /**
     *乘法运算，保留两位小数，四舍五入
     */
    public static BigDecimal mul(double d1, double d2){
        BigDecimal bigDecimal=new BigDecimal(String.valueOf(d1));
        BigDecimal bigDecimal2=new BigDecimal(String.valueOf(d2));
        return bigDecimal.multiply(bigDecimal2);
    }
    /**
     *除法运算，保留两位小数，四舍五入
     */
    public static BigDecimal div(double d1,double d2){
        BigDecimal bigDecimal=new BigDecimal(String.valueOf(d1));
        BigDecimal bigDecimal2=new BigDecimal(String.valueOf(d2));
        return bigDecimal.divide(bigDecimal2,2,BigDecimal.ROUND_HALF_UP);
    }
    /**
     *加法运算，保留两位小数，四舍五入
     */
    public static BigDecimal add(double d1,double d2){
        BigDecimal bigDecimal=new BigDecimal(String.valueOf(d1));
        BigDecimal bigDecimal2=new BigDecimal(String.valueOf(d2));
        return bigDecimal.add(bigDecimal2);
    }
    /**
     *减法运算，保留两位小数，四舍五入
     */
    public static BigDecimal sub(double d1,double d2){
        BigDecimal bigDecimal=new BigDecimal(String.valueOf(d1));
        BigDecimal bigDecimal2=new BigDecimal(String.valueOf(d2));
        return bigDecimal.subtract(bigDecimal2);
    }
}
