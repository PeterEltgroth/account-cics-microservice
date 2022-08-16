#!bash
SOURCE_IMAGE = os.getenv("SOURCE_IMAGE", default='tanzuapplicationplatform.azurecr.io/supply-chain/account-cics-microservice-source')
LOCAL_PATH = os.getenv("LOCAL_PATH", default='.')
NAMESPACE = os.getenv("NAMESPACE", default='default')

allow_k8s_contexts('arn:aws:eks:us-west-1:580953995046:cluster/1-cl-tap')

k8s_custom_deploy(
    'account-cics-microservice',
    apply_cmd="tanzu apps workload apply -f config/workload.yaml --debug --live-update" +
              " --local-path " + LOCAL_PATH +
              " --source-image " + SOURCE_IMAGE +
              " --namespace " + NAMESPACE +
              " --yes >/dev/null" +
              " && kubectl get workload account-cics-microservice --namespace " + NAMESPACE + " -o yaml",
    delete_cmd="tanzu apps workload delete -f config/workload.yaml --namespace " + NAMESPACE + " --yes",
    deps=['build.gradle.kts', './bin/main'],
    container_selector='workload',
    live_update=[
      sync('./bin/main', '/workspace/BOOT-INF/classes')
    ]
)

k8s_resource('account-cics-microservice', port_forwards=["8080:8080"],
extra_pod_selectors=[{'serving.knative.dev/service': 'account-cics-microservice'}])
