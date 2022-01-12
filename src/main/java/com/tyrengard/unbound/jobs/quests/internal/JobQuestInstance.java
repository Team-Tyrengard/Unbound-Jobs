package com.tyrengard.unbound.jobs.quests.internal;

import com.tyrengard.unbound.jobs.tasks.TaskType;
import dev.morphia.annotations.Entity;

import java.sql.Time;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
public class JobQuestInstance {
    private final String questId;
    private final HashMap<Integer, Integer> taskProgress;
    private boolean active;

    public JobQuestInstance(String questId, Set<Integer> taskIds) {
        this.questId = questId;
        this.taskProgress = new HashMap<>();
        for (Integer taskId : taskIds) {
            taskProgress.put(taskId, 0);
        }
        this.active = true;
    }

    public JobQuestInstance(String questId, HashMap<Integer, Integer> taskProgress, boolean active) {
        this.questId = questId;
        this.taskProgress = taskProgress;
        this.active = active;
    }

    public String getQuestId() {
        return questId;
    }

    public int getProgress(int jobQuestTaskId) {
        return taskProgress.get(jobQuestTaskId);
    }

    public void setProgress(int id, int progress) {
        taskProgress.put(id, progress);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
