package com.dgomesbr.awsemrstarter.app.Jobs.runner;

import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.model.*;
import com.dgomesbr.awsemrstarter.app.Jobs.base.JobConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by diego.magalhaes on 1/4/2015.
 */
@Component()
public class JobRunner {
    private static final Logger logger = LoggerFactory.getLogger(JobRunner.class);

    @Autowired
    private ApplicationContext context;
    @Autowired
    private AmazonElasticMapReduce emrClient;
    private Map<String,String> jobsAvailable = new HashMap();

    public static final String JOB_PWTIM125 = "BEAN-revive-log-recomendacao";
    public static final String JOB_PARAM_PWTIM125 = "recomendacao";

    public static final String JOB_VISUALIZACOES = "BEAN-revive-elb-visualizacoes";
    public static final String JOB_PARAM_VISUALIZACOES = "logs";

    @Value("${aws.logging.folder}")
    private String loggingFolder;

    @Value("${aws.ami.version}")
    private String amiVersion;

    @Value("${aws.jobs.keypair}")
    private String keyPair;

    public JobRunner() {
        jobsAvailable.put(JOB_PARAM_PWTIM125,JOB_PWTIM125);
        jobsAvailable.put(JOB_PARAM_VISUALIZACOES, JOB_VISUALIZACOES);
    }

    private RunJobFlowRequest ConfigJobFlowRequest(JobConfig job) {
        return new RunJobFlowRequest()
                .withName("[AUTO] " + job.getName())
                .withAmiVersion(amiVersion)
                .withSteps(job.steps())
                .withTags(job.tags())
                .withLogUri(loggingFolder)
                .withInstances(new JobFlowInstancesConfig()
                        .withEc2KeyName(keyPair)
                        .withInstanceCount(1)
                        .withKeepJobFlowAliveWhenNoSteps(false)
                        .withMasterInstanceType("r3.xlarge"));
    }

    public void startJob(String jobKey){
        if (jobsAvailable.containsKey(jobKey)){
            JobConfig job = (JobConfig) context.getBean(jobsAvailable.get(jobKey));
            RunJobFlowResult submission = emrClient.runJobFlow( ConfigJobFlowRequest(job) );
            String jobFlowId = submission.getJobFlowId();
            logger.info("Submitted EMR Job " + submission);

            DescribeClusterResult result =
                    null;
            try {
                result = waitForCompletion(jobFlowId, 60, TimeUnit.SECONDS);
                diagnoseClusterResult(result, jobFlowId);
            } catch (InterruptedException e) {
                emrClient.shutdown();
            }
        }else{
            logger.error("Nenhum job encontrado para a chave [" + jobKey + "]");
        }
    }

    private DescribeClusterResult waitForCompletion(String jobFlowId,long sleepTime, TimeUnit timeUnit) throws InterruptedException {
        String state = "STARTING";
        while (true) {
            DescribeClusterResult result = emrClient.describeCluster(
                    new DescribeClusterRequest().withClusterId(jobFlowId)
            );
            ClusterStatus status = result.getCluster().getStatus();
            String newState = status.getState();
            if (!state.equals(newState)) {
                logger.info("Cluster id {} switched from {} to {}.  Reason: {}.",
                        jobFlowId, state, newState, status.getStateChangeReason());
                state = newState;
            }

            switch (state) {
                case "TERMINATED":
                case "TERMINATED_WITH_ERRORS":
                case "WAITING":
                    return result;
            }

            timeUnit.sleep(sleepTime);
        }
    }

    private void diagnoseClusterResult(DescribeClusterResult result, String jobFlowId) {
        ClusterStatus status = result.getCluster().getStatus();
        ClusterStateChangeReason reason = status.getStateChangeReason();
        ClusterStateChangeReasonCode code =
                ClusterStateChangeReasonCode.fromValue(reason.getCode());
        switch (code) {
            case ALL_STEPS_COMPLETED:
                logger.info("Completed EMR job {}", jobFlowId);
                break;
            default:
                failEMR(jobFlowId, status);
        }
    }

    private static void failEMR(String jobFlowId, ClusterStatus status) {
        String msg = "EMR cluster run %s terminated with errors.  ClusterStatus = %s";
        throw new RuntimeException(String.format(msg, jobFlowId, status));
    }

}
