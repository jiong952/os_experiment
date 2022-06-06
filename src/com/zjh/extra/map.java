package com.zjh.extra;

/**
 * @author 张俊鸿
 * @description: 位示图
 * @since 2022-06-06 17:12
 */


import java.util.Random;
import java.util.Scanner;

public class map {
    public static void main(String[] args) {
        Bitmap bitmap = new Bitmap();
        int i;
        do {
            System.out.println("==========位示图=========");
            System.out.println("0.申请盘块");
            System.out.println("1.释放盘块");
            System.out.println("2.打印位示图");
            System.out.println("3.打印盘块号");
            System.out.println("4.退出操作");
            System.out.print("请输入想要操作的序号（0-4）：");
            System.out.println("=======================");
            Scanner scanner = new Scanner(System.in);
            i = scanner.nextInt();
            switch (i) {
                case 0:
                    System.out.print("请输入所需要的连续块数：");
                    int num = scanner.nextInt();
                    bitmap.distribution(num);
                    break;
                case 1:
                    bitmap.release();
                    break;
                case 2:
                    bitmap.printBM();
                    break;
                case 3:
                    bitmap.printBlock();
                    break;
                case 4:
                    break;
            }
        } while (i != 4);
        System.out.println("运行结束");
    }

}

class Bitmap {
    // 行数
    final int size = 2000;
    // 列数
    final int length = 32;
    // 位示图
    int[][] bitmap;
    // 盘块号
    int[][] block;

    // 构造方法,初始化位示图
    public Bitmap() {

        bitmap = new int[size][length];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < length; j++) {
                int random = new Random().nextInt(2);
                // 随机赋予0或1
                bitmap[i][j] = random;
            }
        }

    }

    // 输出位示图
    public void printBM() {
        System.out.println("位示图:");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < length; j++) System.out.print(bitmap[i][j] + "  ");
            System.out.println();
        }
        System.out.println("-----------------------");
    }

    // 输出盘块号
    public void printBlock() {
        block = new int[size][length];

        System.out.println("block:");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < length; j++) {
                // 计算对应的盘块号
                block[i][j] = i * length + j;
                System.out.print(block[i][j] + "   \t");
            }
            System.out.println();
        }
        System.out.println("----------------------------------------------------------");
    }

    // 分配盘块号
    public void distribution(int count){
        int tag = 0;

        //打印分配前的位示图
        System.out.println("分配前的位示图：");
        printBM();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < length; j++) {
                if (bitmap[i][j] == 0){
                    tag++;
                    if(tag == count){
                        // 分配成功，置1
                        for (int k = i; k >= 0; k--) {
                            for (int l = j; l >= 0; l--) {
                                bitmap[k][l] = 1;
                                tag--;
                                if(tag == 0) break;
                            }
                            if(tag == 0) break;
                        }

                        //打印分配后的位示图
                        System.out.println("分配后的位示图：");
                        printBM();
                        return;
                    }
                }

                if(bitmap[i][j] == 1){
                    tag = 0;
                }
            }
        }
        // 未找到空间可分配
        System.out.println("存储空间不够，分配失败");
    }

    //释放盘块号
    public void release() {
        System.out.print("请输入要释放的盘块号：");
        Scanner scanner = new Scanner(System.in);
        int random = scanner.nextInt();

        int x = random / length;
        int y = random % length;
        int tag = bitmap[x][y];

        if(tag == 0) {
            // 盘块号为空
            System.out.println("该盘块号无需被释放");
        } else {
            // 盘块号不为空,释放
            //打印释放前的位示图
            System.out.println("释放前的位示图：");
            printBM();
            bitmap[x][y] = 0;

            //打印释放后的位示图
            System.out.println("释放后的位示图：");
            printBM();
        }
    }
}









