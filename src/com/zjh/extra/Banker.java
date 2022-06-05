package com.zjh.extra;

import java.util.Scanner;

/**
 * @author 张俊鸿
 * @description: 银行家算法
 * @since 2022-06-05 1:46
 */
public class Banker {
    public Scanner scanner = new Scanner(System.in);
    public static String[] resource=new String[10]; //资源的名称
    public static int[][] max= new int[10][10];  //最大需求矩阵
    public static int[][] allocation=new int[10][10];  //已分配矩阵
    public static int[][] need=new int[10][10]; //需求矩阵
    public static int[] available=new int[10]; //现有资源矩阵
    public static int resource_num;   //资源数
    public static int process_num;    //进程数
    public static int request[]=new int[10];  //请求向量
    public static int work[]=new int[10];   //工作向量
    public static boolean finish[]=new boolean[10]; //标记向量
    public static int security[]=new int[10];  //存放安全序列

    /**
     * 初始化资源
     */
    public void init(){
        String name;
        int number;
        System.out.print("系统可用资源种类为:");
        resource_num = scanner.nextInt();
        for (int i = 0; i < resource_num; i++) {
            System.out.println("资源"+i+"名称为:");
            name = scanner.next();
            resource[i] = name;
            System.out.println("资源"+name+"初始化个数为:");
            number = scanner.nextInt();
            available[i] = number;
        }
        System.out.print("进程数为:");
        process_num = scanner.nextInt();
        System.out.println("请输入Max矩阵(空格分开资源 换行分开进程):");
        boolean flag;
        do {
            flag=false;
            for(int i = 0; i < process_num; i++)
            {
                for(int j = 0; j < resource_num; j++)
                {
                    max[i][j]=scanner.nextInt();
                    if(max[i][j]>available[j])
                    {
                        flag=true;
                        break;
                    }
                }
            }
            if(flag)
            {
                System.out.println("资源最大需求量大于系统中资源最大量，请重新输入!");
            }
        }while(flag);
        System.out.println("请输入Allocation矩阵(空格分开资源 换行分开进程):");
        int[] temp = new int[resource_num];
        do {
            flag=false;
            for(int i = 0; i < process_num; i++)
            {
                for(int j = 0; j < resource_num ; j++)
                {
                    allocation[i][j] = scanner.nextInt();
                    if(allocation[i][j] > max[i][j])
                    {
                        flag = true;
                        break;
                    }
                    need[i][j] = max[i][j] - allocation[i][j];
                    temp[j] += allocation[i][j];//统计已经分配给进程的资源数目
                }
            }
            if(flag)
            {
                System.out.println("分配的资源大于最大量，请重新输入!");
            }
        }while(flag);

        //初始化Available矩阵
        for (int i = 0; i < resource_num; i++) {
            available[i] = available[i] - temp[i];
        }
    }

    /**
     * 按照输入进行分配
     *
     * @param process 过程
     */
    public void allocate(int process){
        for (int j = 0; j < resource_num; j++) {
            available[j] = available[j] - request[j];
            allocation[process][j] = allocation[process][j] + request[j];
            need[process][j] = need[process][j] - request[j];
        }
    }

    /**
     * 分配不安全 进行回滚
     *
     * @param process 过程
     */
    public void rollback(int process){
        for (int j = 0; j < resource_num; j++) {
            available[j] = available[j] + request[j];
            allocation[process][j] = allocation[process][j] - request[j];
            need[process][j] = need[process][j] + request[j];
        }
    }

