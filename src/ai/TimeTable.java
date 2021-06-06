/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Danish Ali
 */
public class TimeTable {
    char Reg[][] = new char[223][6338];

    int rows=223;
    int cols=6338;

    ArrayList[][] table = new ArrayList[3][7];

    void populate() throws FileNotFoundException, IOException{
        File f = new File("registration.txt");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        int c, k = 0, j = 0;
        while ((c = fr.read()) != -1) {
            if ((char)c == '0' || (char)c == '1') {
                Reg[k][j] = (char) c;
                j++;
                if(j>=6338){
                    j=0;
                    k++;
                }
            }
        }

        int []Div=getSubjects();

        Div[1]=Div[0]+Div[1];
        Div[2]=Div[1]+Div[2];

     //   System.out.print(Div[0]+" "+Div[1]+" "+Div[2]);

        int day=0;
        int i=0;

        int rowT=0;
        int colT=0;

        for(i=0;i<3;i++){
            for(j=0;j<7;j++){
                table[i][j] =new ArrayList();
            }
        }

        for(i=0;i<223;i++)
        {
           // System.out.println(rowT);
            Random rand=new Random();
            colT=rand.nextInt(7);
            
            table[rowT][colT].add(i);
            //colT++;

//            if(colT%7 == 0)
//            {
//              colT=0;
//            }

            if (i == Div[day]) {
                day++;
                rowT++;
            }
        }

        int index=0;

        for(i=0;i<3;i++)
        {
            for(j=0;j<7;j++)
            {
                int s=table[i][j].size();

                System.out.println("List No"+index);

                for(k=0;k<s;k++)
                {
                    System.out.println(table[i][j].get(k));
                }
                System.out.println(" ");
                index++;

            }
        }





    }

    int[] getSubjects(){
        //int arr[];
        int count=rows/3;
        if(rows%3==0)
        {
            int []arr={count, count, count};
            return arr;
        }
        else
        {
            int []arr={count, count, count+1};
            return arr;
        }
    }

    void printTimeTable(){
        for(int i=0;i<223;i++) { //Reg.length
            for (int m = 0; m < 6338; m++) { //Reg[i].length
                System.out.print(Reg[i][m] + " ");
            }
            System.out.print("\n");
        }
    }
}
