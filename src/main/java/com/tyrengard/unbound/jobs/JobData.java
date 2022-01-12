package com.tyrengard.unbound.jobs;

import dev.morphia.annotations.Entity;

@Entity
public record JobData(short level, int exp) {
}
