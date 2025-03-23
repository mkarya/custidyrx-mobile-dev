package com.custodyrx.app;

import com.payne.reader.util.ArrayUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        byte[] str = ArrayUtils.hexStringToBytes("11 12 13");
        System.out.println(Arrays.toString(str));

        ArrayList al = new ArrayList<>();
        al.add("qwe");
        al.add("111");

        ArrayList clone = (ArrayList) al.clone();
        clone.add("333");


        al.set(0, "666");

        System.out.println(al + "\n" + clone);
    }
}
