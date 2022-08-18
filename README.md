# OpenLegacy Proof of Concept

## Warnings and Limitations
 - The proof-of-concept accelerator adds the user supplied license and api keys to the applications properties. Secrets should be fetched from a secure store not hardcoded.
 - `Tiltfile` live_update sync assumes VisualStudio Code which auto-compiles Java into `./bin/main
## Overview

This proof-of-concept illustrates several integration options between [OpenLegacy](https://www.openlegacy.com/) and [Tanzu Application Platform (TAP)](https://tanzu.vmware.com/application-platform) and assumes TAP infrastructure based on the [TAP Reference Architecture](https://github.com/vmware-tanzu-labs/tanzu-validated-solutions/blob/main/src/reference-designs/tap-architecture-planning.md) has already been created and configured per corporate operations and security standards.

### Personas

The definition of these personas is based on a TAP Accelerator integration. Other styles of integration are possible and would alter the boundaries and activities of these personas.
#### API Designer / Integration Dev
- Knows and has access to OpenLegacy Hub and Tanzu Application Platform
- Activities
    - Understands company API needs
    - Uses OpenLegacy Hub to create and configure projects to satisy API needs
    - Generates Services from OpenLegacy Hub, downloads it, adds files for TAP integration, and configures it to meet corporate standards
    - Publishes the TAP Accelerator
#### Lead Application Developer
- Does not have access to OpenLegacy Hub, the TAP Secure Software Supply Chain or QA/Test/Prod Run clusters
- Needs access to a TAP Iterate Kubernetes Cluster
- Only requires basic knowledge of `tanzu` and `kubectl` clis
- Activities
    - Chooses the neccessary TAP Accelerator, fills in the requried fields, and generates.
    - Downloads and unzips the Zip file.
    - Initializes a Git repo and pushes it.
    - Ensures their workstation is targeting the correct Kubernetes cluster and namespace.
    - Runs `tilt up` to deploy the new workload and start development.

## Deploying to Tanzu Application Platform
### Developer inner-loop
`tilt up`
- The [Tiltfile](Tiltfile) applies the [TAP Workload](config/workload.yaml) to the developers namespace and configures live-update to synchronize local file changes to the remote Kubernetes pod.
- The initial `tilt up` will take some time as a full image build and push is run. After this a full build will only run if a file is changed that is not hot-loadable (e.g. a gradle build file).

 `tilt down`
- Deletes the TAP workload.

### CLI driven
`tanzu app workload create WORKLOAD_NAME --git-repo REPO_URL --git-branch REPO_BRANCH --type web --label app.kubernetes.io/part-of=WORKLOAD_NAME`

### Workload driven
`tanzu app wld apply -f config/workload.yaml`

## Modifications to OpenLegacy generated files

- build.gradle.kts
    - Builpacks
        - Hardcoded kotlin and java versions in `plugins` section as they were not getting resolved in the build pod
        - Commented out `withSourcesJar()`. If two jar files are built neither is exploded preventing live reload from working. Having a sources jar in the container which could be deployed to production is also problematic.
- application.yaml
    - Replaced the license and api-key values in [application.yml](src/main/resources/application.yml) with placeholders. These should be retrieved from a secret store at runtime. In this PoC the Accelerator will substitute them back into [application.yml](src/main/resources/application.yml) based on user input.
- Added a gradle wrapper and updated gradle to 4.7.178