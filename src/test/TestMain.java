/* $
javac -d ./class -cp "./class;lib/purejavahidapi.jar;./lib/jna-4.0.0.jar" -sourcepath ./src -encoding UTF-8 ./src/test/TestMain.java;
java -cp "./class;lib/purejavahidapi.jar;./lib/jna-4.0.0.jar" test.TestMain;
*/

/*
javac -d ./bin -cp "./bin;lib/purejavahidapi.jar;./lib/jna-4.0.0.jar" -sourcepath ./src -encoding UTF-8 ./src/test/TestMain.java;
*/

package test;

import joyconapi.util.*;


public class TestMain {
    public static void main(String[] args) throws InterruptedException {
        Debugger.common.inactivate();

        Test2.main(args);
        for (int i = 5; i > 0; i--) {
            System.out.print("\r" + i + "秒後に終了します...");
            Thread.sleep(1000);
        }
        
        System.out.println("\r終了します               ");
    }
}
