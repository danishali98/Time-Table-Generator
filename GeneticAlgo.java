/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticalgo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Danish Ali
 */
public class GeneticAlgo {

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        int pop_size = 100;
        Population pop = new Population(pop_size);

        int i = 0;
        
        System.out.println("Creating initial population...");
        while (i < pop_size) {
            TimeTable obj = new TimeTable();
            obj.populate();
            pop.addChromosome(obj);
            i++;
        }

        System.out.println("Computing fitness...");
        pop.computeFitness();
        int fitness = pop.getMaxFitness();
        
        TimeTable best = null;

        System.out.println("Starting Genetic Algorithm...");
        i = 0;
        double rate=0.9;
        while (i<50) {
            System.out.println("Iteration: " + i);
            pop.crossOver();
            pop.performMutation();
            pop.computeFitness();
            fitness = pop.getMaxFitness();
            createNewGeneration(pop, rate);
            i++;
        }
//        System.out.println("Fitness after Genetic Algorithm: " + pop.fitness_pop[0]);
//        pop.printBestSolution();
        best=pop.population[0].clone();
        pop.localSearch(best,pop.fitness_pop[0]);
    }
    
    static void createNewGeneration(Population pop, double rate) throws IOException
    {
        for (int j = 0; j < pop.size - 1; j++) {
                for (int k = 0; k < pop.size - j - 1; k++) {
                    //System.out.println(pop.fitness_pop[k] + "   " + pop.fitness_pop[k+1]);
                    if (pop.fitness_pop[k] < pop.fitness_pop[k + 1]) {
                        TimeTable temp = pop.population[k];
                        int temp1 = pop.fitness_pop[k];
                        pop.population[k] = pop.population[k + 1];
                        pop.fitness_pop[k] = pop.fitness_pop[k + 1];
                        pop.population[k + 1] = temp;
                        pop.fitness_pop[k + 1] = temp1;
                    }
                }
            }
            int k=(int) (rate*pop.size);
            for(;k<pop.size;k++)
            {
                TimeTable temp=new TimeTable();
                temp.populate();
                pop.createGeneration(k, temp);
            }
    }
}
