# OpenLegacy Proof of Concept

- [OpenLegacy Proof of Concept](#openlegacy-proof-of-concept)
    - [Warnings and Limitations](#warnings-and-limitations)
    - [Overview](#overview)
        - [Personas](#personas)
            - [API Designer / Integration Developer](#api-designer--integration-developer)
            - [Application Developer](#application-developer)
    - [Integration Options](#integration-options)
        - [No Code Service](#no-code-service)
        - [Code Project](#code-project)
        - [Application Accelerator](#application-accelerator)
        - [Backstage Plugin](#backstage-plugin)
    - [Modifications to OpenLegacy generated files](#modifications-to-openlegacy-generated-files)
## Warnings and Limitations

 - The proof-of-concept accelerator adds the user supplied license and api keys to the applications properties. Secrets should be fetched not hardcoded.
 - `Tiltfile` live_update sync assumes VisualStudio Code which auto-compiles Java into `./bin/main

## Overview

This proof-of-concept illustrates several integration options between [OpenLegacy](https://www.openlegacy.com/) and [Tanzu Application Platform (TAP)](https://tanzu.vmware.com/application-platform) and assumes TAP infrastructure based on the [TAP Reference Architecture](https://github.com/vmware-tanzu-labs/tanzu-validated-solutions/blob/main/src/reference-designs/tap-architecture-planning.md) has already been created and configured per corporate operations and security standards.

### Personas

The boundary between the personas below will change depending on the [integration option](#integration-options) that best fits the customer.

#### API Designer / Integration Developer
- Knows and has access to OpenLegacy Hub and Tanzu Application Platform
- Activities
    - Understands company API needs
    - Uses OpenLegacy Hub to create and configure projects to satisy API needs
    - Generates Services from OpenLegacy Hub
    - Provides the generated service to Application Developer by:
        1. Providing the Applicaiton Developer the URL to the OpenAPI endpoints
        2. An archive containing the generated service
        3. Wrapping the generated service into a TAP Accelerator and publishing it
#### Application Developer
- Does not have access to OpenLegacy Hub, the TAP Secure Software Supply Chain or QA/Test/Prod Run clusters
- Requires access to a TAP Iterate Kubernetes Cluster
- Only needs basic knowledge of `tanzu` and `kubectl` CLIs
- Activities
    - Depending on the integration style is provided:
        1. URL to the OpenAPI endpoints
        2. The archive
        3. The name of a TAP Accelerator

## Integration Options

### No Code Service

API Designer / Integration Developer
- Creates a code repository for the generated service
- Deploys the service with either:
    - DockerFile 
        ```bash
        tanzu apps workload create docker-cics \
        --git-repo https://github.com/PeterEltgroth/account-cics-microservice.git \
        --git-branch docker \
        --label "app.kubernetes.io/part-of=docker-cics" \
        --param dockerfile=Dockerfile \
        --type web
        ```
    - The build
        ```bash
        tanzu app workload apply -f config/workload.yaml
        ```
        or
        ```bash
        tanzu app workload create WORKLOAD_NAME \
        --git-repo REPO_URL \
        --git-branch REPO_BRANCH \
        --label "app.kubernetes.io/part-of=WORKLOAD_NAME" \
        --type web
        ```
- Provides the URL to OpenAPI endpoints and access information

Appplication Developer
- Starts making API calls to the OpenAPI endpoints in an existing or new project

### Code Project

API Designer / Integration Developer
- Provides the developer with the generated .zip or creates a source code repository and provides it

Appplication Developer
- Extracts or fetches/clones the archive
- Modifies configuration (application properties, workload, Tiltfile, etc.)
- Ensures they are pointed at the correct development/iterate Kubernetes cluster
- Runs `tilt up` to deploy and begin development
    - The [Tiltfile](Tiltfile) applies the [TAP Workload](config/workload.yaml) to the developers namespace and configures live update to synchronize local file changes to the remote Kubernetes pod.
    - The initial `tilt up` takes some time as a full image build and push is run. After this a full build only runs if a non-live updatable file is changed, for example a gradle build file.
- Runs `tilt down` to delete the TAP workload.

### Application Accelerator

API Designer / Integration Developer
- Creates and publishes an Application Accelerator

Appplication Developer
- Goes to TAP, finds the accelerator, fills in required fields, and clicks Generate Accelerator.
- Downlaods the zip
- Creates a source code repository at the location they specified in the TAP Accelerator
- Ensures they are pointed at the correct development/iterate Kubernetes cluster
- Runs `tilt up` to deploy and work and `tilt down` to delete the workload

### Backstage Plugin

Tanzu Application Platform GUI is built on [Backstage](https://backstage.io/) which has many [plugins](https://backstage.io/plugins); however, TAP GUI currently DOES NOT support the installation of third-party plugins.

## Modifications to OpenLegacy generated files

- build.gradle.kts
    - Builpacks
        - Hardcoded kotlin and java versions in `plugins` section as they were not getting resolved in the build pod
        - Commented out `withSourcesJar()`. If two jar files are built neither is exploded preventing live reload from working. Having a sources jar in the container which could be deployed to production is also problematic.
- application.yaml
    - Replaced the license and api-key values in [application.yml](src/main/resources/application.yml) with placeholders. These should be retrieved from a secret store at runtime. In this PoC the Accelerator will substitute them back into [application.yml](src/main/resources/application.yml) based on user input.
- Dockerfile
    - Modified into a [multi-stage build](https://docs.docker.com/develop/develop-images/multistage-build/) Docker to first build the application with [gradle 7.4.2-jdk11-alpine](https://hub.docker.com/layers/gradle/library/gradle/7.4.2-jdk11-alpine/images/sha256-1c1341f5e927d5c6eeb328486e7be68d5938b3e17c451b447e0c7c86c353e94d?context=explore), and then run it.
- Added a gradle wrapper and updated gradle to 7.4.2
