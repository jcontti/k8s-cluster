#!/bin/sh
nohup kubectl -n jenkins port-forward jenkins-bfb54f5b4-t76m7 8080:8080 &
