import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

class Population{
    ArrayList<Individual> wholePopulation = new ArrayList<>();
    ArrayList<Individual> parents = new ArrayList<>();
    ArrayList<Individual> children = new ArrayList<>();
}

class Individual{
    ArrayList<Integer> citiesOrder = new ArrayList<>();
    double fitness;
}

class Hw3{
    private int numberOfCities;
    private int populationSize = 4;
    private int generationSize = 5000;
    private int parentCount = 2;
    private Population population = new Population();
    private ArrayList<double[]> cities = new ArrayList<>();
    String[] cityNames = {
            "Aberystwyth", "Brighton", "Edinburgh", "Exeter", "Glasgow",
            "Inverness", "Liverpool", "London", "Newcastle", "Nottingham", "Oxford", "Stratford"
    };
    public void useManualPoints() {

        double[][] manualPoints = {
                {0.190032E-03, -0.285946E-03},
                {383.458, -0.608756E-03},
                {-27.0206, -282.758},
                {335.751, -269.577},
                {69.4331, -246.780},
                {168.521, 31.4012},
                {320.350, -160.900},
                {179.933, -318.031},
                {492.671, -131.563},
                {112.198, -110.561},
                {306.320, -108.090},
                {217.343, -447.089}
        };
        this.numberOfCities = manualPoints.length;
        for (int i = 0; i < manualPoints.length; i++) {
            double x = manualPoints[i][0];
            double y = manualPoints[i][1];
            cities.add(new double[]{x, y});
        }
    }

    public void getBestFitnessAndPath(){
        double bestFitness = Double.MAX_VALUE;
        ArrayList<Integer> path = new ArrayList<>();
        for (int i = 0; i < population.wholePopulation.size(); i++){
            if(population.wholePopulation.get(i).fitness < bestFitness){
                bestFitness = population.wholePopulation.get(i).fitness;
                path = population.wholePopulation.get(i).citiesOrder;
            }
        }

        System.out.println("The shortest path is:");
        System.out.println(bestFitness);
        for (int i = 0; i < path.size(); i++) {
            System.out.print(this.cityNames[path.get(i)]);
            if (i < path.size() - 1) {
                System.out.print(" -> ");
            }
        }

        System.out.println();
    }

    public void generatePoints(int numberOfCities){
        this.numberOfCities = numberOfCities;
        for(int i = 0; i < numberOfCities; i++){
            cities.add(generateCityLocation());
        }
    }

    private double[] generateCityLocation(){
        double[] cityLocation = new double[2];
        cityLocation[0] = pickRandom(-500,500);
        cityLocation[1] = pickRandom(-500,500);

        return cityLocation;
    }

    public void run(){
        int t = 0;
        initialize(population.wholePopulation);
        evaluate(population.wholePopulation);

        while (t<generationSize){
            population.parents = selectParents(population.wholePopulation);
            population.children = reproduction(population.parents);
            mutate(population.children);
            evaluate(population.children);
            buildNextGenerationFrom(population.children,population.wholePopulation);
            if(t ==0 || t==1 || t==5 || t==20){
                getBestFitnessAndPath();
            }
            t=t+1;
        }
    }

    private ArrayList<Individual> selectParents(ArrayList<Individual> wholePopulation) {
        ArrayList<Individual> parents = wholePopulation.stream()
                .sorted(Comparator.comparingDouble(o -> o.fitness))
                .limit(parentCount)
                .collect(Collectors.toCollection(ArrayList::new));
        return parents;
    }

    private void initialize(ArrayList<Individual> emptyPopulation){
        ArrayList<Integer> baseRoute = new ArrayList<>();
        for (int i = 0; i < numberOfCities; i++){
            baseRoute.add(i);
        }
        for (int i = 0; i < populationSize; i++){
            Collections.shuffle(baseRoute);
            Individual individual = new Individual();
            individual.citiesOrder = new ArrayList<>(baseRoute);
            emptyPopulation.add(individual);
        }
    }

    private void evaluate(ArrayList<Individual> population){
        for (int i = 0; i < population.size(); i++){
            population.get(i).fitness = calculateFitness(population.get(i));
        }
    }

