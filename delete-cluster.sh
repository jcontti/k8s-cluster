#!/bin/sh

helm uninstall jenkins -n jenkins 
kind delete cluster --name k8s-cluster-1 
