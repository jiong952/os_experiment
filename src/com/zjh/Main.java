package com.zjh;


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Memory my = new Memory();
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("请输入要分配内存还是要释放内存");
            System.out.println("1 分配内存 2 释放内存");
            int n =sc.nextInt();
            switch(n) {

                case 1:{
                    System.out.println("请输入要分配的内存大小");

                    int size = sc.nextInt();
                    my.allocation(size);
                    my.showZones();
                    break;
                }
                case 2:{
                    System.out.println("输入想要释放内存的分区号");
                    int id = sc.nextInt();
                    my.collection(id);
                    break;
                }
            }
        }
    }
}

