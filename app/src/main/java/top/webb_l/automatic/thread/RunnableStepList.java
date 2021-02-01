package top.webb_l.automatic.thread;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import top.webb_l.automatic.adapter.EditStepAdapter;
import top.webb_l.automatic.model.Steps;

public class RunnableStepList implements Runnable {
    private Thread t;
    private final Context mContext;
    private final RecyclerView stepList;
    private final List<Steps> steps;
    private final EditStepAdapter adapter;

    public RunnableStepList(Context context, RecyclerView stepList, List<Steps> steps, EditStepAdapter adapter) {
        this.mContext = context;
        this.stepList = stepList;
        this.steps = steps;
        this.adapter = adapter;
    }

    @Override
    public void run() {

    }

    public void start() {
        if (t == null) {
            t = new Thread(this, "StepList");
            t.start();
        }
    }

    public void stop() {
        if (t == null) {
            t.stop();
        }
    }
}
