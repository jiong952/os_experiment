package com.zjh.extra;
import java.util.*;


/**
 * @author 张俊鸿
 * @description: 多级反馈调度队列
 * @since 2022-06-05 3:49
 */
public class MSFQS {
    private static Queue<Process> firstQueue = new LinkedList<>();
    private static Queue<Process> secondQueue = new LinkedList<>();
    private static Queue<Process> thirdQueue = new LinkedList<>();
    private static int firstTimeSlice;  //第一队列时间片
    private static int secondTimeSlice; //第二队列时间片
    private static int thirdTimeSlice;  //第三队列时间片
    private static int processNum;     //进程数量
    private static Scanner scanner = new Scanner(System.in);
    private static Process[] processArray; //进程数组

    /**
     * 进程类
     */
    private static class Process implements Comparable<Process> {
        String name;     //进程号
        int arriveTime; //到达时间
        int serveTime;   //运行时间
        int needTime;  //仍需时间
        int turnRoundTime; //周转时间
        String state;    //进程状态   等待 运行 完成

        @Override
        public String toString() {
            System.out.println();
            return String.format("进程%s: %10d %7d %8d    %s\n", name, arriveTime, serveTime, needTime, state);
        }

        @Override
        public int compareTo( Process b ) {
            //按reachTime从小到大排序
            return Float.compare(arriveTime, b.arriveTime);
        }
    }

    /**
     * 运行
     */
    private static void running(){
        int firstCpu = firstTimeSlice;
        int secondCpu = secondTimeSlice;
        int thirdCpu = thirdTimeSlice;
        int cpuTime = 0;
        int num = 0;
        /*当有进程未运行时或进程队列不为空时，以每1时间片为单位*/
        while(num < processNum || !firstQueue.isEmpty() || !secondQueue.isEmpty() || !thirdQueue.isEmpty()){
            /*当前时刻有进程到达，则添加入第一队列*/
            while(num < processNum && processArray[num].arriveTime == cpuTime)
                firstQueue.offer(processArray[num++]);
            //打印上一秒各队列进程状态
            viewMenu(cpuTime);
            /*当前为队列1在运行进程*/
            if(!firstQueue.isEmpty()){
                if (secondQueue.peek() != null) secondQueue.peek().state = "等待";
                if (thirdQueue.peek() != null) thirdQueue.peek().state = "等待";
                //仍需时间-1
                firstQueue.peek().needTime -= 1;
                //CPU剩余时间片-1
                firstTimeSlice -= 1;
                //更新当前时间+1
                cpuTime++;
                //进程正在运行，状态：E.
                if(firstQueue.peek().needTime > 0){
                    firstQueue.peek().state = "运行";
                    //当前队列CPU时间片用完而进程仍未运行完时，进程出队，入次优先级队尾
                    if(firstTimeSlice == 0) {
                        firstQueue.peek().state = "等待";
                        secondQueue.offer(firstQueue.poll());
                        firstTimeSlice = firstCpu;
                    }

                }
                //进程运行完毕，状态：F，记录完成时刻并出队
                else if(firstQueue.peek().needTime == 0){
                    firstQueue.peek().state = "完成";
                    System.out.printf("【当前时刻】：%d,此进程"+firstQueue.peek().name+"运行结束：\n",cpuTime);
                    System.out.println("周转时间为：" + (cpuTime-firstQueue.peek().arriveTime));
                    Objects.requireNonNull(firstQueue.poll());
                    for (int i = 0; i < processArray.length; i++) {
                        if(processArray[i].name.equals(firstQueue.peek().name)){
                            processArray[i].turnRoundTime = cpuTime-firstQueue.peek().arriveTime;
                        }
                    }
                    firstTimeSlice = firstCpu;
                }
            }
            /*当前为队列2在运行进程*/
            else if(!secondQueue.isEmpty()){
                if (thirdQueue.peek() != null)
                    thirdQueue.peek().state = "等待";
                //仍需时间：-1
                secondQueue.peek().needTime -= 1;
                //CPU剩余时间片：-1
                secondTimeSlice -= 1;
                //更新当前时间：+1
                cpuTime++;

                //进程运行完毕，状态：F，记录完成时刻并出队
                if(secondQueue.peek().needTime == 0){
                    secondTimeSlice = secondCpu;
                    secondQueue.peek().state = "完成";
                    System.out.printf("【当前时刻】：%d,此进程"+secondQueue.peek().name+"运行结束：\n",cpuTime);
                    System.out.println("周转时间为：" + (cpuTime-secondQueue.peek().arriveTime));
                    for (int i = 0; i < processArray.length; i++) {
                        if(processArray[i].name.equals(secondQueue.peek().name)){
                            processArray[i].turnRoundTime = cpuTime-secondQueue.peek().arriveTime;
                        }
                    }
                    Objects.requireNonNull(secondQueue.poll());
                }
                //进程正在运行，状态：E.
                else if(secondQueue.peek().needTime > 0){
                    secondQueue.peek().state = "运行";
                    //当前队列CPU时间片用完而进程仍未运行完时，进程出队，入次优先级队尾
                    if(secondTimeSlice == 0) {
                        secondQueue.peek().state = "等待";
                        thirdQueue.offer(secondQueue.poll());
                        secondTimeSlice = secondCpu;
                    }
                }
            }
            /*当前为队列3在运行进程*/
            else if(!thirdQueue.isEmpty()){
                //仍需时间：-1
                thirdQueue.peek().needTime -= 1;
                //CPU剩余时间片：-1
                thirdTimeSlice -= 1;
                //更新当前时间：+1
                cpuTime++;

                //进程正在运行，状态：R.
                if(thirdQueue.peek().needTime > 0){
                    thirdQueue.peek().state = "运行";
                    //当前队列CPU时间片用完而进程仍未运行完时，进程出队，入次优先级队尾
                    if(thirdTimeSlice == 0) {
                        thirdQueue.peek().state = "等待";
                        thirdQueue.offer(thirdQueue.poll());
                        thirdTimeSlice = thirdCpu;
                    }
                }
                //进程运行完毕，状态：F，记录完成时刻并出队
                else{
                    firstTimeSlice = firstCpu;
                    thirdQueue.peek().state = "完成";
                    System.out.printf("【当前时刻】：%d,此进程"+thirdQueue.peek().name+"运行结束：\n",cpuTime);
                    System.out.println("周转时间为：" + (cpuTime-thirdQueue.peek().arriveTime));
                    for (int i = 0; i < processArray.length; i++) {
                        if(processArray[i].name.equals(thirdQueue.peek().name)){
                            processArray[i].turnRoundTime = cpuTime-thirdQueue.peek().arriveTime;
                        }
                    }
                    Objects.requireNonNull(thirdQueue.poll());
                }
            }
        }
    }

