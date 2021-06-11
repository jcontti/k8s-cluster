#!/bin/sh


kind delete cluster --name k8s-cluster-1 

kubectl delete pv -n jenkins jenkins-pv
kubectl delete all --all -n jenkins

#kubectl delete sa -n jenkins jenkins