/*
    This is the main pipeline section with the stages of the CI/CD
 */
pipeline {

    agent any
    
    environment {
        PROJECT = "qwiklabs-gcp-01-adf6005c1439"
        CLUSTER = "jenkins-cd"
        CLUSTER_ZONE = "us-east1-d"
        JENKINS_CRED = "${PROJECT}"
        APP_SERVICE1 = "servicea"
        APP_SERVICE2 = "serviceb"
        TAG_ID = "1.0.0"
  }
    
    tools {
        maven 'Maven 3.3.9' 
        jdk 'jdk8' 
    }
    
    stages{

        //def commitId
    stage ('Extract') {
        steps {
            script {
                checkout scm
            sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
            }
            
        }
       
    }

    stage('Build Application Code') { 
        steps {
            script {
                sh '''
            cd serviceA
            mvn -DskipTests clean package
            '''
            sh '''
            cd serviceB
            mvn -DskipTests clean package
            cd ..
            '''
            }
            
        }
        
    }

    stage('Build Docker Image') {
        steps {
             script {
                 sh '''
            cd serviceA
            docker build -t ${env.APP_SERVICE1}:${env.TAG_ID} .
            docker tag {env.APP_SERVICE1}:${env.TAG_ID} gcr.io/${env.PROJECT}/${env.APP_SERVICE1}:${env.TAG_ID}
            '''
            sh '''
            cd serviceB
            docker build -t ${env.APP_SERVICE2}:${env.TAG_ID} .
            docker tag ${env.APP_SERVICE2}:${env.TAG_ID} gcr.io/${env.PROJECT}/${env.APP_SERVICE2}:${env.TAG_ID}
            '''
             }
            
        }
        
    }

    stage('Push Image to GCR') {
        steps {
            script {
                docker.withRegistry('https://gcr.io/${env.PROJECT}/', 'env.JENKINS_CRED') {
            sh "docker push ${env.APP_SERVICE1}:${env.TAG_ID}"
            sh "docker push ${env.APP_SERVICE2}:${env.TAG_ID}"
            }
            
            }
        }
        
    }

    stage('Helm Deployment') {
        steps {
           script {
                container('helm') {
                    // Init authentication and config for your kubernetes cluster
                    sh("helm init --client-only --skip-refresh")
                    sh("helm upgrade --install --wait ${env.APP_SERVICE1} ./helm/serviceA/ --namespace dev")
                    sh("helm upgrade --install --wait ${env.APP_SERVICE2} ./helm/serviceB/ --namespace dev")
                }
            }
        }
    }

    }
}

