

pipeline {

    agent any

 

    stages {

        stage('Checkout') {

            steps {

              checkout([$class: 'GitSCM', branches: [[name: 'main']], userRemoteConfigs: [[url: 'https://github.com/prasad739/BookingService.git']]])

            }

        }

 

        stage('Build') {

            steps {

                bat 'mvn clean install'

            }

        }

 

        stage('Test') {

            steps {

                bat 'mvn test'

            }

        }

 

        stage('Package') {

            steps {

                bat 'mvn package'

            }

        }

 

        stage('Deploy') {

            steps {

                echo 'mvn deploy'

            }

        }

    }

}
