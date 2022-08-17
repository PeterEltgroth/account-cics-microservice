# OpenLegacy Proof of Concept

### Warnings and Limitations
 - The proof-of-concept accelerator adds the user supplied license and api keys to the applications properties. Secrets should be fetched from a secure store not hardcoded.
 - `Tiltfile` live_update sync assumes VisualStudio Code which auto-compiles Java into `./bin/main

## Deploying to Tanzu Application Platform

### CLI driven
`tanzu app workload create WORKLOAD_NAME --git-repo REPO_URL --git-branch REPO_BRANCH --type web --label app.kubernetes.io/part-of=WORKLOAD_NAME`

### Workload manifest driven
`tanzu app wld apply -f config/workload.yaml`
## Developer inner-loop
`tilt up`
- The [Tiltfile](Tiltfile) applies `config/workload.yaml` to the developers namespace and sync files for live-update.
 
 `tilt down` 
- Deletes the workload.
