# a-complete-cluster
Istio-jenkins-helm-kubernetes-docker-grafana-stackdriver

![alt text](https://github.com/Hitman007IN/a-complete-cluster/blob/master/architectural_diagram.png)

# Create Cluster on GKE

Step 1 :- create cluster
- gcloud container clusters create jenkins-cd \
--num-nodes 6 \
--machine-type n1-standard-4 \
--zone=us-east1-b \
--scopes "https://www.googleapis.com/auth/source.read_write,cloud-platform"

Step 2 :- Connect to cluster
- gcloud container clusters get-credentials jenkins-cd --zone us-east1-b --project flawless-mason-258102

# Install Helm on GKE

Step 1 :- Install Helm
- wget https://storage.googleapis.com/kubernetes-helm/helm-v2.14.1-linux-amd64.tar.gz
- tar zxfv helm-v2.14.1-linux-amd64.tar.gz
- cp linux-amd64/helm .
- kubectl create clusterrolebinding cluster-admin-binding --clusterrole=cluster-admin --user=$(gcloud config get-value account)
- kubectl create serviceaccount tiller --namespace kube-system
- kubectl create clusterrolebinding tiller-admin-binding --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
- ./helm init --service-account=tiller
- ./helm update
- ./helm version

# Install Istio on GKE

Step 1 :- Download istio file from https://github.com/istio/istio/releases/tag/1.0.5

Step 2 :- extract tar xvf istio-1.0.5-linux.tar.gz and cd istio-1.0.5

Step 3 :- kubectl create namespace istio-system

Step 4 :- helm template install/kubernetes/helm/istio \
  --set global.mtls.enabled=false \
  --set tracing.enabled=true \
  --set kiali.enabled=true \
  --set grafana.enabled=true \
  --namespace istio-system > istio.yaml

Step 5 :- kubectl apply -f istio.yaml

Step 6 :- kubectl get pods -n istio-system

# Install Jenkins on GKE

Step 1 :- Configure Jenkins
- git clone https://github.com/GoogleCloudPlatform/continuous-deployment-on-kubernetes.git
- cd continuous-deployment-on-kubernetes

Step 2 :- Install Jenkins
- helm install -n cd stable/jenkins -f jenkins/values.yaml --version 1.2.2 --wait
- kubectl get pods
- kubectl create clusterrolebinding jenkins-deploy --clusterrole=cluster-admin --serviceaccount=default:cd-jenkins
- export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/component=jenkins-master" -l "app.kubernetes.io/instance=cd" -o jsonpath="{.items[0].metadata.name}")
- kubectl port-forward $POD_NAME 8080:8080 >> /dev/null &

Step 3 :- Connect to Jenkins
- printf $(kubectl get secret cd-jenkins -o jsonpath="{.data.jenkins-admin-password}" | base64 --decode);echo
- open browser with port 8080 

# Create Jenkins Pipeline

Step 1 :-
- kubectl create namespace dev
- kubectl create clusterrolebinding default-service --clusterrole=cluster-admin --serviceaccount=default:default

Step2 2 :-  Add your service account credentials

- First we will need to configure our GCP credentials in order for Jenkins to be able to access our code repository

- In the Jenkins UI, Click “Credentials” on the left
- Click either of the “(global)” links (they both route to the same URL)
- Click “Add Credentials” on the left
- From the “Kind” dropdown, select “Google Service Account from metadata”. your project name will be displayed
- Click “OK”

Clone Sourec Repository - https://source.developers.google.com/p/flawless-mason-258102/r/github_hitman007in_a-complete-cluster


Reference :-
- https://cloud.google.com/solutions/jenkins-on-kubernetes-engine-tutorial
- https://medium.com/google-cloud/back-to-microservices-with-istio-p1-827c872daa53
