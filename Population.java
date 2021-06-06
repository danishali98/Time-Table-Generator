/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticalgo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Danish Ali
 */
public class Population {

    int index;
    int size;
    int fitness;
    TimeTable[] population;
    int[] fitness_pop;

    public Population(int _size) {
        size = _size;
        population = new TimeTable[size];
        fitness_pop = new int[size];
        index = 0;
    }

    void addChromosome(TimeTable obj) {
        population[index] = obj;
        index++;
    }

    void printBestSolution() {
        int j = 0;
//        System.out.println("Fitness in Population class: " + fitness_pop[j]);
        System.out.println("\nBest fit solution:\n");
        population[j].printTable();
    }

    int getMaxFitness() {
        int max_fitness = 0;
        for (int i = 0; i < size; i++) {
            if (fitness_pop[i] > max_fitness) {
                max_fitness = fitness_pop[i];
            }
        }
        return max_fitness;
    }

    void crossOver() {

        int loop = 0;
        TimeTable first = null;
        TimeTable second = null;
        while (loop < size / 2) {

            Random rand = new Random();

            int firstIndex = rand.nextInt(size);
            int secIndex = rand.nextInt(size);

            if (firstIndex != secIndex) {

                first = population[firstIndex];
                second = population[secIndex];

                int[] indices = getIndex();

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 7; j++) {
                        if (i < indices[0] && j < indices[1]) {
                            second.table[i][j] = first.table[i][j];
                        } else {
                            first.table[i][j] = second.table[i][j];
                        }
                    }
                }
                loop++;
            } else {
                firstIndex = rand.nextInt(3);
            }
        }
    }

    void sortedPop() {
        for (int i = 0; i < size; i++) {
            System.out.println("Fitness " + i + " : " + fitness_pop[i]);
        }
    }

    void createGeneration(int index, TimeTable obj) {
        population[index] = obj;
    }

    void computeFitness() throws IOException {
        for (int i = 0; i < size; i++) {
            fitness_pop[i] = population[i].computeFitness();
        }
    }

    void performMutation() {
        Random rand = new Random();

        double temp = rand.nextDouble();

        if (temp < 0.01) {
            int index = rand.nextInt(size);

            int[] indices = getIndex();
            int i = indices[0], j = indices[1];
            indices = getIndex();
            int l = indices[0], m = indices[1];

            int size = population[index].table[i][j].size();

            int firstIndex = rand.nextInt(size);
            int secIndex = rand.nextInt(size);

            Collections.swap(population[index].table[i][j], firstIndex, secIndex);

            List<Integer> temp1 = new ArrayList<>();
            List<Integer> temp2 = new ArrayList<>();

            int size1 = population[index].table[i][j].size();
            for (int z = 0; z < size1; z++) {
                temp1.add(population[index].table[i][j].get(z));
            }
            int size2 = population[index].table[l][m].size();
            for (int z = 0; z < size2; z++) {
                temp2.add(population[index].table[l][m].get(z));
            }

            population[index].table[i][j].removeAll(population[index].table[i][j]);
            population[index].table[l][m].removeAll(population[index].table[l][m]);

            for (int z = 0; z < temp2.size(); z++) {
                population[index].table[i][j].add(temp2.get(z));
            }
            for (int z = 0; z < temp1.size(); z++) {
                population[index].table[l][m].add(temp1.get(z));
            }
        }
    }

    int[] getIndex() {
        Random rand = new Random();
        int[] indices = new int[3];
        indices[0] = rand.nextInt(3);
        indices[1] = rand.nextInt(7);
        return indices;
    }

    void localSearch(TimeTable best, int best_fitness) throws IOException {

        System.out.println("Running Local Search...");
        int total_clashes = best.clashCourseId.size();
        TimeTable temp1 = best.clone();
        TimeTable temp = null;

        for (int i = 0; i < total_clashes; i++) {
            int r = 0, c = 0;

            temp = temp1.clone();
            //Taking up courses causing clash iteratively
            int c_id = temp.clashCourseId.get(i);

            Boolean flag = true;
            int rem_indices = ((best.days * best.slots) - 1);
            //Removing the specific course from list
            for (int j = 0; j < best.days && flag == true; j++) {
                for (int k = 0; k < best.slots && flag == true; k++) {
                    int list_size = temp.table[j][k].size();

                    for (int l = 0; l < list_size && flag == true; l++) {
                        if (temp.table[j][k].get(l) == c_id) {
                            r = j;
                            c = k;
                            temp.table[j][k].remove(l);
                            flag = false;
                        }
                    }
                }
            }

            //Making copies of updated TimeTable
            List<TimeTable> copies = new ArrayList<>();
            for (int j = 0; j < rem_indices; j++) {
                copies.add(temp.clone());
            }

            //Placing the course at different indices in each TimeTable
            int l = 0;
            for (int j = 0; j < best.days; j++) {
                for (int k = 0; k < best.slots; k++) {
                    if (j == r && k == c) {
                    } else {
                        copies.get(l).table[j][k].add(c_id);
                        l++;
                    }
                }
            }

            int[] fitness_copies = new int[rem_indices];
            for (int j = 0; j < rem_indices; j++) {
                fitness_copies[j] = copies.get(j).computeFitness();
            }

            //If a better fit is found then it is updated
            Boolean update = false;
            for (int j = 0; j < rem_indices; j++) {
                if (fitness_copies[j] > best_fitness) {
                    temp1 = null;
                    temp1 = temp.clone();
                    best_fitness = fitness_copies[j];
                    temp = null;
                    temp = copies.get(j).clone();
                    update = true;
                }
            }
            //Otherwise the previous TimeTable is restored
            if (update == false) {
                temp = null;
                temp = temp1.clone();
            }
        }
//        System.out.println("Fitness after Local Search: " + best_fitness);
        System.out.println("Final Schedule:\n");
        temp.printTable();
    }
}
