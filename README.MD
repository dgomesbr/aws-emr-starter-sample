# AWS Elastic Map Reduce Starter Sample
Amazon EMR Starter Sample for running jobs programmatically

[![Build Status](https://travis-ci.org/dgomesbr/aws-emr-starter-sample.svg)](https://travis-ci.org/dgomesbr/aws-emr-starter-sample)

* Has an entry point on JobRunner
* Will create an job which implement JobConfig
* Will create a Cluster and run the steps configured
* After the completion of it the cluster will terminated automatically
* All the property needed for the AWS-SDK interaction is located at /resources/application.properties

The Job steps and instances are configured on the ```ConfigJobFlowRequest``` method, for the sake of the simplicity
creates a job with only a masterInstance with the size of r3.xlarge
```java
    private RunJobFlowRequest ConfigJobFlowRequest(JobConfig job) {
        return new RunJobFlowRequest()
                .withName(job.getName())
                .withAmiVersion(amiVersion)
                .withSteps(job.steps())
                .withTags(job.tags())
                .withLogUri(loggingFolder)
                .withInstances(new JobFlowInstancesConfig()
                        .withEc2KeyName(keyPair)
                        .withInstanceCount(1)
                        .withKeepJobFlowAliveWhenNoSteps(false)
                        .withMasterInstanceType(masterInstanceType));
    }
```