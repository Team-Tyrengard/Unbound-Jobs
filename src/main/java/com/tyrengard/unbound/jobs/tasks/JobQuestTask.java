package com.tyrengard.unbound.jobs.tasks;

import com.tyrengard.unbound.jobs.quests.internal.JobQuest;

public interface JobQuestTask extends Task<JobQuest> {
    int getId();
    int getProgressRequired();
    String getStatusString(int currentProgress);

    abstract class Base implements JobQuestTask {
        protected final int id;
        protected final TaskType type;
        protected final JobQuest source;
        protected final int amount;

        public Base(int id, TaskType type, JobQuest source, int amount) {
            this.id = id;
            this.type = type;
            this.source = source;
            this.amount = amount;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public JobQuest getSource() {
            return source;
        }

        @Override
        public TaskType getType() {
            return type;
        }

        @Override
        public int getProgressRequired() {
            return amount;
        }
    }
}
