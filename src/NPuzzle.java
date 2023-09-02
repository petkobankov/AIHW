import java.util.*;

class Node {
    int[][] matrix;
    int distance = 0;
    int zeroX;
    int zeroY;
    String pathToTheNode;
    int manhattanDistance;

    public Node(Node other, int newZeroX, int newZeroY, String moveDirection, int[][] baseMatrix){
        this.matrix = baseMatrix;
        matrix[other.zeroX][other.zeroY] = matrix[newZeroX][newZeroY];
        matrix[newZeroX][newZeroY] = 0;
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

    public Node(int[][] startingMatrix, int zeroX, int zeroY, String pathToTheNode){
        this.matrix = new int[Hw1.MATRIX_DIMENSION][Hw1.MATRIX_DIMENSION];
        for (int i = 0; i < Hw1.MATRIX_DIMENSION; i++){
            for (int j = 0; j < Hw1.MATRIX_DIMENSION; j++){
                this.matrix[i][j] = startingMatrix[i][j];
            }
        }
        this.manhattanDistance = getManhattanDistance();
        this.distance = 0;
        this.zeroX = zeroX;
        this.zeroY = zeroY;
        this.pathToTheNode = pathToTheNode;
    }
    private int getManhattanDistance() {
        int sum = 0;
        for (int i = 0; i < Hw1.MATRIX_DIMENSION; i++) {
            for (int j = 0; j < Hw1.MATRIX_DIMENSION; j++) {
                int value = matrix[i][j];
                int targetRow = (value - 1) / Hw1.MATRIX_DIMENSION;
                int targetCol = (value - 1) % Hw1.MATRIX_DIMENSION;
                if(value==0){
                    targetRow = Hw1.MATRIX_DIMENSION-1;
                    targetCol = Hw1.MATRIX_DIMENSION-1;
                }

                sum += Math.abs(i - targetRow) + Math.abs(j - targetCol);
            }
        }
        return sum;
    }

    private int getManhattanDistanceCell(int row, int column) {
        int value = matrix[row][column];
        int targetRow = (value - 1) / Hw1.MATRIX_DIMENSION;
        int targetCol = (value - 1) % Hw1.MATRIX_DIMENSION;
        if(value==0){
            targetRow = Hw1.MATRIX_DIMENSION-1;
            targetCol = Hw1.MATRIX_DIMENSION-1;
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
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int i = 0; i < 4; i++){
            int newZeroX = zeroX + directions[i][0];
            int newZeroY = zeroY + directions[i][1];
            if (Hw1.isPosValid(newZeroX, newZeroY)) {
                int[][] successorMatrix = new int[Hw1.MATRIX_DIMENSION][Hw1.MATRIX_DIMENSION];
                for (int row = 0; row < Hw1.MATRIX_DIMENSION; row++) {
                    System.arraycopy(matrix[row], 0, successorMatrix[row], 0, Hw1.MATRIX_DIMENSION);
                }
                String moveDirection = Hw1.getMoveDirection(directions[i]);
                Node successor = new Node(this,newZeroX,newZeroY,moveDirection,successorMatrix);
                priorityQueue.offer(successor);
            }
        }
        return priorityQueue;
    }
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(matrix);
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
        return Arrays.deepEquals(this.matrix, other.matrix);
    }
}

class Hw1 {
    private int N;
    public static int MATRIX_DIMENSION;
    private static final int FOUND = -2;
    private Node startingNode;
    private int[] board;
    private Set<Node> visitedNodes = new HashSet<>();

    public void getInput() {
        Scanner sc = new Scanner(System.in);
        N = sc.nextInt() + 1;
        int zeroPosition = sc.nextInt();
        MATRIX_DIMENSION = (int) Math.sqrt(N);
        int[][] startingNodeMatrix = new int[Hw1.MATRIX_DIMENSION][Hw1.MATRIX_DIMENSION];
        board = new int[N];
        for (int i = 0; i < N; i++) {
            int currentPuzzleNumber = sc.nextInt();
            int row = oneDtoTwoD(i)[0];
            int col = oneDtoTwoD(i)[1];
            startingNodeMatrix[row][col] = currentPuzzleNumber;
            board[i] = currentPuzzleNumber;
        }
        startingNode = new Node(startingNodeMatrix,oneDtoTwoD(N-1)[0], oneDtoTwoD(N-1)[1], "");
        if (zeroPosition != -1) {
            startingNode.zeroX = oneDtoTwoD(zeroPosition)[0];
            startingNode.zeroY = oneDtoTwoD(zeroPosition)[1];
        }
    }

    private int[] oneDtoTwoD(int position) {
        int[] twoDPosition = new int[2];
        twoDPosition[0] = position / MATRIX_DIMENSION;
        twoDPosition[1] = position % MATRIX_DIMENSION;
        return twoDPosition;
    }

    public Node ida_star() {
        int bound = startingNode.manhattanDistance;
        Stack<Node> path = new Stack<>();
        path.push(startingNode);
        visitedNodes.add(startingNode);
        while (true) {
            int t = search(path, 0, bound);
            if (t == FOUND) {
                return path.pop();
            }
            if (t == Integer.MAX_VALUE) {
                return null;
            }
            bound = t;
        }
    }

    private int search(Stack<Node> path, int g, int bound) {
        Node node = path.peek();
        int f = g + node.manhattanDistance;
        if (f > bound) {
            return f;
        }
        if (isGoal(node)) {
            return FOUND;
        }
        int min = Integer.MAX_VALUE;
        PriorityQueue<Node> successors = node.successors();
        for (Node successor : successors) {
            if(!visitedNodes.contains(successor)){
                path.add(successor);
                visitedNodes.add(successor);
                int t = search(path, g + 1, bound);
                if ( t == FOUND){
                    return FOUND;
                }
                if (t < min){
                    min = t;
                }
                path.pop();
                visitedNodes.remove(successor);
            }else{
                //System.out.println(successor);
            }
        }
        return min;
    }



    private boolean isGoal(Node node) {
        int goalValue = 1;
        for (int i = 0; i < MATRIX_DIMENSION; i++) {
            for (int j = 0; j < MATRIX_DIMENSION; j++) {
                if (node.matrix[i][j] != goalValue % (MATRIX_DIMENSION * MATRIX_DIMENSION)) {
                    return false;
                }
                goalValue++;
            }
        }
        return true;
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
