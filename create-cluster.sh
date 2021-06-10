#!/bin/bash
# create cluster
kind create cluster --name=k8s-cluster-1 --config kind-conf.yaml

# view kube config
cat ~/.kube/config

# switch context
kubectl cluster-info --context kind-k8s-cluster-1

# view all nodes
 kubet get nodes --all-namespaces


# create namespaces to separate environments
kubectl create namespace jenkins
kubectl create namespace dev
kubectl create namespace production

# set up Jenkins server
./create-jenkins.sh

