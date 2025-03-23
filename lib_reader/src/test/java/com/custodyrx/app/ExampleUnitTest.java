package com.custodyrx.app;

import com.payne.reader.bean.config.Cmd;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        // byte[] bl={1,2,3};
        // byte[] br={11,22,33};
        //
        // byte[] bytes = ArrayUtils.mergeBytes(bl, br);
        //
        // System.out.println(Arrays.toString(bytes));

        System.out.println(Cmd.getNameForCmd(Cmd.TEMPERATURE_LABEL_COMMAND));
    }
}