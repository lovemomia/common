package cn.momia.common.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MobileUtilTest {
    @Test
    public void testValidation() {
        String mobile1 = null;
        String mobile2 = "";
        String mobile3 = "12345678910";
        String mobile4 = "12345x78910";
        String mobile5 = "22345678910";
        String mobile6 = "x2345678910";
        String mobile7 = "22345x78910";

        Assert.assertTrue(MobileUtil.isInvalid(mobile1));
        Assert.assertTrue(MobileUtil.isInvalid(mobile2));
        Assert.assertFalse(MobileUtil.isInvalid(mobile3));
        Assert.assertTrue(MobileUtil.isInvalid(mobile4));
        Assert.assertTrue(MobileUtil.isInvalid(mobile5));
        Assert.assertTrue(MobileUtil.isInvalid(mobile6));
        Assert.assertTrue(MobileUtil.isInvalid(mobile7));
    }

    @Test
    public void testEncrypt() {
        Assert.assertEquals(MobileUtil.encrypt("22345678910"), "");
        Assert.assertEquals(MobileUtil.encrypt("12345678910"), "123****8910");
    }
}
