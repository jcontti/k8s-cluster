#!/bin/bash

# create cluster
kind create cluster --name=k8s-cluster-1 --config kind-conf.yaml

# view kube config
cat ~/.kube/config

# switch context
kubectl cluster-info --context kind-k8s-cluster-1

# view all nodes
kubectl get nodes --all-namespaces

# create namespaces to separate environments
kubectl create namespace jenkins
kubectl create namespace dev
kubectl create namespace production

####### set up Jenkins server

# secrets...
kubectl create secret generic jcasc-secret -n jenkins \
  --from-literal=CRED_GHSVCACCNT_PASSWD=$(aws secretsmanager get-secret-value --secret-id jenkins-svcaccounts --region us-east-2 --query SecretString --output text | jq -r .credGhsvcaccntPasswd) \
  --dry-run=client -o yaml | kubectl apply -f -

# create persisten volume for jenkins 
kubectl apply -f jenkins-volume.yaml 

# create a service account for jenkins
kubectl apply -f jenkins-sa.yaml

# execute jenkins creation...
#./create-jenkins.sh

