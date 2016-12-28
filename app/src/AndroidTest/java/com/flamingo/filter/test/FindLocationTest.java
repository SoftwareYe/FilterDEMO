package com.flamingo.filter.test;

import android.provider.Settings;
import android.test.InstrumentationTestCase;

import com.flamingo.filterdemo.tools.FindLocation;

import org.junit.Test;

/**
 * Created by matrix on 16/6/22.
 */
public class FindLocationTest extends InstrumentationTestCase {

    //@Test
    public void testGetLocation(){
        System.out.println("hehe");
        String result = FindLocation.getLocation("18814122690");
        System.out.println("hehe"+result);
        assertEquals("广东",result);
    }

}
