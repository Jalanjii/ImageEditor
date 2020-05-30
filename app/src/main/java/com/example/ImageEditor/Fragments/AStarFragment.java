package com.example.ImageEditor.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ImageEditor.AstarAlgorithm.GridView;
import com.example.ImageEditor.AstarAlgorithm.ProcessAlgorithm;
import com.example.ImageEditor.R;

public class AStarFragment extends Fragment {
    private GridView gridView;
    private Button buttonStartPoint, buttonEndPoint, buttonStartAlgorithm, buttonChange;
    private EditText editTextRows, editTextCols;
    private CheckBox isDiagonal;
    private View view;
    private boolean diagonal = false;
    private int cols, rows;

    public static Fragment newInstance() {
        return new AStarFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_astar, container, false);
        defineIds();

        cols = 20;
        rows = 20;

        gridView.init(rows, cols);

        isDiagonal.setOnClickListener(v -> diagonal = isDiagonal.isChecked());


        buttonStartPoint.setOnClickListener(v -> gridView.setStart_clicked(true));
        buttonEndPoint.setOnClickListener(v -> gridView.setStop_clicked(true));
        buttonStartAlgorithm.setOnClickListener(v -> startAlgorithm());
        buttonChange.setOnClickListener(v -> changeSize());


        return view;
    }

    private void defineIds() {
        gridView = view.findViewById(R.id.grid);
        buttonStartPoint = view.findViewById(R.id.buttonStartPoint);
        buttonEndPoint = view.findViewById(R.id.buttonEndPoint);
        buttonStartAlgorithm = view.findViewById(R.id.buttonStartAlgorithm);
        buttonChange = view.findViewById(R.id.buttonChange);
        isDiagonal = view.findViewById(R.id.checkBoxDiagonal);
        editTextRows = view.findViewById(R.id.editTextRows);
        editTextCols = view.findViewById(R.id.editTextCols);
    }

    private void changeSize() {
        int colsTemp = cols;
        int rowsTemp = rows;
        cols = Integer.parseInt(editTextCols.getText().toString().isEmpty() ? "20" : editTextCols.getText().toString());
        rows = Integer.parseInt(editTextRows.getText().toString().isEmpty() ? "20" : editTextRows.getText().toString());
        if (rows >= 3 && cols >= 3) {
            gridView.setRowsNCols(rows, cols);
        } else {
            cols = colsTemp;
            rows = rowsTemp;
            Toast.makeText(getContext(), "Min rows and cols value is 3", Toast.LENGTH_LONG).show();
        }

    }

    private void startAlgorithm() {
        gridView.clearPath();
        int[][] grid = gridView.getGrid();
        ProcessAlgorithm algorithm = new ProcessAlgorithm(cols, rows, gridView, diagonal);
        algorithm.execute(grid);
    }
}
