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
) 
environment {
        PROJECT = "qwiklabs-gcp-01-516dac6d48f0"
        CLUSTER = "jenkins-cd"
        CLUSTER_ZONE = "us-east1-d"
        JENKINS_CRED = "${PROJECT}"
        APP_SERVICE1 = "servicea"
        APP_SERVICE2 = "serviceb"
        TAG_ID = "1.0.0"
  }
  
  {  
    node('mypod') {

        stage ('Git Checkout') {
            checkout scm
            commitId = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
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
            container ('docker') {
                //def registryIp = sh(script: 'getent hosts registry.kube-system | awk \'{ print $1 ; exit }\'', returnStdout: true).trim()
                //repository = "${registryIp}:80/hello"

                sh '''cd serviceA
                docker build -t ${env.APP_SERVICE1}:${env.TAG_ID} .
                docker tag ${env.APP_SERVICE1}:${env.TAG_ID} gcr.io/${env.PROJECT}/${env.APP_SERVICE1}:${env.TAG_ID}
                docker push gcr.io/${env.PROJECT}/${env.APP_SERVICE1}:${env.TAG_ID}
                cd ..'''
            
                sh '''cd serviceB
                docker build -t ${env.APP_SERVICE2}:${env.TAG_ID} .
                docker tag ${env.APP_SERVICE2}:${TAG_ID} gcr.io/${env.PROJECT}/${env.APP_SERVICE2}:${env.TAG_ID}
                docker push gcr.io/${env.PROJECT}/${env.APP_SERVICE2}:${env.TAG_ID}
                cd ..'''
            }
        }
        stage ('Helm Deploy') {
            container ('helm') {
                sh "/helm init --client-only --skip-refresh"
                sh "/helm upgrade --install --wait --set image.repository=gcr.io/${PROJECT},image.tag=${TAG_ID} hello hello"
            }
        }
    }
}


