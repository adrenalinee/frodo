if (projectName == "") {
    error("[FRODO] projectName 은 필수값입니다.")
}
if (projectRepositoryUrl == "") {
    error("[FRODO] projectRepositoryUrl 은 필수값입니다.")
}
if (projectRepositoryBranch == "") {
    error("[FRODO] projectRepositoryBranch 은 필수값입니다.")
}
if (imagePath == "") {
    error("[FRODO] imagePath 는 팔수값입니다.")
}

pipelineJob("${projectName}-${projectRepositoryBranch}-build-gradle-push-image") {
    parameters {
        credentialsParam("repositoryCredential") {
            type("com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl")
            description("소스 저장소에 접근하기 위한 credential 입니다.")
            required(true)
        }
        credentialsParam("imageRegistryCredential") {
            type("com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl")
            description("image registry 에 접근하기 위한 credential 입니다.")
            required(true)
        }
        stringParam {
            name("projectName")
            defaultValue(projectName)
            trim(true)
        }
        stringParam {
            name("repositoryUrl")
            defaultValue(projectRepositoryUrl)
            trim(true)
        }
        stringParam {
            name("branch")
            defaultValue(projectRepositoryBranch)
            trim(true)
        }
        choiceParam {
            name("jdkVersion")
            choices(["17", "20"])
            defaultValue(jdkVersion)
            description("build 를 진행할 jdk의 버전을 지정합니다.")
        }
        stringParam {
            name("imagePath")
            defaultValue(imagePath)
            description("image push 할 주소 입니다. 프로토콜을 제외한 경로 입니다. ex) domain/owner/repo")
            trim(true)
        }
    }
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url(frodoRepositoryUrl)
                        credentials(frodoCredential)
                    }
                    branch(frodoBranch)
                    scriptPath("jenkinsfile/build-gradle-push-image.Jenkinsfile")
                }
            }
        }
    }
    properties {
        disableConcurrentBuilds()
    }
    logRotator {
        numToKeep(30)
    }
}