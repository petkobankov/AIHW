import java.util.*;

class Node {
    int[][] matrix;
    int distance = 0;
    int zeroX;
    int zeroY;
    ArrayList<String> pathToTheNode;
    public Node(int MATRIX_DIMENSION, int[][] matrix, int distance, int zeroX, int zeroY, List<String> pathToTheNode, String moveDirection){
        this.matrix = new int[MATRIX_DIMENSION][MATRIX_DIMENSION];
        if(matrix != null){
            for (int i = 0; i < MATRIX_DIMENSION; i++){
                for (int j = 0; j < MATRIX_DIMENSION; j++){
                    this.matrix[i][j] = matrix[i][j];
                }
            }
        }
        this.distance = distance;
        this.zeroX = zeroX;
        this.zeroY = zeroY;
        this.pathToTheNode = new ArrayList<String>();
        for(int i = 0; i < pathToTheNode.size(); i++) {
            this.pathToTheNode.add(pathToTheNode.get(i));
        }
        if(!moveDirection.isEmpty()){
            this.pathToTheNode.add(moveDirection);
        }
    }

    public Node(int MATRIX_DIMENSION, int distance, int zeroX, int zeroY, List<String> pathToTheNode){
        this(MATRIX_DIMENSION, null, distance, zeroX, zeroY, pathToTheNode, "");
    }
}

class Hw1 {
    private int N;
    private int MATRIX_DIMENSION;
    private static final int FOUND = -2;
    private Node startingNode;
    private int[] board;
    private Set<Node> visitedNodes = new HashSet<>();

    public void getInput() {
        Scanner sc = new Scanner(System.in);
        N = sc.nextInt() + 1;
        int zeroPosition = sc.nextInt();
        MATRIX_DIMENSION = (int) Math.sqrt(N);
        startingNode = new Node(MATRIX_DIMENSION, 0, oneDtoTwoD(N-1)[0], oneDtoTwoD(N-1)[1], new ArrayList<String>());
        board = new int[N];
        for (int i = 0; i < N; i++) {
            int currentPuzzleNumber = sc.nextInt();
            int row = oneDtoTwoD(i)[0];
            int col = oneDtoTwoD(i)[1];
            startingNode.matrix[row][col] = currentPuzzleNumber;
            board[i] = currentPuzzleNumber;
        }
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
        int bound = getManhattanDistance(startingNode);
        Stack<Node> path = new Stack<>();
        path.push(startingNode);
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
        visitedNodes.add(node);
        int f = g + getManhattanDistance(node);
        if (f > bound) {
            return f;
        }
        if (isGoal(node)) {
            return FOUND;
        }
        int min = Integer.MAX_VALUE;

        for (Node succ : successors(node)) {
            if(!visitedNodes.contains(succ)){
                path.add(succ);
                visitedNodes.add(succ);
                int t = search(path, g + 1, bound);
                if ( t == FOUND){
                    return FOUND;
                }
                if (t < min){
                    min = t;
                }
                path.pop();
                visitedNodes.remove(succ);
            }
        }
        return min;
    }

    private int getManhattanDistance(Node node) {
        int sum = 0;
        for (int i = 0; i < MATRIX_DIMENSION; i++) {
            for (int j = 0; j < MATRIX_DIMENSION; j++) {
                int value = node.matrix[i][j];
                int targetRow = (value - 1) / MATRIX_DIMENSION;
                int targetCol = (value - 1) % MATRIX_DIMENSION;
                sum += Math.abs(i - targetRow) + Math.abs(j - targetCol);
            }
        }
        return sum;
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

    private ArrayList<Node> successors(Node node) {
        ArrayList<Node> successorsNodes = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int newZeroX = node.zeroX + dir[0];
            int newZeroY = node.zeroY + dir[1];
            if (isPosValid(newZeroX, newZeroY)) {
                Node successor = createSuccessorNode(node, newZeroX, newZeroY, dir);
                successorsNodes.add(successor);
            }
        }
        Collections.sort(successorsNodes, new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                int f1 = node1.distance + getManhattanDistance(node1);
                int f2 = node2.distance + getManhattanDistance(node2);
                return Integer.compare(f1, f2);
            }
        });
        return successorsNodes;
    }

    private Node createSuccessorNode(Node node, int newZeroX, int newZeroY, int[] dir) {
        String moveDirection = getMoveDirection(dir);
        Node successor = new Node(MATRIX_DIMENSION, node.matrix, node.distance + 1, newZeroX, newZeroY, node.pathToTheNode, moveDirection);
        successor.matrix[node.zeroX][node.zeroY] = successor.matrix[newZeroX][newZeroY];
        successor.matrix[newZeroX][newZeroY] = 0;
        return successor;
    }

    private boolean isPosValid(int row, int col) {
        return row >= 0 && row < MATRIX_DIMENSION && col >= 0 && col < MATRIX_DIMENSION;
    }

    private String getMoveDirection(int[] dir) {
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
