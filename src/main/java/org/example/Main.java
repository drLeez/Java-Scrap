package org.example;

import DeSerialize.DeSerializeTest;
import Pool.PoolTest;

import java.io.IOException;

public class Main
{
    public static void main(String[] argz) throws IOException
    {
        System.out.println("Hello world!");

//        PoolTest.Init();

        DeSerializeTest.Init();
    }
}