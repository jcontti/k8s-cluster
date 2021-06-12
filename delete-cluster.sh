#!/bin/sh

helm uninstall jenkins -n jenkins 

kind delete cluster --name k8s-cluster-1 

#kubectl delete pv -n jenkins jenkins-pv
#kubectl delete sa -n jenkins jenkins

#kubectl delete all --all -n jenkins

