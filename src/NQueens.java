import java.util.ArrayList;
import java.util.stream.Stream;

class Hw2{
    private static final double k = 50;
    private int[] queens;
    private int N;
    private int[] queensPerRow;
    private int[] queensPerD1;
    private int[] queensPerD2;

    public int[] solve(int N){
        this.N = N;
        queens = init(N);
        long iter = 0;
        while(iter++ <= k*N){
            int col = getColWithQueenWithMaxConf();
            if(rowConflicts(col,queens[col])==3){
                return queens;
            }
            updateConflicts(col,queens[col],-1);
            int row = getRowWithMinConflict(col);
            updateConflicts(col,row,1);
            queens[col] = row;
        }
        solve(N);
        return null;
    }

    private int[] init(int N){
        int[] queens = new int[N];
        queensPerRow = new int[N];
        queensPerD1 = new int[2*N-1];
        queensPerD2 = new int[2*N-1];

        for (int i = 0; i < N; i++){
            queens[i] = pickRandom(0,N-1);
            updateConflicts(i, queens[i],1);
        }
        return queens;
    }

    private void updateConflicts(int col, int row, int val){
        queensPerRow[row] += val;
        queensPerD1[col + row] += val;
        queensPerD2[col + (N - row - 1)] += val;
    }

    private int getRowWithMinConflict(int col) {
        int minConflicts = N;
        ArrayList<Integer> minConflictRows = new ArrayList<Integer>();
        for (int row = 0; row < N; row++){
            int conflicts = rowConflicts(col, row);
            if(conflicts==0){
                return row;
            }
            if (conflicts<minConflicts){
                minConflictRows = new ArrayList<>();
                minConflictRows.add(row);
                minConflicts=conflicts;
            }else if(conflicts == minConflicts){
                minConflictRows.add(row);
            }
        }
        return minConflictRows.get(pickRandom(0,minConflictRows.size()-1));
    }

    private int rowConflicts(int col, int row){
        return queensPerRow[row] + queensPerD1[col+row] + queensPerD2[col+(N-row-1)];
    }

    private int getColWithQueenWithMaxConf() {
        int conflicts = 0;
        int maxConflicts = 0;
        ArrayList<Integer> maxConflictCols = new ArrayList<>();

        for (int col = 0; col < N; col++){
            int row = queens[col];
            conflicts = rowConflicts(col,row);
            if(conflicts > maxConflicts){
                maxConflictCols = new ArrayList<>();
                maxConflictCols.add(col);
                maxConflicts = conflicts;
            }else if(conflicts == maxConflicts){
                maxConflictCols.add(col);
            }
        }
        return maxConflictCols.get(pickRandom(0, maxConflictCols.size()-1));
    }

    public static int pickRandom(int min, int max){
        return (int) (Math.random() * (max - min + 1) + min);
    }

}

public class NQueens {
    public static boolean hasConflict(int[] queens, int col1, int col2) {
        int row1 = queens[col1];
        int row2 = queens[col2];

        return row1 == row2 || Math.abs(row1 - row2) == Math.abs(col1 - col2);
    }

    public static boolean hasConflicts(int[] queens) {
        int N = queens.length;
        for(int col = 0; col < N-1; col++){
            for(int col2=col+1;col2<N;col2++){
                if(hasConflict(queens,col,col2)){
                    System.out.println("Conflict in " + col + " " + col2);
                    return true;
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {
        Hw2 hw2 = new Hw2();
        int N = 10000;
        long startTime = System.currentTimeMillis();
        int[] queens = hw2.solve(N);
        //queens = new int[]{1, 4, 2, 0, 3};
        long endTime = System.currentTimeMillis();
        double elapsedTimeSeconds = (endTime - startTime) / 1000.0;
        System.out.printf("Time taken: %.2f seconds\n", elapsedTimeSeconds);
        if(queens == null){
            System.out.println("No solution");
        }else{
            if (hasConflicts(queens)) {
                System.out.println("Response has conflicts");
            }else{
                System.out.println("No conflicts");
            }
            for(int row = 0; row < N; row++){
                for (int col = 0; col < N; col++){
                    if(queens[col]==row){
                        System.out.print("*");
                    }else{
                        System.out.print("_");
                    }
                }
                System.out.print('\n');
            }

        }

    }
}
