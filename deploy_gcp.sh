#!/bin/sh

# Authenticate using service account: https://cloud.google.com/sdk/gcloud/reference/auth/activate-service-account
# Enable stackdriver monitoring: https://cloud.google.com/monitoring/kubernetes-engine/installing
# Enable debugging: https://cloud.google.com/debugger/docs/setup/java
# Deploy docker image to GKE for the first time: https://cloud.google.com/kubernetes-engine/docs/quickstart
# Steps:
# > gcloud container clusters create gh-cluster --num-nodes=1
# > kubectl create deployment gh-service --image=docker.io/joelin/gh-service:latest
# > kubectl expose deployment gh-service --type LoadBalancer --port 80 --target-port 8080
# > kubectl expose deployment gh-service-nodeport --type NodePort --port 80 --target-port 8080
# > kubectl apply -f gh-secret.yml
# > kubectl apply -f gh-ingress.yml

gcloud components install kubectl --quiet
gcloud config set project gothic-dreamer-271402
gcloud auth activate-service-account gh-travis-ci-service-account@gothic-dreamer-271402.iam.gserviceaccount.com --key-file gothic-dreamer-271402-bfe27e1015cc.json
gcloud config set compute/zone asia-east1-b

gcloud container clusters get-credentials gh-cluster --zone asia-east1-b --project gothic-dreamer-271402
kubectl set env deployment/gh-service PROFILE=gcp
kubectl scale deployment gh-service --replicas=0
kubectl scale deployment gh-service --replicas=1
kubectl get services gh-service-nodeport