    /**
     * 安全检查
     *
     * @return boolean
     */
    public boolean safeCheck(){
        //初始化work和finish
        for (int i = 0; i < resource_num; i++) {
            work[i] = available[i];
        }
        for (int i = 0; i < process_num; i++) {
            finish[i] = false;
        }
        //从头开始遍历分配
        int apply,k=0;
        int p = 0;
        while (p < process_num){
            for(int i = 0; i < process_num; i++)
            {
                apply=0;
                for(int j = 0; j < resource_num ; j++)
                {
                    if(finish[i] == false && need[i][j] <= work[j])
                    {
                        apply++;
                        if(apply == resource_num)
                        {
                            for(int m = 0; m < resource_num; m++)
                            {
                                work[m]=work[m] + allocation[i][m];//更改当前可分配资源
                            }
                            finish[i]=true;
                            security[k++]=i;
                            p++;
//                            i=-1; //保证每次查询均从第一个进程开始
                        }
                    }
                }
            }
        }

        for(int i=0;i<process_num;i++)
        {
            if(finish[i]==false)
            {
                System.out.println("系统不安全！");
                return false;
            }
        }
        System.out.println("系统安全！");
        System.out.println("存在一个安全序列：");
        for(int i=0;i<process_num;i++)
        {
            System.out.print("P"+security[i]);
            if(i<process_num-1)
            {
                System.out.print("->");
            }
        }
        return true;
    }

    /**
     * 显示
     */
    public void show(){
        int i,j;
        System.out.println("*************************************************************");
        System.out.println("系统目前可用的资源[Available]:");
        for(i = 0; i < resource_num; i++)
        {
            System.out.print(resource[i]+"  ");
        }
        System.out.println();
        for(j=0;j<resource_num;j++)
        {
            System.out.print(available[j]+"  ");
        }
        System.out.println();
        System.out.println("系统当前的资源分配情况如下：");
        System.out.println("         Max   	    Allocation      Need");
        System.out.print("进程名  ");
        //输出与进程名同行的资源名，Max、Allocation、Need下分别对应
        for(j=0;j<3;j++)
        {
            for(i=0;i<resource_num;i++)
            {
                System.out.print(resource[i]+"  ");
            }
            System.out.print("  ");
        }
        System.out.println();
        //输出每个进程的Max、Allocation、Need
        for(i=0;i<process_num;i++)
        {
            System.out.print("P"+i+"    ");
            for(j=0;j<resource_num;j++)
            {
                System.out.print(max[i][j]+"  ");
            }
            System.out.print("     ");
            for(j=0;j<resource_num;j++)
            {
                System.out.print(allocation[i][j]+"  ");
            }
            System.out.print("     ");
            for(j=0;j<resource_num;j++)
            {
                System.out.print(need[i][j]+"  ");
            }
            System.out.println();
        }
    }

    /**
     * 银行家算法
     */
    public void bank(){
        boolean flag=true;
        int process,j;
        System.out.println("请输入请求分配资源的进程号(0~"+(process_num-1)+"):");
        process=scanner.nextInt();
        System.out.println("请输入进程"+process+"要申请的资源个数:");
        for(j = 0; j < resource_num; j++)
        {
            System.out.print("资源"+resource[j]+":");
            request[j] = scanner.nextInt();
        }
        //判断当前分配是否安全
        for(j = 0; j < resource_num; j++)
        {
            if(request[j] > need[process][j])
            {
                System.out.print("进程"+process+"申请的资源大于系统现在可利用的资源");
                System.out.println("分配不合理，不予分配！");
                flag=false;
                break;
            }
            else
            {
                if(request[j]>available[j])
                {
                    System.out.print("进程"+process+"申请的资源大于系统现在可利用的资源");
                    System.out.println();
                    System.out.println("系统尚无足够资源，不予分配!");
                    flag=false;
                    break;
                }
            }
        }
        //当前分配安全，尝试寻找安全序列
        if(flag)
        {
            allocate(process);
            show();
            if(safeCheck()!=true)
            {
                rollback(process);
                show();
            }
        }
    }


    public static void main(String[] args) {
        Banker banker = new Banker();
        Scanner scanner2 = new Scanner(System.in);
        System.out.println("--------------银行家算法--------------------");
        banker.init();
        banker.show();
        while(true)//循环进行分配
        {
            banker.bank();
            System.out.println();
            System.out.println("是否再请求分配(y/n)");
            String choice = scanner2.next();
            if(choice.equals("n"))
                break;
        }
    }

}
