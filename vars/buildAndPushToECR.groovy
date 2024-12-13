def call(Map config) {
    def awsRegion = config.awsRegion ?: 'us-east-1'
    def ecrRepo = config.ecrRepo
    def dockerImage = config.dockerImage
    def dockerTag = config.dockerTag ?: 'latest'
    def credentialsId = config.credentialsId

    if (!ecrRepo || !dockerImage || !credentialsId) {
        error "Missing required parameters: ecrRepo, dockerImage, credentialsId"
    }

    pipeline {
        agent any

        environment {
            AWS_REGION = awsRegion
            ECR_REPO = ecrRepo
            DOCKER_IMAGE = dockerImage
            DOCKER_TAG = dockerTag
        }

        stages {
            stage('Login to ECR') {
                steps {
                    script {
                        withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                            sh '''
                            aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REPO
                            '''
                        }
                    }
                }
            }

            stage('Build Docker Image') {
                steps {
                    script {
                        sh 'docker build -t $DOCKER_IMAGE:$DOCKER_TAG .'
                    }
                }
            }

            stage('Tag Docker Image') {
                steps {
                    script {
                        sh 'docker tag $DOCKER_IMAGE:$DOCKER_TAG $ECR_REPO/$DOCKER_IMAGE:$DOCKER_TAG'
                    }
                }
            }

            stage('Push Docker Image to ECR') {
                steps {
                    script {
                        sh 'docker push $ECR_REPO/$DOCKER_IMAGE:$DOCKER_TAG'
                    }
                }
            }
        }
    }
}