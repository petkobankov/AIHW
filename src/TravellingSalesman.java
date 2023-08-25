import java.util.ArrayList;
import java.util.Collections;

class Population{
    ArrayList<Individual> startingPopulation;
    ArrayList<Individual> parents;
    ArrayList<Individual> children;
}

class Individual{
    ArrayList<Integer> citiesOrder;
    double fitness;
}

class Hw3{
    private int numberOfCities;
    private int populationSize = 150;
    private int parentsSize = 16;
    private ArrayList<Population> populations = new ArrayList<Population>();
    private ArrayList<double[]> cities = new ArrayList<>();

    public void generatePoints(int numberOfCities){
        this.numberOfCities = numberOfCities;
        for(int i = 0; i < numberOfCities; i++){
            cities.add(generateCityLocation());
        }
    }

    private double[] generateCityLocation(){
        double[] cityLocation = new double[2];
        cityLocation[0] = pickRandom(-500,500);
        cityLocation[0] = pickRandom(-500,500);

        return cityLocation;
    }

    public void run(){
        int t = 0;
        populations.add(new Population());
        initialize(populations.get(t).startingPopulation);
        evaluate(populations.get(t).startingPopulation);
        while (isNotTerminated()){
            populations.get(t).parents = selectParents(populations.get(t).startingPopulation);
            populations.get(t).children = reproduction(populations.get(t).parents);
            mutate(populations.get(t).children);
            evaluate(populations.get(t).children);
            populations.add(new Population());
            populations.get(t+1).startingPopulation=buildNextGenerationFrom(populations.get(t).children,populations.get(t).startingPopulation);
            t=t+1;
        }
    }

    private ArrayList<Individual> selectParents(ArrayList<Individual> startingPopulation) {
        return null;
    }

    public void solve(){
        int t = 0;

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
        for (int i = 0; i < populationSize; i++){
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

    private double getDistanceBetweenPoints(double[] x, double[] y){
        return Math.sqrt((y[1] - y[0]) * (y[1] - y[0]) + (x[1] - x[0]) * (x[1] - x[0]));
    }

    private boolean isNotTerminated(){

        return false;
    }

    private ArrayList<Individual>reproduction(ArrayList<Individual> parents){

        return null;
    }

    private void mutate(ArrayList<Individual> population){

    }

    private ArrayList<Individual> buildNextGenerationFrom(ArrayList<Individual> children, ArrayList<Individual> nextPopulation){

        return null;
    }

    public static double pickRandom(int min, int max){
        return (Math.random() * (max - min + 1) + min);
    }
}

public class TravellingSalesman {
    public static void main(String[] args) {
        Hw3 hw3 = new Hw3();

    }
}
