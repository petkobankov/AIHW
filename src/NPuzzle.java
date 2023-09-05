import java.util.*;

class Node{
    int[] matrix;
    int manhattanDistance;
    int zeroX;
    int zeroY;
    int distance = 0;
    StringBuilder pathToTheNode = new StringBuilder("");

    public PriorityQueue<Node> successors() {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                int f1 = node1.manhattanDistance;
                int f2 = node2.manhattanDistance;
                return Integer.compare(f1, f2);
            }
        });

        for (int i = 0; i < 4; i++){
            int newZeroX = zeroX + Hw1.directions[i][0];
            int newZeroY = zeroY + Hw1.directions[i][1];
            if (Hw1.isPosValid(newZeroX, newZeroY)) {
                Node successor = new Node();
                successor.matrix = new int[Hw1.MATRIX_DIMENSION*Hw1.MATRIX_DIMENSION];
                System.arraycopy(matrix, 0, successor.matrix, 0, Hw1.MATRIX_DIMENSION*Hw1.MATRIX_DIMENSION);
                successor.matrix[Hw1.twoDtoOneD(zeroX,zeroY)] = successor.matrix[Hw1.twoDtoOneD(newZeroX, newZeroY)];
                successor.matrix[Hw1.twoDtoOneD(newZeroX, newZeroY)] = 0;
                int manhattan1 = successor.getManhattanDistanceCell(zeroX,zeroY);
                int manhattan2 = successor.getManhattanDistanceCell(newZeroX,newZeroY);
                int otherManhattan1 = getManhattanDistanceCell(zeroX,zeroY);
                int otherManhattan2 = getManhattanDistanceCell(newZeroX,newZeroY);
                successor.manhattanDistance = manhattanDistance - otherManhattan1 - otherManhattan2 + manhattan1 + manhattan2;

                successor.zeroX = newZeroX;
                successor.zeroY = newZeroY;
                successor.pathToTheNode = new StringBuilder(pathToTheNode);
                if (successor.pathToTheNode.length() > 0) {
                    successor.pathToTheNode.append(", ");
                }
                String moveDirection = Hw1.getMoveDirection(Hw1.directions[i]);
                successor.pathToTheNode.append(moveDirection);
                successor.distance = distance+1;

                priorityQueue.offer(successor);
            }
        }
        return priorityQueue;
    }
    public int getManhattanDistance() {
        int sum = 0;
        for (int i = 0; i < Hw1.MATRIX_DIMENSION; i++) {
            for (int j = 0; j < Hw1.MATRIX_DIMENSION; j++) {
                sum += getManhattanDistanceCell(i,j);
            }
        }
        return sum;
    }

    private int getManhattanDistanceCell(int row, int column) {
        int value = matrix[Hw1.twoDtoOneD(row,column)];
        return Math.abs(row - Hw1.goalMatrix[value][0]) + Math.abs(column - Hw1.goalMatrix[value][1]);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(matrix);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Node other = (Node) obj;
        return Arrays.equals(this.matrix, other.matrix);
    }
}

class Hw1 {
    static final int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private int N;
    public static int[][] goalMatrix;
    public static int MATRIX_DIMENSION;
    private static final int FOUND = -2;
    private int[] board;
    private Set<Node> visitedNodes = new HashSet<>();
    public static int zeroXgoal;
    public static int zeroYgoal;
    HashSet<String> uniqueStates;
    Node lastNode;
    Node startingNode = new Node();


    public void getInput() {
        Scanner sc = new Scanner(System.in);
        N = sc.nextInt() + 1;
        MATRIX_DIMENSION = (int) Math.sqrt(N);
        int zeroPosition = sc.nextInt();
        zeroXgoal = oneDtoTwoD(N-1)[0];
        zeroYgoal = oneDtoTwoD(N-1)[1];
        if (zeroPosition != -1) {
            zeroXgoal = oneDtoTwoD(zeroPosition)[0];
            zeroYgoal = oneDtoTwoD(zeroPosition)[1];
        }
        startingNode.matrix = new int[Hw1.MATRIX_DIMENSION*Hw1.MATRIX_DIMENSION];
        board = new int[N];
        for (int i = 0; i < N; i++) {
            int currentPuzzleNumber = sc.nextInt();
            if(currentPuzzleNumber==0){
                startingNode.zeroX = oneDtoTwoD(i)[0];
                startingNode.zeroY = oneDtoTwoD(i)[1];
            }
            startingNode.matrix[i] = currentPuzzleNumber;
            board[i] = currentPuzzleNumber;
        }
        setUpGoalMatrix();
        startingNode.manhattanDistance = startingNode.getManhattanDistance();
    }

