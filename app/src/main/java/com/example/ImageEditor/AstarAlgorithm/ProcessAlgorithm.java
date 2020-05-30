package com.example.ImageEditor.AstarAlgorithm;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class ProcessAlgorithm extends AsyncTask<int[][], Void, List<Node>> {
    private int cols;
    private int rows;
    private ResultContract contract;
    private boolean isDiagonal;

    public ProcessAlgorithm(int cols, int rows, ResultContract contract, boolean isDiagonal) {
        this.cols = cols;
        this.rows = rows;
        this.contract = contract;
        this.isDiagonal = isDiagonal;
    }

    @Override
    protected void onPostExecute(List<Node> nodes) {
        super.onPostExecute(nodes);
        contract.getResult(nodes);

    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected List<Node> doInBackground(int[][]... ints) {

        int[][] grid = ints[0];

        Node startNode = null;
        Node endNode = null;
        List<Node> blockList = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int value = grid[r][c];

                if (value == GridView.TILE_START) {
                    startNode = new Node(r, c);
                } else if (value == GridView.TILE_END) {
                    endNode = new Node(r, c);
                } else if (value == GridView.TILE_BLOCK) {
                    blockList.add(new Node(r, c));
                }
            }
        }

        if(startNode != null && endNode != null){

            AStar aStar = new AStar(rows, cols, startNode, endNode, isDiagonal);
            aStar.setBlocks(blockList);
            return aStar.findPath();
        }


        return null;
    }
}
