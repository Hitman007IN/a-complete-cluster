//def GOOGLE_APPLICATION_CREDENTIALS = '/home/jenkins/dev/jenkins-deploy-dev-infra.json'

podTemplate(
    label: 'mypod', 
    inheritFrom: 'default',
    containers: [
        containerTemplate(
            name: 'maven', 
            image: 'maven:3.6.3-jdk-11-openj9',
            ttyEnabled: true,
            command: 'cat'
        ),
        containerTemplate(
            name: 'docker', 
            image: 'docker:18.02',
            ttyEnabled: true,
            command: 'cat'
        ),
        containerTemplate(
            name: 'helm', 
            image: 'ibmcom/k8s-helm:v2.6.0',
            ttyEnabled: true,
            command: 'cat'
        )
    ],
    volumes: [
        hostPathVolume(
            hostPath: '/var/run/docker.sock',
            mountPath: '/var/run/docker.sock'
        )
    ]
) {

    node('mypod') {
        
        def PROJECT = "flawless-mason-258102"
        def CLUSTER = "jenkins-cd"
        def CLUSTER_ZONE = "us-east1-d"
        def JENKINS_CRED = "${PROJECT}"
        def APP_SERVICE1 = "servicea"
        def APP_SERVICE2 = "serviceb"
        def TAG_ID = "1.0.0"
        def commitId

        stage ('Git Checkout') {
            checkout scm
            commitId = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        }
        stage ('Maven Build') {
            container ('maven') {
                sh '''cd serviceA
                mvn -DskipTests clean install'''

                sh '''cd serviceB
                mvn -DskipTests clean package
                cd ..'''
            }
        }
        def repository
        stage ('Docker Build and Push') {

            container ('docker') {
                def registryIp = "gcr.io/flawless-mason-258102"

                sh "cat keyfile.json | docker login -u _json_key --password-stdin https://gcr.io"

                sh "docker build -t servicea:1.0.0 serviceA/."
                sh "docker tag servicea:1.0.0 ${registryIp}/servicea:1.0.0"
                sh "docker push ${registryIp}/servicea:1.0.0"
                
                sh "docker build -t serviceb:1.0.0 serviceB/."
                sh "docker tag serviceb:1.0.0 ${registryIp}/serviceb:1.0.0"
                sh "docker push ${registryIp}/serviceb:1.0.0"

            }
        }
        stage ('Helm Deploy') {
            container ('helm') {
                sh "/helm init --client-only --skip-refresh"
                //sh "/helm upgrade --install --wait --set image.repository=gcr.io/qwiklabs-gcp-01-516dac6d48f0,image.tag=1.0.0 servicea hello"
                sh("/helm upgrade --install --wait --set image.repository=gcr.io/flawless-mason-258102,image.tag=1.0.0 servicea helm/serviceA/ --namespace dev")
                sh("/helm upgrade --install --wait --set image.repository=gcr.io/flawless-mason-258102,image.tag=1.0.0 serviceb helm/serviceB/ --namespace dev")
            }
        }
    }
}