    private double calculateFitness(Individual individual){
        double individualFitness = 0;
        for (int i = 0; i < numberOfCities-1; i++){
            double[] currentLocation = cities.get(individual.citiesOrder.get(i));
            double[] nextLocation = cities.get(individual.citiesOrder.get(i+1));
            individualFitness += getDistanceBetweenPoints(currentLocation, nextLocation);
        }
        return individualFitness;
    }
    public static double getDistanceBetweenPoints(double[] point1, double[] point2) {
        double x1 = point1[0];
        double y1 = point1[1];
        double x2 = point2[0];
        double y2 = point2[1];

        double deltaX = x2 - x1;
        double deltaY = y2 - y1;

        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    private boolean isNotTerminated(){

        return false;
    }

    private ArrayList<Individual>reproduction(ArrayList<Individual> parents){
        ArrayList<Individual> children = new ArrayList<>();
        Collections.shuffle(parents);
        for (int i = 0; i < parents.size()-1; i+=2){
            Individual[] currentChildren = crossover(parents.get(i),parents.get(i+1));
            children.add(currentChildren[0]);
            children.add(currentChildren[1]);
        }

        return children;
    }

    private Individual[] crossover(Individual parent1, Individual parent2){
        Individual child1 = new Individual();
        Individual child2 = new Individual();
        ArrayList<Integer> invalidIndexes1 = new ArrayList<>();
        ArrayList<Integer> invalidIndexes2 = new ArrayList<>();
        int crossOverPoint = Hw2.pickRandom(parent1.citiesOrder.size()/2,parent1.citiesOrder.size()-2);

        int switchSides = 1;
        if(pickRandom(0,1)>=.5){
            switchSides = -1;
        }

        for (int i = 0; i < parent1.citiesOrder.size(); i++) {
            Integer value1 = parent1.citiesOrder.get(i);
            Integer value2 = parent2.citiesOrder.get(i);
            if(pickRandom(0,1)<=0.5 && i*switchSides>crossOverPoint*switchSides){
                value1 = parent2.citiesOrder.get(i);
                value2 = parent1.citiesOrder.get(i);
            }
            if(child1.citiesOrder.contains(value1)){
                invalidIndexes1.add(i);
            }
            if(child2.citiesOrder.contains(value2)){
                invalidIndexes2.add(i);
            }
            child1.citiesOrder.add(value1);
            child2.citiesOrder.add(value2);
        }
        int numberOfMistakes = invalidIndexes1.size();
        for (int i = 0; i < numberOfMistakes; i++){
            int faultIndex1 = invalidIndexes1.get(i);
            int faultIndex2 = invalidIndexes2.get(i);
            int temp = child1.citiesOrder.get(faultIndex1);
            child1.citiesOrder.set(faultIndex1,child2.citiesOrder.get(faultIndex2));
            child2.citiesOrder.set(faultIndex2,temp);
        }

        return new Individual[]{child1, child2};
    }

    private void mutate(ArrayList<Individual> children){
        for (int i = 0; i < children.size(); i++){
            if(pickRandom(0,1)<= .6) {
                int mutationIndex1 = Hw2.pickRandom(0, (numberOfCities - 1) / 2);
                int mutationIndex2 = Hw2.pickRandom((numberOfCities - 1) / 2, numberOfCities - 1);
                int temp = children.get(i).citiesOrder.get(mutationIndex1);
                children.get(i).citiesOrder.set(mutationIndex1, children.get(i).citiesOrder.get(mutationIndex2));
                children.get(i).citiesOrder.set(mutationIndex2, temp);
            }
        }
    }

    private void buildNextGenerationFrom(ArrayList<Individual> children, ArrayList<Individual> wholePopulation){
        ArrayList<Individual> nextGeneration = wholePopulation.stream()
                .sorted(Comparator.comparingDouble(o -> o.fitness))
                .limit(wholePopulation.size()-children.size())
                .collect(Collectors.toCollection(ArrayList::new));
        for (int i = 0; i < children.size(); i++){
            nextGeneration.add(children.get(i));
        }

        this.population.wholePopulation = nextGeneration;
    }

    public static double pickRandom(double min, double max){
        return (Math.random() * (max - min) + min);
    }
}

public class TravellingSalesman {
    public static void main(String[] args) {
        Hw3 hw3 = new Hw3();
        //hw3.generatePoints(20);
        hw3.useManualPoints();
        long startTime = System.currentTimeMillis();
        hw3.run();
        long endTime = System.currentTimeMillis();
        double elapsedTimeSeconds = (endTime - startTime) / 1000.0;
        System.out.printf("Time taken: %.2f seconds\n", elapsedTimeSeconds);
        hw3.getBestFitnessAndPath();
    }
}
