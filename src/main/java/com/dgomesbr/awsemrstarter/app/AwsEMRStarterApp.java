package com.dgomesbr.awsemrstarter.app;

import com.dgomesbr.awsemrstarter.app.Config.AWSConfig;
import com.dgomesbr.awsemrstarter.app.Jobs.runner.JobRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(AWSConfig.class)
public class AwsEMRStarterApp {
    private static final Logger logger = LoggerFactory.getLogger(AwsEMRStarterApp.class);

    public static void main(String[] args) throws Exception {// NOSONAR
        ApplicationContext context = SpringApplication.run(AwsEMRStarterApp.class, args);

        if (args.length != 2 && !"j".equalsIgnoreCase(args[0])) {
            showUsage();
            return;
        }

        String jobName = args[1];
        logger.info("Starting job [" + jobName + "]");
        JobRunner runner = (JobRunner) context.getBean(JobRunner.class);
        runner.startJob(jobName);
        System.exit(SpringApplication.exit(context));
    }

    private static void showUsage() {
        logger.warn("Usage: AwsEMRStarterApp -j [logs|recomendacao]");
    }

}