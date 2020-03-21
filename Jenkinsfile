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
        //containerTemplate(
        //    name: 'gcloud',
        //   image: 'gcr.io/cloud-builders/gcloud',
        //    ttyEnabled: true,
        //    command: 'cat'
       // )
    ],
    volumes: [
        hostPathVolume(
            hostPath: '/var/run/docker.sock',
            mountPath: '/var/run/docker.sock'
        )
    ]
) {

    node('mypod') {
        
        def PROJECT = "qwiklabs-gcp-00-89ee1953fcd8"
        def CLUSTER = "jenkins-cd"
        def CLUSTER_ZONE = "us-east1-d"
        def JENKINS_CRED = "${PROJECT}"
        def APP_SERVICE1 = "servicea"
        def APP_SERVICE2 = "serviceb"
        def TAG_ID = "1.0.0"
        def commitId

        //env.GOOGLE_APPLICATION_CREDENTIALS = GOOGLE_APPLICATION_CREDENTIALS

        stage ('Git Checkout') {
            checkout scm
            commitId = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()

            //sh "cp jenkins-deploy-dev-infra.json /home/jenkins/dev/"
            //sh "gcloud auth activate-service-account --key-file=${GOOGLE_APPLICATION_CREDENTIALS}"
            //sh "gcloud config set compute/zone ${env.CLUSTER_ZONE}"
            //sh "gcloud config set core/project ${env.PROJECT_ID}"
            //sh "gcloud config set compute/region ${env.REGION}"
        }
        stage ('Maven Build') {
            container ('maven') {
                sh '''cd serviceA
                mvn -DskipTests clean package'''

                sh '''cd serviceB
                mvn -DskipTests clean package
                cd ..'''
            }
        }
        def repository
        stage ('Docker Build and Push') {

            //container ('gcloud') {
            //    sh "gcloud auth configure-docker"
            //}

            container ('docker') {
                def registryIp = "gcr.io/qwiklabs-gcp-00-89ee1953fcd8"//sh(script: 'getent hosts registry.kube-system | awk \'{ print $1 ; exit }\'', returnStdout: true).trim()
                //repository = "${registryIp}:80/services" 

                //echo registryIp
                //echo repository

                sh "cat keyfile.json | docker login -u _json_key --password-stdin https://gcr.io"

                sh "docker build -t servicea:1.0.0 serviceA/."
                sh "docker tag servicea:1.0.0 ${registryIp}/servicea:1.0.0"
                sh "docker push ${registryIp}/servicea:1.0.0"
                
                sh "docker build -t serviceb:1.0.0 serviceB/."
                sh "docker tag serviceb:1.0.0 ${registryIp}/serviceb:1.0.0"
                sh "docker push ${registryIp}/serviceb:1.0.0"
                
                //sh '''cd serviceA
                //docker build -t servicea:1.0.0 .
                //docker tag servicea:1.0.0 gcr.io/qwiklabs-gcp-01-fd6e8f56c6dd/servicea:1.0.0
                //docker push gcr.io/qwiklabs-gcp-01-fd6e8f56c6dd/servicea:1.0.0
                //cd ..'''
            
                //sh '''cd serviceB
                //docker build -t serviceb:1.0.0 .
                //docker tag serviceb:1.0.0 gcr.io/qwiklabs-gcp-01-fd6e8f56c6dd/serviceb:1.0.0
                //docker push gcr.io/qwiklabs-gcp-01-fd6e8f56c6dd/serviceb:1.0.0
                //cd ..'''

            }
        }
        stage ('Helm Deploy') {
            container ('helm') {
                sh "/helm init --client-only --skip-refresh"
                //sh "/helm upgrade --install --wait --set image.repository=gcr.io/qwiklabs-gcp-01-516dac6d48f0,image.tag=1.0.0 servicea hello"
                sh("/helm upgrade --install --wait --set image.repository=gcr.io/qwiklabs-gcp-00-89ee1953fcd8,image.tag=1.0.0 ./helm/serviceA/ servicea --namespace dev")
                sh("/helm upgrade --install --wait --set image.repository=gcr.io/qwiklabs-gcp-00-89ee1953fcd8,image.tag=1.0.0 ./helm/serviceB/ serviceb --namespace dev")
            }
        }
    }
}


