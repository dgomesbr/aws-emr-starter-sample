package com.dgomesbr.awsemrstarter.app.Jobs.base;

import com.amazonaws.services.elasticmapreduce.model.StepConfig;
import com.amazonaws.services.elasticmapreduce.model.Tag;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by diego.magalhaes on 1/4/2015.
 */
public interface JobConfig {
    static final String STEP_CONFIG_HIVE_INSTALL = "BEAN_STEP_CONFIG_HIVE_INSTALL";
    static final String STEP_CONFIG_HADOOP_DEBBUGING = "BEAN_STEP_CONFIG_HADOOP_DEBBUGING";


    LinkedHashSet<StepConfig> steps();

    String getName();

    List<Tag> tags();
}
