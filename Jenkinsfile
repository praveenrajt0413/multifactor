pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'multifactor-auth'
        DOCKER_TAG = 'latest'
    }

    stages {
        stage('Checkout') {
            steps {
                // In a real environment, checkout scm
                echo 'Source is local'
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ."
            }
        }

        stage('Deploy to K8s') {
            steps {
                sh "kubectl apply -f deployment.yaml"
            }
        }
    }
}
