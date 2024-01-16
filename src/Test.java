/* $
javac -d ./class -cp "./class;lib/purejavahidapi.jar;./lib/jna-4.0.0.jar" -sourcepath ./src -encoding UTF-8 ./src/Test.java;
java -cp "./class;lib/purejavahidapi.jar;./lib/jna-4.0.0.jar" Test;
*/ 

/*
javac -d ./bin -cp "./bin;lib/purejavahidapi.jar;./lib/jna-4.0.0.jar" -sourcepath ./src -encoding UTF-8 ./src/Test.java;
*/

import test.Test2;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        Test2.main(args);
        for (int i = 5; i > 0; i--) {
            System.out.print("\r" + i + "秒後に終了します...");
            Thread.sleep(1000);
        }
        System.out.println();
        
    }
}
