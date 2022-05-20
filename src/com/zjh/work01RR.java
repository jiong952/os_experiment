package com.zjh;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

/**
 * @author 张俊鸿
 * @description: 时间片轮转算法
 * @since 2022-05-09 19:20
 */
public class work01RR {
    static Scanner scanner = new Scanner(System.in);

    /**
     * 进程控制块PCB
     */
    static class PCB implements Comparable<PCB>{
        String name; // 进程名
        int arriveTime; // 到达时间
        int serveTime; // 运行时间
        int hasRunTime = 0; // 已经运行时间
        int beginTime; // 开始时间
        int turnRoundTime; //周转时间
        int avgTurnRoundTime; //带权周转时间
        @Override
        public int compareTo(PCB o) { // 按照 到达时间 进入就绪队列
            return  this.arriveTime - o.arriveTime;
        }

        public PCB(String name, int arriveTime, int serveTime) {
            this.name = name;
            this.arriveTime = arriveTime;
            this.serveTime = serveTime;
        }
    }
    static PCB[] pcbArrays; // 进程数组
    static int timeSlice; //时间片
    //使用优先队列将输入的进程自动按照到达时间排序,模拟按照时间到达
    static Queue<PCB> pcb_queue = new PriorityQueue<>();

    /**
     * 输入进程信息
     */
    public static void input(){
        System.out.print("请输入进程数：");
        int pcb_num = scanner.nextInt();
        pcbArrays = new PCB[pcb_num];
        System.out.print("请输入时间片大小：");
        timeSlice = scanner.nextInt();
        for (int i = 0; i < pcb_num; i++) {
            System.out.println("进程号"+ (i+1));
            System.out.print("请输入进程名：");
            String name = scanner.next();
            System.out.print("请输入到达时间：");
            int arriveTime = scanner.nextInt();
            System.out.print("请输入服务时间：");
            int serveTime = scanner.nextInt();
            PCB pcb = new PCB(name, arriveTime, serveTime);
            //放进数组
            pcbArrays[i] = pcb;
            //加入pcb队列
            pcb_queue.offer(pcb);
        }
    }

    /**
     * 检查是否有到达的进程，有就加入就绪队列中
     *
     * @param readyQueue 就绪队列
     * @param cpuTime    cpu当前的时间
     */
    public static void check(Queue<PCB> readyQueue, int cpuTime){
        while (true){
            //查看到达的进程，直到有进程没到达就退出
            PCB pcb_first = pcb_queue.peek();
            if(pcb_first == null){
                //全部加入就绪队列了
                break;
            }else if(pcb_first.arriveTime <= cpuTime){//判断是否到达
                //已就绪，弹出排队队列，加入就绪队列
                readyQueue.offer(pcb_queue.poll());
            }else {
                //没有就绪的进程
                break;
            }
        }
    }

    public static void printCurrent(PCB pcb,int cpuTime){
        System.out.println("当前时间:"+cpuTime+"\t运行的进程:"+pcb.name+"\t剩余运行时间:"+(pcb.serveTime-pcb.hasRunTime));
    }

    /**
     * 一个时间片内进程运行
     *
     * @param pcb     进程控制块
     * @param cpuTime cpu时间
     * @return boolean 是否提前退出
     */
    static boolean pcbRun(PCB pcb,int cpuTime){
        if(pcb.hasRunTime == 0){
            //首次响应时间
            pcb.beginTime = cpuTime;
        }
        printCurrent(pcb,cpuTime);
        pcb.hasRunTime++;
        if(pcb.hasRunTime == pcb.serveTime){
            //进程结束
            pcb.turnRoundTime = cpuTime + 1 - pcb.arriveTime;
            return true;
        }
        return false;
    }

    /**
     * cpu开始运行
     */
    public static void run(){
        int cpuTime = 0;
        //cpu开始工作时间是第一个到达的时间
        if(!pcb_queue.isEmpty()){
            cpuTime = pcb_queue.peek().arriveTime;
        }
        Queue<PCB> readyQueue = new LinkedList<>(); //就绪队列
        int timeSliceFlag = timeSlice;
        PCB runningPcb = null;
        while (true){
            System.out.println();
            //判断是否结束
            if(pcb_queue.isEmpty() && readyQueue.isEmpty() && runningPcb== null){
                System.out.println("======所有进程运行结束======");
                break;
            }else {
                //处理进程
                if(timeSliceFlag % timeSlice == 0){
                    //一个时间片结束
                    check(readyQueue,cpuTime);
                    if(runningPcb != null){
                        readyQueue.offer(runningPcb);
                        runningPcb = null;
                    }
                    //取出要运行的进程
                    runningPcb = readyQueue.poll();
                    if(runningPcb == null){
                        //当前没有可以运行的pcb
                        cpuTime++;
                        continue;
                    }
                }
                //时间片没到，判断是否有进程提前结束
                if(pcbRun(runningPcb, cpuTime)){
                    runningPcb = null;
                    timeSliceFlag = timeSlice;
                }else {
                    timeSliceFlag++;
                }
                cpuTime++;
            }
        }
    }
    // 周转时间
    private static void showTurnAroundTime() {
        double averageT = 0;
        double averageWTAT = 0;
        System.out.println("进程\t 周转时间\t 带权周转时间\t 响应时间\t");
        for (int i = 0; i < pcbArrays.length; i ++) {
            int turnAroundTime = pcbArrays[i].turnRoundTime;
            double weightTurnAroundTime = turnAroundTime*1.0/pcbArrays[i].serveTime;
            int beginTime = pcbArrays[i].beginTime;
            System.out.printf(i+"\t   "+turnAroundTime+"\t\t   ");
            System.out.printf("%.1f", weightTurnAroundTime);
            System.out.print("\t\t  "+beginTime+"\n");
            averageT += turnAroundTime;
            averageWTAT += weightTurnAroundTime;
        }
        averageT /= pcbArrays.length;
        averageWTAT /= pcbArrays.length;
        System.out.println("平均周转时间：" + averageT);
        System.out.printf("平均带权周转时间：%.2f\n", averageWTAT);
    }
    public static void main(String[] args) {
        input();
        run();
        showTurnAroundTime();
    }
}