    private void setUpGoalMatrix() {
        goalMatrix = new int[Hw1.MATRIX_DIMENSION * Hw1.MATRIX_DIMENSION][2];

        for (int i = 0; i < N; i++) {
            int value = i;
            int row = value / Hw1.MATRIX_DIMENSION;
            int column = value % Hw1.MATRIX_DIMENSION;
            if(row>Hw1.zeroXgoal || (row==Hw1.zeroXgoal && column > Hw1.zeroYgoal)){
                value++;
            }
            int targetRow = (value - 1) / Hw1.MATRIX_DIMENSION;
            int targetCol = (value - 1) % Hw1.MATRIX_DIMENSION;
            if(i == 0){
                targetRow = Hw1.zeroXgoal;
                targetCol = Hw1.zeroYgoal;
            }
            goalMatrix[i] = new int[2];
            goalMatrix[i][0] = targetRow;
            goalMatrix[i][1] = targetCol;
        }
    }

    public static int[] oneDtoTwoD(int position) {
        int[] twoDPosition = new int[2];
        twoDPosition[1] = position % MATRIX_DIMENSION; //column
        twoDPosition[0] = position / MATRIX_DIMENSION; //row
        return twoDPosition;
    }

    public static int twoDtoOneD(int row, int column) {
        return row * MATRIX_DIMENSION + column;
    }


    public Node ida_star() {
        int bound = startingNode.manhattanDistance;
        visitedNodes.add(startingNode);
        while (true) {
            int t = search(startingNode, 0, bound);
            if (t == FOUND) {
                return lastNode;
            }
            if (t == Integer.MAX_VALUE) {
                return null;
            }
            bound = t;
        }
    }

    private int search(Node currentNode, int g, int bound) {
        int f = g + currentNode.manhattanDistance;
        if (f > bound) {
            return f;
        }
        if (currentNode.manhattanDistance==0) {
            lastNode=currentNode;
            return FOUND;
        }
        int min = Integer.MAX_VALUE;
        PriorityQueue<Node> successors = currentNode.successors();
        int i = 0;
        for (Node successor : successors) {
            i++;
            if(!visitedNodes.contains(successor)){
                visitedNodes.add(successor);
                int t = search(successor, g + 1, bound);
                if ( t == FOUND){
                    return FOUND;
                }
                if (t < min){
                    min = t;
                }
                visitedNodes.remove(successor);
            }
        }
        return min;
    }

    public static boolean isPosValid(int row, int col) {
        return row >= 0 && row < MATRIX_DIMENSION && col >= 0 && col < MATRIX_DIMENSION;
    }

    public static String getMoveDirection(int[] dir) {
        if (dir[0] == -1) {
            return "down";
        } else if (dir[0] == 1) {
            return "up";
        } else if (dir[1] == -1) {
            return "right";
        } else {
            return "left";
        }
    }

    public boolean isSolvable() {
        int inversions = countInversions(board);

        if (MATRIX_DIMENSION % 2 == 1) {
            return inversions % 2 == 0;
        } else {
            int totalSum = inversions + startingNode.zeroX;
            return totalSum % 2 == 1;
        }
    }

    public static int countInversions(int[] board) {
        int inversions = 0;
        int n = board.length;

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (board[i] != 0 && board[j] != 0 && board[i] > board[j]) {
                    inversions++;
                }
            }
        }

        return inversions;
    }
}

public class NPuzzle {

    public static void main(String[] args) {
        Hw1 hw1 = new Hw1();
        hw1.getInput();
        if(hw1.isSolvable()){
            long startTime = System.currentTimeMillis();
            Node goalNode = hw1.ida_star();
            long endTime = System.currentTimeMillis();
            double elapsedTimeSeconds = (endTime - startTime) / 1000.0;
            System.out.printf("Time taken: %.2f seconds\n", elapsedTimeSeconds);
            System.out.println(goalNode.distance);
            System.out.println(goalNode.pathToTheNode.toString());
        }else{
            System.out.println("Not solvable");
        }

    }
}
