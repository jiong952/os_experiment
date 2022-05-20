package com.zjh.dynamicPartition;

import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author 张俊鸿
 * @description: 循环首次适应算法
 * @since 2022-05-20 12:19
 */
public class NextFit {
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

        public Zone(int size, int head) {
            this.size = size;
            this.head = head;
            this.state = "空闲";
        }
    }
    /** 内存总大小 **/
    public static int memorySize;
    /** 空闲分区表 **/
    public static LinkedList<Zone> freeZones;
    /** 上一次分配的空闲分区下标 **/
    public  static  int last;

    /**
     * 初始化内存
     *
     * @param memorySize 内存大小
     */
    public NextFit(int memorySize) {
        this.memorySize = memorySize;
        this.freeZones = new LinkedList<>();
        this.last = 0;
        //向分区表加入空闲分区
        freeZones.add(new Zone(memorySize,0));
    }

    /**
     * 寻找空闲分区
     *
     * @param size 大小
     */
    public void findFree(int size){
        //获得上一次分配的空闲分区
        Zone lastZone = freeZones.get(last);
        //判断是否还足够继续分
        //不够则从last开始遍历，找到一个，分配，更新下标
        if(lastZone.state.equals("空闲") && (lastZone.size > size)){
            //该空闲区足够
            allocation(size,last,lastZone);
            return;
        }else {
            //大小不够，循环寻找下一个
            int zoneNum = freeZones.size();
            for (int i = ((last + 1) % zoneNum); i != last; i = (i+1) % zoneNum) {
                Zone temp = freeZones.get(i);
                if(temp.state.equals("空闲") && (temp.size > size)){
                    //该空闲区足够 分配
                    allocation(size,i,temp);
                    //更新下标
                    this.last = i;
                    return;
                }
            }
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
     */
    public void allocation(int size, int index, Zone freeZone){
        //创建新空闲分区
        Zone newZone = new Zone( freeZone.size - size,freeZone.head + size);
        freeZones.add(index + 1,newZone);
        //将当前分区置为占用状态
        freeZone.size = size;
        freeZone.state = "占用";
    }

    /**
     * 回收分区
     *
     * @param index 下标
     */
    public void recycle(int index){
        //判断是否存在该分区
        if(index >= freeZones.size()){
            System.out.println("请输入正确的分区号!");
            return;
        }
        //判断分区是否被分配
        Zone zone = freeZones.get(index);
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
            //移除后两个
            freeZones.remove(index);
            freeZones.remove(index + 1);
            //更新下标为前一个
            last = index - 1;
        }else if(front && !next){
            //前一个是空的，后一个不是
            Zone frontZone = freeZones.get(index - 1);
            frontZone.size = frontZone.size + zone.size;
            //移除
            freeZones.remove(index);
            //更新下标为前一个
            last = index - 1;
            zone.state = "空闲";
        }else if(!front && next){
            //后一个是空的，前一个不是
            Zone nextZone = freeZones.get(index + 1);
            zone.size = zone.size + nextZone.size;
            //移除后一个
            freeZones.remove(index + 1);
            zone.state = "空闲";
        }else {
            //前后都非空，改变该分区状态
            zone.state = "空闲";
        }
        System.out.println("成功回收"+zone.size+"大小的空间");
    }

    /**
     * 打印当前分区表
     */
    private void printCurrent() {
        System.out.println("分区编号\t分区始址\t分区大小\t空闲状态\t");
        for (int i = 0; i < freeZones.size(); i++){
            Zone zone = freeZones.get(i);
            System.out.println(i + "\t\t" + zone.head + "\t\t" +
                    zone.size + "  \t" + zone.state);
        }
    }


    public static void main(String[] args) {
        System.out.println("=========循环首次适应算法============");
        System.out.println("请输入初始内存总大小");
        int memorySize = scanner.nextInt();
        //初始化
        NextFit nextFit = new NextFit(memorySize);
        while(true) {
            System.out.println("请输入要分配内存还是要释放内存");
            System.out.println("1 分配内存 2 释放内存");
            int choice =scanner.nextInt();
            switch(choice) {
                case 1:{
                    System.out.println("请输入要分配的内存大小");
                    int size = scanner.nextInt();
                    nextFit.findFree(size);
                    nextFit.printCurrent();
                    break;
                }
                case 2:{
                    System.out.println("输入想要释放内存的分区号");
                    int index = scanner.nextInt();
                    nextFit.recycle(index);
                    nextFit.printCurrent();
                    break;
                }
            }
        }
    }


}
