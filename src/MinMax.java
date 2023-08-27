import java.util.ArrayList;
import java.util.Scanner;

class TicTacToeNode{
    int[][] board;
    int[] lastMove = new int[2];
    int freeMovesLeft;
    int winner = 0;

    public void printWinner(){
        switch(winner){
            case -1:
                System.out.println("Human wins");
                break;
            case 1:
                System.out.println("Computer wins");
                break;
            default:
                System.out.println("Draw");
        }
    }

    public void printBoard(){
        for (int row = 0; row < 3; row++){
            for (int col = 0; col < 3; col++){
                if(board[row][col]==-1){
                    System.out.print("x ");
                }else if(board[row][col] == 1){
                    System.out.print("o ");
                }else{
                    System.out.print("_ ");
                }
            }
            System.out.println();
        }
    }

    public TicTacToeNode(){
        freeMovesLeft = 9;
        this.board = new int[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
        };
    }
    public boolean isThereAwinner(){
        int[][] winningPositions = new int[][]{
                {0,0}, {0,1}, {0,2},
                {1,0}, {1,1}, {1,2},
                {2,0}, {2,1}, {2,2},
                {0,0}, {1,0}, {2,0},
                {0,1}, {1,1}, {2,1},
                {0,2}, {1,2}, {2,2},
                {0,0}, {1,1}, {2,2},
                {0,2}, {1,1}, {2,0}
        };
        for (int i = 0; i < winningPositions.length; i+=3){
            int sum = board[winningPositions[i][0]][winningPositions[i][1]]
                    + board[winningPositions[i+1][0]][winningPositions[i+1][1]]
                    + board[winningPositions[i+2][0]][winningPositions[i+2][1]];
            if(sum == 3 || sum == -3){
                winner = board[winningPositions[i][0]][winningPositions[i][1]];
                return true;
            }
        }
        return false;
    }
    public TicTacToeNode(TicTacToeNode other){
        this.board = new int[3][3];
        this.freeMovesLeft = other.freeMovesLeft;
        for (int row = 0; row < 3; row++){
            for (int col = 0; col < 3; col++){
                this.board[row][col] = other.board[row][col];
            }
        }
    }
    public boolean pickMove(int row, int col, int type){
        if(freeMovesLeft>0 && this.board[row][col]==0){
            this.board[row][col] = type;
            lastMove[0] = row;
            lastMove[1] = col;
            freeMovesLeft--;
            return true;
        }
        return false;
    }
}

class Hw4{
    int[] lastMove;
    public int minmax(TicTacToeNode node, int depth, int alpha, int beta, boolean maximizingPlayer){
        if(depth == 0 || isGameOver(node)){
            return getStaticEvaluation(node);
        }

        if(maximizingPlayer){
            int maxEval = Integer.MIN_VALUE;
            int[] lastMove = new int[]{-1,-1};
            for(TicTacToeNode move : getPositionMoves(node, maximizingPlayer)){
                int eval = minmax(move, depth - 1, alpha, beta, false);
                if(eval > maxEval){
                    maxEval = eval;
                    lastMove = move.lastMove;
                }
                if (beta <= Math.max(alpha, eval)){
                    break;
                }
            }
            this.lastMove = lastMove;
            return maxEval;
        }else{
            int minEval = Integer.MAX_VALUE;
            for(TicTacToeNode move : getPositionMoves(node, maximizingPlayer)){
                int eval = minmax(move, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                if (Math.min(beta, eval) <= alpha){
                    break;
                }
            }
            return minEval;
        }
    }

    public boolean isGameOver(TicTacToeNode node){
        return node.freeMovesLeft<=0 || node.isThereAwinner();
    }

    private int getStaticEvaluation(TicTacToeNode node){
        return node.winner;
    }

    private ArrayList<TicTacToeNode> getPositionMoves(TicTacToeNode node, boolean maximizingPlayer){
        ArrayList<TicTacToeNode> possibleMoves = new ArrayList<>();
        for (int row = 0; row < 3; row++){
            for (int col = 0; col < 3; col++){
                if(node.board[row][col]==0){
                    TicTacToeNode newMove = new TicTacToeNode(node);
                    if(maximizingPlayer){
                        newMove.pickMove(row,col,1);
                    }else{
                        newMove.pickMove(row,col,-1);
                    }
                    possibleMoves.add(newMove);
                }
            }
        }
        return possibleMoves;
    }
}

public class MinMax {

    public static void main(String[] args) {
        Hw4 hw4 = new Hw4();
        Scanner sc = new Scanner(System.in);
        System.out.printf("Start of game");
        System.out.println("Are you first? 1 - yes 0 - no:");
        int isPlayerFirst = sc.nextInt();
        boolean playerOnMove = isPlayerFirst == 1;
        TicTacToeNode game = new TicTacToeNode();
        while(!hw4.isGameOver(game)){
            System.out.println("State of game");
            game.printBoard();
            if(playerOnMove){
                System.out.println("Pick a move. row col: ");
                int row = sc.nextInt();
                int col = sc.nextInt();
                game.pickMove(row,col,-1);
                playerOnMove = false;
            }else{
                hw4.minmax(game,9,Integer.MIN_VALUE,Integer.MAX_VALUE,true);
                game.pickMove(hw4.lastMove[0],hw4.lastMove[1],1);
                playerOnMove = true;
            }
        }
        System.out.println("Game over");
        game.printBoard();
        game.printWinner();
    }
}
