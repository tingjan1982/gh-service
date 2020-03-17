#!/bin/sh

# Login using service account: https://cloud.google.com/sdk/gcloud/reference/auth/activate-service-account

gcloud components install kubectl --quiet
gcloud config set project gothic-dreamer-271402
gcloud auth activate-service-account gh-travis-ci-service-account@gothic-dreamer-271402.iam.gserviceaccount.com --key-file gothic-dreamer-271402-bfe27e1015cc.json
gcloud config set compute/zone asia-east1-b

# enable stackdriver monitoring: https://cloud.google.com/monitoring/kubernetes-engine/installing
# enable debugging: https://cloud.google.com/debugger/docs/setup/java

# gcloud container clusters create gh-cluster --num-nodes=1 --enable-stackdriver-kubernetes --scopes=https://www.googleapis.com/auth/cloud_debugger (--enable-cloud-logging is for legacy cluster 1.14 and prior)
# kubectl run nextpos-web --image=docker.io/joelin/nextpos-service:latest --port 8080
# kubectl expose deployment nextpos-web --type=LoadBalancer --port 80 --target-port 8080

## document the environment related steps here for future reference.
## https://dzone.com/articles/configuring-spring-boot-on-kubernetes-with-configm
## https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#define-container-environment-variables-using-configmap-data

# kubectl set env deployment/nextpos-web PROFILE=gcp
# kubectl create configmap application-gcp-props --from-file=application-gcp.properties

## volumes and volumeMounts YAML definition:

#  volumeMounts:
#  - mountPath: /config
#    name: application-props
#    readOnly: true

#  volumes:
#  - name: application-props
#    configMap:
#      name: application-gcp-props
#      defaultMode: 420
#      items:
#      - key: application-gcp.properties
#        path: application-gcp.properties

gcloud container clusters get-credentials gh-cluster --zone asia-east1-b --project gothic-dreamer-271402
kubectl create deployment gh-service --image=docker.io/joelin/gh-service:latest
kubectl set env deployment/gh-service PROFILE=gcp
kubectl scale deployment gh-service --replicas=0
kubectl scale deployment gh-service --replicas=1
kubectl get services gh-service
