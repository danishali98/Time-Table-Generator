/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticalgo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Danish Ali
 */
public class TimeTable {

    int rows = 223;
    int cols = 3169;
    char Reg[][];
    int days;
    int slots;
    ArrayList<Integer>[][] table;

    List<Integer> clashCourseId = new ArrayList<>();

    protected TimeTable clone() {
        TimeTable obj = null;
        try {
            obj = new TimeTable();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    obj.Reg[i][j] = this.Reg[i][j];
                }
            }
            for (int i = 0; i < days; i++) {
                for (int j = 0; j < slots; j++) {
                    int size = this.table[i][j].size();
                    obj.table[i][j] = new ArrayList<>();
                    for (int k = 0; k < size; k++) {
                        obj.table[i][j].add(this.table[i][j].get(k));
                    }
                }
            }
            for (int i = 0; i < clashCourseId.size(); i++) {
                obj.clashCourseId.add(this.clashCourseId.get(i));
            }
        } catch (IOException ex) {
            Logger.getLogger(TimeTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }

    public TimeTable() throws FileNotFoundException, IOException {

        File f = new File("general.txt");
        FileReader fr = new FileReader(f);

        days = fr.read() - 48;
        fr.read();              //skipping the space
        slots = fr.read() - 48;
        fr.close();

        this.Reg = new char[rows][cols];
        table = new ArrayList[days][slots];
    }

    void printTimeTable() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(Reg[i][j] + " ");
            }
            System.out.println();
        }
    }

    void printTable() {
        int list_index = 0;
        for (int i = 0; i < days; i++) {
            for (int j = 0; j < slots; j++) {
                int size = table[i][j].size();
                System.out.println("Slot No " + list_index + " :");
                for (int k = 0; k < size; k++) {
                    System.out.println(table[i][j].get(k));
                }
                System.out.println();
                list_index++;
            }
        }
    }

    void populate() throws FileNotFoundException, IOException {
        File f = new File("registration.txt");
        FileReader fr = new FileReader(f);

        int bit, i = 0, j = 0, k = 0;
        while ((bit = fr.read()) != -1) {
            //disregrading the tab in data file
            if ((char) bit == '0' || (char) bit == '1') {
                Reg[i][j] = (char) bit;
                if ((char) bit == '1') {
                    k++;
                }
                j++;
                if (j == cols) {
                    j = 0;
                    //System.out.println(k);
                    k = 0;
                    i++;
                }
            }
        }
        //get Subjects for each day
        int[] div = getSubjects();
        //maintaining count for Subjects as days progress
        updateDiv(div);

        int day = 0;
        i = 0;
        int rowT = 0;
        int colT = 0;

        //Initialize List at each index
        for (i = 0; i < days; i++) {
            for (j = 0; j < slots; j++) {
                table[i][j] = new ArrayList<>();
            }
        }

        for (i = 0; i < rows; i++) {
            //placing Subjects randomly
            Random rand = new Random();
            colT = rand.nextInt(slots);
            table[rowT][colT].add(i);

            if (i == div[day]) {
                day++;
                rowT++;
            }
        }
    }

    int getStudentIdInSubjects(int courseId, List<Integer> list, int[] examCount) {
        //List<Integer> list = new ArrayList<>();
        int multiple_Count = 0;
        int i = courseId;
        int check1 = 0;
        boolean check = false;
        boolean flag = false;
        for (int j = 0; j < cols; j++) {
            if (Reg[i][j] == '1') {
                examCount[j] += 1;          //Increment exam count of student
                //Storing id of courses which have clashes.
                if (examCount[j] > 1 && check == false) {
                    for (int k = 0; k < clashCourseId.size(); k++) {
                        if (clashCourseId.get(k) == i) {
                            check1 = 1;
                        }
                    }
                    if (check1 == 0) {
                        this.clashCourseId.add(i);
                        check = true;
                    }
                }
                for (int k = 0; k < list.size(); k++) {
                    if (list.get(k) == j) {             //if courses exists already in the list
                        //temp.clashCourseId.add(j);
                        flag = true;
                        multiple_Count -= 20;
                    }
                }
                if (flag == false) {                    //if course does not already exist
                    //temp.clashCourseId.add(i);
                    multiple_Count += 10;
                    list.add(j);
                }
            }
            flag = false;
        }
        return multiple_Count;
    }

    void getStudentsInSubjects(int[] studentCountInSubjects) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (Reg[i][j] == '1') {
                    studentCountInSubjects[i]++;
                }
            }
        }
    }

    void updateDiv(int[] div) {
        div[1] = div[0] + div[1];
        div[2] = div[1] + div[2];
    }

    int[] getSubjects() {
        int count = rows / 3;
        if (rows % 3 == 0) {
            int[] arr = {count, count, count};
            return arr;
        } else {
            int[] arr = {count, count, count + 1};
            return arr;
        }
    }

    int computeFitness() throws IOException {

        int timetable_fitness = 0;
        int totalStudents = 0;
        int subjectCount = rows;
        int days = this.days;
        int slots = this.slots;
        int studentCount = cols;
        int[] studentCountInSubjects = new int[subjectCount];

        //store count of exams for every student per day
        int[] examCountPerDay = new int[studentCount];

        for (int i = 0; i < studentCount; i++) {
            examCountPerDay[i] = 0;
        }

        getStudentsInSubjects(studentCountInSubjects);

        int[] subjectCheck = new int[subjectCount];
        for (int z = 0; z < subjectCount; z++) {
            subjectCheck[z] = 0;
        }

        List<Integer> rooms = getRoomCapacity();
        int totalRoomCap = 0;
        for (int z = 0; z < rooms.size(); z++) {
            totalRoomCap = totalRoomCap + rooms.get(z);
        }

        List<Integer> studentList = new ArrayList<>();
        for (int j = 0; j < days; j++) {
            for (int k = 0; k < slots; k++) {
                //System.out.println(i + " " + j + " " +k);
                int size = this.table[j][k].size();

                for (int l = 0; l < size; l++) {
                    int courseId = this.table[j][k].get(l);
                    subjectCheck[courseId] = 1;
                    totalStudents += studentCountInSubjects[courseId];

                    //checking if a student has multiple courses in one slot
                    timetable_fitness += this.getStudentIdInSubjects(courseId, studentList, examCountPerDay);
                }

                studentList.clear();        //Clearing list after each slot

                //Checking room capacity and total students for each slot.
                if (totalStudents > totalRoomCap) {
                    timetable_fitness -= 70;
                } else {
                    timetable_fitness += 50;
                }
            }
            //Checking multiple exam count
            for (int z = 0; z < studentCount; z++) {
                if (examCountPerDay[z] > 3) {
                    timetable_fitness -= 50;
                }
                examCountPerDay[z] = 0;
            }
        }
        //Checking if all subjects are scheduled in the time table
        for (int z = 0; z < subjectCount; z++) {
            if (subjectCheck[z] == 0) {
                timetable_fitness -= 15;
            } else {
                timetable_fitness += 5;
            }
        }
        //Initializing subject count to zero.
        for (int z = 0; z < subjectCount; z++) {
            subjectCheck[z] = 0;
        }
        return timetable_fitness;
    }

    List<Integer> getRoomCapacity() throws FileNotFoundException, IOException {
        List<Integer> roomCap = new ArrayList<>();

        File file = new File("capacity.txt");
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        int temp = 0;
        while ((line = br.readLine()) != null) {
            temp = Integer.parseInt(line);
            roomCap.add(temp);
        }
        fr.close();
        return roomCap;
    }
}
