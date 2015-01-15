package com.dgomesbr.awsemrstarter.app.Config;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient;
import com.amazonaws.services.elasticmapreduce.model.ActionOnFailure;
import com.amazonaws.services.elasticmapreduce.model.StepConfig;
import com.amazonaws.services.elasticmapreduce.util.StepFactory;
import com.dgomesbr.awsemrstarter.app.Jobs.base.JobConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class AWSConfig {
    /**
     * Credentials provider using application.properties
     * @return instance of ProfielCredentialsProvider
     */
    @Bean
    public ProfileCredentialsProvider credentialsProvider(){
        return new ProfileCredentialsProvider();
    }

    /**
     * The EMR Client that will be used in the jobs
     * @return instance of EMRClient
     */
    @Bean
    @Lazy
    public AmazonElasticMapReduce EMRClient(){
        return new AmazonElasticMapReduceClient(credentialsProvider().getCredentials());
    }

    /**
     * Predefined Step - Configure HIVE
     */
    @Bean(name = JobConfig.STEP_CONFIG_HIVE_INSTALL)
    @Lazy
    public StepConfig HiveStepConfig(){
        return new StepConfig("Setup hive", new StepFactory().newInstallHiveStep(StepFactory.HiveVersion.Hive_Latest)).withActionOnFailure(ActionOnFailure.TERMINATE_CLUSTER);
    }

    /**
     * Predefined Step - Setup Hadoop Debugging
     */
    @Bean(name = JobConfig.STEP_CONFIG_HADOOP_DEBBUGING)
    @Lazy
    public StepConfig HadoopDebuggingConfig(){
        return new StepConfig("Setup hadoop debugging", new StepFactory().newEnableDebuggingStep()).withActionOnFailure(ActionOnFailure.TERMINATE_CLUSTER);
    }
}