    /**
     * 输入
     */
    private static Process[] input(){
        System.out.println("-----------多级队列反馈调度模拟系统-------------\n");
        System.out.println("请依次输入三个队列的时间片长度：");
        firstTimeSlice = scanner.nextInt();
        secondTimeSlice = scanner.nextInt();
        thirdTimeSlice = scanner.nextInt();
        System.out.print("请输入进程数:" );
        processNum = scanner.nextInt();
        /*获取到进程数组*/
        processArray = new Process[processNum];
        System.out.println( "请依次输入进程号,进程到达时间,进程运行时间:" );
        for(int i = 0; i < processNum; i++ ) {
            processArray[i] = new Process();
            processArray[i].name = scanner.next();
            processArray[i].arriveTime = scanner.nextInt();
            processArray[i].serveTime = scanner.nextInt();
            processArray[i].needTime = processArray[i].serveTime;
            processArray[i].state = "等待";
        }
        //对进程按照compareTo()的要求按照到达时间排序
        Arrays.sort(processArray);
        return processArray;
    }
    /**
     * 输出每一轮运行结果
     */
    private static void viewMenu(int currentTime){
        System.out.printf("【当前时刻】：%d\n",currentTime);
        System.out.println("            到达时间 运行时间  剩余时间  状态");
        if(firstQueue.isEmpty()) System.out.println("队列一：空");
        else System.out.println("队列一：\n"+ firstQueue.toString()
                .replace("[", "").replace("]", "")
                .replace(", ", ""));
        if(secondQueue.isEmpty()) System.out.println("队列二：空");
        else System.out.println("队列二：\n"+ secondQueue.toString()
                .replace("[", "").replace("]", "")
                .replace(", ", ""));
        if(thirdQueue.isEmpty()) System.out.println("队列三：空");
        else System.out.println("队列三：\n"+ thirdQueue.toString()
                .replace("[", "").replace("]", "")
                .replace(", ", ""));
        System.out.println("=============================================");
    }

    /**
     * 输出最终结果
     */
    private static void show(){
        double averageT = 0;
        double averageWTAT = 0;
        System.out.println("================所有进程运行结束=====================");
        System.out.println("进程\t 周转时间\t 带权周转时间\t 到达时间\t");
        for (int i = 0; i < processArray.length; i ++) {
            int turnAroundTime = processArray[i].turnRoundTime;
            double weightTurnAroundTime = turnAroundTime*1.0/processArray[i].serveTime;
            System.out.printf(processArray[i].name+"\t   "+turnAroundTime+"\t   ");
            System.out.printf("%.1f\t\t\t", weightTurnAroundTime);
            System.out.printf("%2d\n", processArray[i].arriveTime);
            averageT += turnAroundTime;
            averageWTAT += weightTurnAroundTime;
        }
        averageT /= processArray.length;
        averageWTAT /= processArray.length;
        System.out.println("平均周转时间：" + averageT);
        System.out.printf("平均带权周转时间：%.2f\n", averageWTAT);
    }
    public static void main(String[] args) {
        input();
        running();
        show();
    }
}




