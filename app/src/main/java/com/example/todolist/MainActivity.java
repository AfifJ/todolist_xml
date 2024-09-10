// MainActivity.java
package com.example.todolist;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText taskInput;
    private Button addButton;
    private ListView taskList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> tasks;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskInput = findViewById(R.id.taskInput);
        addButton = findViewById(R.id.addButton);
        taskList = findViewById(R.id.taskList);

        dbHelper = new DatabaseHelper(this);
        tasks = dbHelper.getAllTasks();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        taskList.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = taskInput.getText().toString();
                if (!task.isEmpty()) {
                    dbHelper.addTask(task);
                    tasks.add(task);
                    adapter.notifyDataSetChanged();
                    taskInput.setText("");
                }
            }
        });

        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDeleteDialog(position);
            }
        });
    }

    private void showEditDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit or Delete Task");
        builder.setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
            if (which == 0) {
                showEditDialog(position);
            } else {
                deleteTask(position);
            }
        });
        builder.show();
    }

    private void showEditDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");
        final EditText input = new EditText(this);
        input.setText(tasks.get(position));
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTask = input.getText().toString();
            if (!newTask.isEmpty()) {
                dbHelper.updateTask(tasks.get(position), newTask);
                tasks.set(position, newTask);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void deleteTask(int position) {
        dbHelper.deleteTask(tasks.get(position));
        tasks.remove(position);
        adapter.notifyDataSetChanged();
    }
}