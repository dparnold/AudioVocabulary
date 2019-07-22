package com.dparnold.audiovocabulary.Helper;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by dominik on 2/5/18.
 */

public class Util {
    public static int[] shuffleArray(int n){
        Random rnd = ThreadLocalRandom.current();
        int[] shuffledArray = new int[n];
        int index;
        for(int i =n-1;i>=0;i--){
            index = rnd.nextInt(n);
            shuffledArray[i] = index;
        }
        return shuffledArray;
    }

    public static String int2StringDigits (int integer, int digits){
        int integerCopy = integer;
        String string = "";
        int integerDigits = 1;
        System.out.println(Integer.toString(integerCopy));
        while (integerCopy>9){
            integerDigits+=1;
            integerCopy=integerCopy/10;
        }
        while(integerDigits<digits){
            string=string+"0";
            digits= digits-1;
        }
        string=string+Integer.toString(integer);
        return string;
    }

}
