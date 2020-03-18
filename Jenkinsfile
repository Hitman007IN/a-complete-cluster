/*
    This is the main pipeline section with the stages of the CI/CD
 */
pipeline {

    environment {
        PROJECT = "qwiklabs-gcp-01-395f73c2d999"
        CLUSTER = "jenkins-cd"
        CLUSTER_ZONE = "us-east1-d"
        JENKINS_CRED = "${PROJECT}"
        APP_SERVICE1 = "servicea"
        APP_SERVICE2 = "serviceb"
        TAG_ID = "1.0.0"
  }

    agent any

    //def commitId
    stage ('Extract') {
        checkout scm
        sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
    }

    stage('Build Application Code') { 
        steps {
            sh '''
            cd serviceA
            mvn -DskipTests clean install
            '''
        }
        steps {
            sh '''
            cd serviceB
            mvn -DskipTests clean install
            '''
        }
    }

    stage('Build Docker Image') {
        steps {
            sh '''
            cd serviceA
            docker.build -t env.APP_SERVICE1:env.TAG_ID .
            docker.tag env.APP_SERVICE1:env.TAG_ID gcr.io/env.PROJECT/env.APP_SERVICE1:env.TAG_ID
            '''
        }
        steps {
            sh '''
            cd serviceB
            docker.build -t env.APP_SERVICE2:env.TAG_ID .
            docker.tag env.APP_SERVICE2:env.TAG_ID gcr.io/env.PROJECT/env.APP_SERVICE2:env.TAG_ID
            '''
        }
    }

    stage('Push Image to GCR') {
        docker.withRegistry('https://gcr.io', 'env.JENKINS_CRED') {
            sh "docker push env.APP_SERVICE1:env.TAG_ID"
            sh "docker push env.APP_SERVICE2:env.TAG_ID"
        }
    }

    stage('Deployment') {
        steps {
           script {
                container('helm') {
                    // Init authentication and config for your kubernetes cluster
                    sh("helm init --client-only --skip-refresh")
                    sh("helm upgrade --install --wait env.APP_SERVICE1 ./helm --namespace dev")
                    sh("helm upgrade --install --wait env.APP_SERVICE2 ./helm --namespace dev")
                }
            }
        }
    }
}

