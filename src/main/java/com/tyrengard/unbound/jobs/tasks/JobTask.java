package com.tyrengard.unbound.jobs.tasks;

import com.tyrengard.unbound.jobs.Job;
import com.tyrengard.unbound.jobs.actions.Action;

public interface JobTask extends Task<Job> {
    double getBasePay();
    int getBaseExp();

    abstract class Base implements JobTask {
        protected final Action action;
        protected final Job source;
        protected final double basePay;
        protected final int baseExp;

        public Base(Action action, Job source, double basePay, int baseExp) {
            this.action = action;
            this.source = source;
            this.basePay = basePay;
            this.baseExp = baseExp;
        }

        @Override
        public Action getAction() {
            return action;
        }

        @Override
        public Job getSource() {
            return source;
        }

        @Override
        public double getBasePay() {
            return basePay;
        }

        @Override
        public int getBaseExp() {
            return baseExp;
        }
    }
}
