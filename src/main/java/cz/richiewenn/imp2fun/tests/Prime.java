package cz.richiewenn.imp2fun.tests;

public class Prime {
    public int prime() {
        int Nth = 20;
        int num = 1;
        int count = 0;
        for(; count < Nth; num = num + 1) {
            int i = 2;
            for(; num%i != 0; i = i + 1);

            if(i == num) {
                count = count + 1;
            }
        }
        return num;
    }
}