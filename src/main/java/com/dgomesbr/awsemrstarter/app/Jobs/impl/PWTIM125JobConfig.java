package com.dgomesbr.awsemrstarter.app.Jobs.impl;

import com.amazonaws.services.elasticmapreduce.model.ActionOnFailure;
import com.amazonaws.services.elasticmapreduce.model.HadoopJarStepConfig;
import com.amazonaws.services.elasticmapreduce.model.StepConfig;
import com.amazonaws.services.elasticmapreduce.model.Tag;
import com.amazonaws.services.elasticmapreduce.util.StepFactory;
import com.dgomesbr.awsemrstarter.app.Jobs.base.JobConfig;
import com.dgomesbr.awsemrstarter.app.Jobs.runner.JobRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by diego.magalhaes on 1/4/2015.
 */
@Component(JobRunner.JOB_PWTIM125)
public class PWTIM125JobConfig implements JobConfig {

    @Autowired @Qualifier(STEP_CONFIG_HIVE_INSTALL) StepConfig hive;

    @Autowired @Qualifier(STEP_CONFIG_HADOOP_DEBBUGING) StepConfig hadoopDebugging;

    @Override
    public LinkedHashSet<StepConfig> steps() {
        LinkedHashSet<StepConfig> lhs = new LinkedHashSet<>();

        lhs.add(hadoopDebugging);
        lhs.add(hive);
        lhs.add(customJar());
        lhs.add(hiveProgram());

        return lhs;
    }

    public StepConfig customJar(){
        HadoopJarStepConfig customJarStep = new HadoopJarStepConfig()
                .withJar("s3://tim-ads-lb-logs/AWSLogs/830062373251/EMRJavaInit/tim-ads-lb-logs.jar")
                .withArgs("recomendacao");
        return new StepConfig("Custom JAR", customJarStep).withActionOnFailure(ActionOnFailure.TERMINATE_CLUSTER);
    }

    public StepConfig hiveProgram(){
        HadoopJarStepConfig customJarStep = new StepFactory().newRunHiveScriptStep("s3://tim-ads-lb-logs/AWSLogs/830062373251/EMRHiveQuery/hive-script-recomendacao.q")
                .withJar("s3://elasticmapreduce/libs/script-runner/script-runner.jar");
        return new StepConfig("Hive Program", customJarStep).withActionOnFailure(ActionOnFailure.TERMINATE_CLUSTER);
    }

    @Override
    public String getName() {
        return "Revive Log - PWTIM-125";
    }

    @Override
    public List<Tag> tags() {
        return Arrays.asList(new Tag().withKey("Project").withValue("TIM-REVIVE-EMR-CLUSTER"));
    }
}
