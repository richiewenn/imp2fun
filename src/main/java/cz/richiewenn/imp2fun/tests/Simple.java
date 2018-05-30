package cz.richiewenn.imp2fun.tests;

public class Simple {
    public int simple() {
        int a = 0;
        int b = 0;
        for (int i = 0; i < 10; i++) {
            a++;
            if(a % 2 == 0) {
                b++;
            }
        }
        int c = a + b;
        return c;
    }
}


