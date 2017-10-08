package cz.richiewenn.imp2fun.tests;

public class Fibonacci {

    public int[] fibonacci(int count) {
        int[] feb = new int[count];
        feb[0] = 0;
        feb[1] = 1;
        for(int i=2; i < count; i++){
            feb[i] = feb[i-1] + feb[i-2];
        }
        return feb;
    }
}
