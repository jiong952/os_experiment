package com.zjh.dynamicPartition;

import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author 张俊鸿
 * @description: 首次适应算法
 * @since 2022-05-20 16:10
 */
public class FirstFit {
    static Scanner scanner = new Scanner(System.in);
    /**
     * 内存分区
     */
    static class Zone{
        /** 大小 **/
        private int size;
        /** 初始地址 **/
        private int head;
        /** 当前状态 **/
        private String state;
        /** 作业号 **/
        private int num; //-1表示空闲

        public Zone(int size, int head, int num) {
            this.size = size;
            this.head = head;
            this.state = "空闲";
            this.num = num;
        }
    }
    /** 内存总大小 **/
    public static int memorySize;
    /** 空闲分区表 **/
    public static LinkedList<Zone> freeZones;

    /**
     * 初始化内存
     *
     * @param memorySize 内存大小
     */
    public FirstFit(int memorySize) {
        this.memorySize = memorySize;
        this.freeZones = new LinkedList<>();
        //向分区表加入空闲分区
        freeZones.add(new Zone(memorySize,0,-1));
    }

    /**
     * 找到第一个足够大的分区
     *
     * @param size 大小
     * @param num  作业号
     */
    public void findFirstFree(int size,int num) {
        //最佳分区的下标
        int index = 0;
        //判断是否找到
        boolean flag = false;
        //遍历寻找
        for (int i = 0; i < freeZones.size(); i++) {
            if(freeZones.get(i).state.equals("空闲") && (freeZones.get(i).size > size)){
                //找到足够大的了
                index = i;
                flag = true;
                break;
            }
        }
        if (flag){
            allocation(size,index,freeZones.get(index),num);
        }else {
            //遍历完找不到没有空闲分区
            System.out.println("不存在可以存放"+size+"的内存分区");
        }
    }

    /**
     * 分配内存
     *
     * @param size     大小
     * @param index    下标
     * @param freeZone 空闲分区
     * @param num 作业号
     */
    public void allocation(int size, int index, Zone freeZone,int num){
        //创建新空闲分区
        Zone newZone = new Zone( freeZone.size - size,freeZone.head + size,-1);
        freeZones.add(index + 1,newZone);
        //将当前分区置为占用状态
        freeZone.size = size;
        freeZone.state = "占用";
        freeZone.num = num;
    }

    /**
     * 回收分区
     *
     * @param num 下标
     */
    public void recycle(int num){
        Zone zone = null;
        int index = -1;
        //遍历查找
        for (int i = 0; i < freeZones.size(); i++) {
            if(freeZones.get(i).num == num){
                zone = freeZones.get(i);
                index = i;
                break;
            }
        }
        //判断是否存在该分区
        if(zone == null){
            System.out.println("请输入正确的分区号!");
            return;
        }
        //判断分区是否被分配
        if(zone.state.equals("空闲")){
            System.out.println("此分区是空闲分区，无需回收");
            return;
        }
        //判断前后分区是否是空闲的，是则进行合并
        boolean front = false;
        boolean next = false;
        if(index - 1 >= 0){
            front = freeZones.get(index - 1).state.equals("空闲");
        }
        if(index + 1 <= freeZones.size() - 1){
            next = freeZones.get(index + 1).state.equals("空闲");
        }
        if(front && next){
            //前后都是空的,直接把三个都变成一个，下标变为前一个的
            Zone frontZone = freeZones.get(index - 1);
            Zone nextZone = freeZones.get(index + 1);
            frontZone.size = frontZone.size + zone.size + nextZone.size;
            frontZone.num = -1;
            //移除后两个
            freeZones.remove(index + 1);
            freeZones.remove(index);
        }else if(front && !next){
            //前一个是空的，后一个不是
            Zone frontZone = freeZones.get(index - 1);
            frontZone.size = frontZone.size + zone.size;
            //移除
            freeZones.remove(index);
            zone.state = "空闲";
            zone.num = -1;
        }else if(!front && next){
            //后一个是空的，前一个不是
            Zone nextZone = freeZones.get(index + 1);
            zone.size = zone.size + nextZone.size;
            //移除后一个
            freeZones.remove(index + 1);
            zone.state = "空闲";
            zone.num = -1;
        }else {
            //前后都非空，改变该分区状态
            zone.state = "空闲";
            zone.num = -1;
        }
        System.out.println("成功回收"+zone.size+"大小的空间");
    }

    /**
     * 打印当前分区表
     */
    private void printCurrent() {
        System.out.println("分区编号\t分区始址\t分区大小\t空闲状态\t作业号");
        for (int i = 0; i < freeZones.size(); i++){
            Zone zone = freeZones.get(i);
            System.out.println(i + "\t\t" + zone.head + "\t\t" +
                    zone.size + "  \t" + zone.state+ "  \t" + (zone.num == -1 ? "": zone.num));
        }
    }

    public static void main(String[] args) {
        System.out.println("=========首次适应算法============");
        System.out.println("请输入初始内存总大小");
        int memorySize = scanner.nextInt();
        //初始化
        FirstFit firstFit = new FirstFit(memorySize);
        while(true) {
            System.out.println("请输入要分配内存还是要释放内存");
            System.out.println("1 分配内存 2 释放内存");
            int choice =scanner.nextInt();
            switch(choice) {
                case 1:{
                    System.out.println("请输入分配内存的作业号：");
                    int num = scanner.nextInt();
                    System.out.println("请输入要分配的内存大小");
                    int size = scanner.nextInt();
                    firstFit.findFirstFree(size,num);
                    firstFit.printCurrent();
                    break;
                }
                case 2:{
                    System.out.println("输入想要释放内存的作业号");
                    int num = scanner.nextInt();
                    firstFit.recycle(num);
                    firstFit.printCurrent();
                    break;
                }
            }
        }
    }
}
