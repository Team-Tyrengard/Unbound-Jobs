package com.tyrengard.unbound.jobs.tasks;

import com.tyrengard.unbound.jobs.Job;

public interface JobTask extends Task<Job> {
    double getBasePay();
    int getBaseExp();

    abstract class Base implements JobTask {
        protected final TaskType type;
        protected final Job source;
        protected final double basePay;
        protected final int baseExp;

        public Base(TaskType type, Job source, double basePay, int baseExp) {
            this.type = type;
            this.source = source;
            this.basePay = basePay;
            this.baseExp = baseExp;
        }

        @Override
        public TaskType getType() {
            return type;
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
