#!/bin/bash
set -e

helm repo add jenkinsci https://charts.jenkins.io

export KUBECONFIG=/.kube/config

helm upgrade jenkins jenkinsci/jenkins --version 2.19.0 -n jenkins -i \
  --set master.tag=2.287                                        \
  --set master.serviceType=NodePort                             \
  --set master.agent.enabled=false                              \
  --set master.nodeSelector."kubernetes\.io/os"=linux           \
  --set master.nodeSelector."node\.kubernetes\.io/jenkins"=owned \
  --set master.resources.requests.cpu=500m                     \
  --set master.resources.requests.memory=200Mi                 \
  --set persistence.annotations."helm\.sh/resource-policy"=keep \
  -f plugins.yaml                                               \
  -f .jcasc-settings.yaml

kubectl apply -f ingress.yaml -n jenkins
