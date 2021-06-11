#!/bin/bash
set -e

## el problema esta en el nombre del release, VER

helm repo add jenkinsci https://charts.jenkins.io

helm repo update

# # el flag -n es del namespaces 
 helm upgrade jenkins jenkinsci/jenkins --version 2.19.0 -n jenkins -i \
   --set master.tag=2.287                                        \
   --set master.serviceType=NodePort                             \
   --set master.resources.requests.cpu=200m                     \
   --set master.resources.requests.memory=100Mi                 \
    -f plugins.yaml                                               \

#chart=jenkinsci/jenkins
#helm install jenkins -n jenkins -f jenkins-values.yaml $chart


# Get your 'admin' user password by running
printf $(kubectl get secret --namespace jenkins jenkins -o jsonpath="{.data.jenkins-admin-password}" | base64 --decode);echo

# Get the Jenkins URL to visit by running these commands in the same shell:
export NODE_PORT=$(kubectl get --namespace jenkins -o jsonpath="{.spec.ports[0].nodePort}" services jenkins)
export NODE_IP=$(kubectl get nodes --namespace jenkins -o jsonpath="{.items[0].status.addresses[0].address}")
echo http://$NODE_IP:$NODE_PORT/login

## see if it's working
kubectl get pods -n jenkins
kubectl get services -n jenkins
kubectl get configmap -n jenkins
kubectl get deployment -n jenkins

# save pod name in a variable
myjenkinspod=$(kubectl -n jenkins get pods --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}')

kubectl describe pod $myjenkinspod -n jenkins

kubectl -n jenkins logs $myjenkinspod --all-containers 

# see logs to debug problems:
#  kubectl describe pod $myjenkinspod -n jenkins 
#  kubectl -n jenkins logs $myjenkinspod -c copy-default-config
#  kubectl -n jenkins logs $myjenkinspod -c jenkins-sc-config 

# After all is running, I mean, if we run kubectl get pods -n jenins  and our pods is running
#   we should set up port forwarding using:
kubectl -n jenkins port-forward $myjenkinspod 8080:8080

# we should be able to ennter using http://127.0.0.1:8080/  or  http://127.0.0.1:8080/ 