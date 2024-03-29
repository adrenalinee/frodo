final String argocdImage = "docker.io/bitnami/argo-cd:2.9.2"

final Integer idleMinutes = 60
final Integer instanceCap = 5

// final String frodoAdminToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhcmdvY2QiLCJzdWIiOiJmcm9kb0FkbWluOmFwaUtleSIsIm5iZiI6MTY4OTIyNTAwMSwiaWF0IjoxNjg5MjI1MDAxLCJqdGkiOiI4MzdjNjBhYS0zYjUxLTQzYmItYTI4MC0yZGI5MDUyOTA1MjEifQ.G1C8bJq6VnxNaPDKaOT6celQ2YnWmomT13a9iDfxhPc"

// final def frodoAdminToken = params.frodoAdminToken
final String projectName = params.projectName
final String helmChartRepositoryUrl = params.helmChartRepositoryUrl
final String helmChartBranch = params.helmChartBranch
final String helmChartPath = params.helmChartPath
final String imagePath = params.imagePath
final String imageTag = params.imageTag


podTemplate(
    name: "jenkins-agent-argocd",
    label: "jenkins-agent-argocd",
    nodeUsageMode: "EXCLUSIVE", // label 이 일치하는 job 에서만 사용됨.
    idleMinutes: idleMinutes, //대기시간(대시시간동안 다른 job 실행가능).
    instanceCap: instanceCap, //최대 생성가능한 동일 스팩 팟 갯수,
    containers: [
        containerTemplate(
            name: "argocd",
            image: argocdImage,
            command: "sleep",
            args: "infinity",
            runAsUser: "0"
        )
    ]
) {
    node("jenkins-agent-argocd") {
        stage("create argocd project") {
            container("argocd") {
                withCredentials([
                    string(
                        credentialsId: "argocdAdminToken",
                        variable: "token"
                    )
                ]) {
                    sh(getArgocdProjCreateCommand() + ' --auth-token=$token')
                }
            }
        }
        stage("create argocd application") {
            container("argocd") {
                withCredentials([
                    string(
                        credentialsId: "argocdAdminToken",
                        variable: "token"
                    )
                ]) {
                    sh(getArgocdAppCreateCommand() + ' --auth-token=$token')
                }
            }
        }
    }
}

def getArgocdAppCreateCommand() {
    return """argocd app create ${projectName} \
--plaintext \
--server argo-cd-argocd-server.argo-cd.svc.cluster.local \
--repo ${helmChartRepositoryUrl} \
--revision ${helmChartBranch} \
--path ${helmChartPath} \
--project ${projectName} \
--dest-namespace ${projectName} \
--dest-server https://kubernetes.default.svc \
--sync-option CreateNamespace=true \
--sync-policy none \
--helm-set image.repository=${imagePath} \
--helm-set image.tag=${imageTag}"""
}

def getArgocdProjCreateCommand() {
    return """argocd proj create ${projectName} \
--plaintext \
--server argo-cd-argocd-server.argo-cd.svc.cluster.local \
--description "frodo 에서 생성한 project 입니다." \
--dest *,* \
--src *"""
}
