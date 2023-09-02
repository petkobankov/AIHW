import java.util.*;

class Node {
    static final int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    int[] matrix;
    int distance = 0;
    int zeroX;
    int zeroY;
    String pathToTheNode;
    int manhattanDistance;

    public Node(Node other, int newZeroX, int newZeroY, String moveDirection, int[] baseMatrix){
        this.matrix = baseMatrix;
        matrix[Hw1.twoDtoOneD(other.zeroX,other.zeroY)] = matrix[Hw1.twoDtoOneD(newZeroX, newZeroY)];
        matrix[Hw1.twoDtoOneD(newZeroX, newZeroY)] = 0;
        int manhattan1 = getManhattanDistanceCell(other.zeroX,other.zeroY);
        int manhattan2 = getManhattanDistanceCell(newZeroX,newZeroY);
        int otherManhattan1 = other.getManhattanDistanceCell(other.zeroX,other.zeroY);
        int otherManhattan2 = other.getManhattanDistanceCell(newZeroX,newZeroY);
        this.manhattanDistance = other.manhattanDistance - otherManhattan1 - otherManhattan2 + manhattan1 + manhattan2;
        this.distance = other.distance+1;
        this.zeroX = newZeroX;
        this.zeroY = newZeroY;
        this.pathToTheNode = other.pathToTheNode;
        this.pathToTheNode += ", " + moveDirection;
    }

    public Node(){
        this.distance = 0;
        this.pathToTheNode = "";
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
        if(row>Hw1.zeroXgoal || (row==Hw1.zeroXgoal && column > Hw1.zeroYgoal)){
            value++;
        }
        int targetRow = (value - 1) / Hw1.MATRIX_DIMENSION;
        int targetCol = (value - 1) % Hw1.MATRIX_DIMENSION;
        if(matrix[Hw1.twoDtoOneD(row,column)] == 0){
            targetRow = Hw1.zeroXgoal;
            targetCol = Hw1.zeroYgoal;
        }
        return Math.abs(row - targetRow) + Math.abs(column - targetCol);
    }
    public PriorityQueue<Node> successors() {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                int f1 = node1.distance + node1.manhattanDistance;
                int f2 = node2.distance + node2.manhattanDistance;
                return Integer.compare(f1, f2);
            }
        });

        for (int i = 0; i < 4; i++){
            int newZeroX = zeroX + directions[i][0];
            int newZeroY = zeroY + directions[i][1];
            if (Hw1.isPosValid(newZeroX, newZeroY)) {
                int[] successorMatrix = new int[Hw1.MATRIX_DIMENSION*Hw1.MATRIX_DIMENSION];
                System.arraycopy(matrix, 0, successorMatrix, 0, Hw1.MATRIX_DIMENSION*Hw1.MATRIX_DIMENSION);
                String moveDirection = Hw1.getMoveDirection(directions[i]);
                Node successor = new Node(this,newZeroX,newZeroY,moveDirection,successorMatrix);
                priorityQueue.offer(successor);
            }
        }
        return priorityQueue;
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
    private int N;
    public static int MATRIX_DIMENSION;
    private static final int FOUND = -2;
    private Node startingNode;
    private int[] board;
    private Set<Node> visitedNodes = new HashSet<>();
    public static int zeroXgoal;
    public static int zeroYgoal;
    HashSet<String> uniqueStates;
    Node lastNode;

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
        startingNode = new Node();
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
        startingNode.manhattanDistance = startingNode.getManhattanDistance();
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



//    private boolean isGoal(Node node) {
//            int goalValue = 1;
//            for (int i = 0; i < MATRIX_DIMENSION; i++) {
//                for (int j = 0; j < MATRIX_DIMENSION; j++) {
//                    if (node.matrix[i][j] != goalValue % (MATRIX_DIMENSION * MATRIX_DIMENSION)) {
//                        return false;
//                    }
//                    goalValue++;
//                }
//            }
//            return true;
//    }

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
