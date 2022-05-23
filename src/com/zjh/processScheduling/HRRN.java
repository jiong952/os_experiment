package com.zjh.processScheduling;

import java.util.*;

/**
 * @author 张俊鸿
 * @description: 高响应比调度算法
 * @since 2022-05-20 9:14
 */
public class HRRN {
    static Scanner scanner = new Scanner(System.in);
    /**
     * 进程控制块PCB
     */
    static class PCB implements Comparable<PCB>{
        String name; // 进程名
        int arriveTime; // 到达时间
        int serveTime; // 期待运行时间
        int waitTime; // 等待时间
        int beginTime; // 开始时间
        int hasRunTime = 0; // 已经运行时间
        int turnRoundTime; // 周转时间
        double priority; // 优先权 【还没到达优先权为0】
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
    //使用优先队列将输入的进程自动按照到达时间排序,模拟按照时间到达
    static Queue<PCB> pcb_queue = new PriorityQueue<>();

    /**
     * 输入进程信息
     */
    public static void input(){
        System.out.print("请输入进程数：");
        int pcb_num = scanner.nextInt();
        pcbArrays = new PCB[pcb_num];
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
     * 检查是否有到达的进程，有就加入就绪列表中
     *
     * @param readyList 就绪列表
     * @param cpuTime    cpu当前的时间
     */
    public static void check(List<PCB> readyList, int cpuTime){
        while (true){
            //查看到达的进程，直到有进程没到达就退出
            PCB pcb_first = pcb_queue.peek();
            if(pcb_first == null){
                //全部加入就绪队列了
                break;
            }else if(pcb_first.arriveTime <= cpuTime){//判断是否到达
                //已就绪，弹出排队队列，加入就绪队列
                readyList.add(pcb_queue.poll());
            }else {
                //没有就绪的进程
                break;
            }
        }
    }
    /**
     * 计算优先级并弹出最大优先级的进程
     *
     * @param readyList 就绪列表
     * @param cpuTime   cpu时间
     * @return {@link PCB}
     */
    public static PCB getByPriority(List<PCB> readyList, int cpuTime){
        //遍历list
        double max = 0.0;
        int index = 0;
        for (int i = 0; i < readyList.size(); i++) {
            readyList.get(i).waitTime = cpuTime - readyList.get(i).arriveTime;
            readyList.get(i).priority = (readyList.get(i).waitTime + readyList.get(i).serveTime) / readyList.get(i).serveTime;
            if(readyList.get(i).priority >= max) {
                max = readyList.get(i).priority;
                index = i;
            }
        }
        PCB pcb = readyList.get(index);
        readyList.remove(index);
        return pcb;
    }

    /**
     * 打印当前进程
     *
     * @param pcb     印刷电路板
     * @param cpuTime cpu时间
     */
    public static void printCurrent(PCB pcb, int cpuTime){
        System.out.println("当前时间:"+cpuTime+"\t运行的进程:"+pcb.name+"\t剩余运行时间:"+(pcb.serveTime-pcb.hasRunTime));
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
        List<PCB> readyList = new ArrayList<>();
        //当前运行的进程
        PCB runningPcb = null;
        while (true){
            System.out.println();
            //判断是否结束
            if(pcb_queue.isEmpty() && readyList.isEmpty() && runningPcb == null){
                System.out.println("======所有进程运行结束======");
                break;
            }else {
                //处理进程
                //先查看是否有到达的进程
                check(readyList,cpuTime);
                //上一个运行完毕，要取出新的
                if(runningPcb == null){
                    runningPcb = getByPriority(readyList,cpuTime);
                    //响应时间
                    runningPcb.beginTime = cpuTime;
                    printCurrent(runningPcb,cpuTime);
                    runningPcb.hasRunTime++;
                    cpuTime++;
                }else {
                    //当前有进程正在运行
                    //判断是否结束
                    if(runningPcb.hasRunTime == runningPcb.serveTime){
                        //进程结束
                        runningPcb.turnRoundTime = cpuTime - runningPcb.arriveTime;
                        System.out.println("======进程"+runningPcb.name+"运行结束======");
                        //置空
                        runningPcb = null;
                        continue;
                    }
                    printCurrent(runningPcb,cpuTime);
                    runningPcb.hasRunTime++;
                    cpuTime++;
                }
            }
        }
    }


    /**
     * 显示周转时间
     */
    private static void showTurnAroundTime() {
        double averageT = 0;
        double averageWTAT = 0;
        System.out.println("进程\t 周转时间\t 带权周转时间\t 响应时间\t");
        for (int i = 0; i < pcbArrays.length; i ++) {
            int turnAroundTime = pcbArrays[i].turnRoundTime;
            double weightTurnAroundTime = turnAroundTime*1.0/pcbArrays[i].serveTime;
            int beginTime = pcbArrays[i].beginTime;
            System.out.printf(pcbArrays[i].name+"\t   "+turnAroundTime+"\t\t   ");
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
        System.out.println("======非抢占式高响应比调度算法======");
        run();
        showTurnAroundTime();
    }







}
