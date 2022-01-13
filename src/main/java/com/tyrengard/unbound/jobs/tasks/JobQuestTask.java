package com.tyrengard.unbound.jobs.tasks;

import com.tyrengard.unbound.jobs.actions.Action;
import com.tyrengard.unbound.jobs.quests.internal.JobQuest;

public interface JobQuestTask extends Task<JobQuest> {
    int getId();
    int getProgressRequired();
    String getStatusString(int currentProgress);

    abstract class Base implements JobQuestTask {
        protected final int id;
        protected final Action action;
        protected final JobQuest source;
        protected final int amount;

        public Base(int id, Action action, JobQuest source, int amount) {
            this.id = id;
            this.action = action;
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
        public Action getAction() {
            return action;
        }

        @Override
        public int getProgressRequired() {
            return amount;
        }
    }
}
