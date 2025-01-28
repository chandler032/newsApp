pipeline {
    agent any
    environment {
        IMAGE_NAME = 'newsapp'  // Docker image name
        IMAGE_TAG = 'latest'            // Docker image tag
        REGISTRY = 'docker.io'          // Use your Docker registry
    }
    stages {
        stage('Checkout') {
            steps {
                // Clone the repository
                git 'https://github.com/your-repo/news-app.git'
            }
        }
        stage('Build') {
            steps {
                // Build the application
                sh './mvnw clean install'
            }
        }
        stage('Test') {
            steps {
                // Run tests
                sh './mvnw test'
            }
        }
        stage('Code Quality') {
            steps {
                // Run SonarQube analysis (assuming SonarQube is configured)
                sh './mvnw sonar:sonar -Dsonar.host.url=http://your-sonarqube-url'
            }
        }
        stage('Docker Build') {
            steps {
                // Build Docker image
                sh 'docker build -t $REGISTRY/$IMAGE_NAME:$IMAGE_TAG .'
            }
        }
        stage('Docker Publish') {
            steps {
                // Push Docker image to registry
                sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD'
                sh 'docker push $REGISTRY/$IMAGE_NAME:$IMAGE_TAG'
            }
        }
        stage('Deploy') {
            steps {
                // Deploy the service locally using Docker
                sh 'docker run -d -p 8080:8080 $REGISTRY/$IMAGE_NAME:$IMAGE_TAG'
            }
        }
    }
    post {
        always {
            echo 'Cleaning up...'
            sh 'docker rmi $REGISTRY/$IMAGE_NAME:$IMAGE_TAG'
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline execution failed!'
        }
    }
}
