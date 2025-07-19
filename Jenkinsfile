pipeline {
    agent {
        docker {
            image 'web-automation-framework'
            args '-v /root/.m2:/root/.m2'
        }
    }

    environment {
        MAVEN_OPTS = '-Dmaven.test.failure.ignore=false'
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo '📥 Checking out code from GitHub...'
                git branch: 'main', url: 'https://github.com/amolaldar05/WebAutomationFrameworkUsingAI.git'
            }
        }

        stage('Start Selenium Grid') {
            steps {
                echo '🚀 Starting Selenium Grid with Docker Compose...'
                sh 'docker-compose up -d'
            }
        }

        stage('Build with Maven') {
            steps {
                echo '⚙️ Building project and downloading dependencies...'
                sh 'mvn clean compile'
            }
        }

        stage('Run TestNG Tests') {
            steps {
                echo '🚀 Running TestNG Tests...'
//                 sh 'mvn test -DsuiteXmlFile=testNG_files/grid_testNG.xml'
                    sh 'mvn clean test -P grid'
            }
        }

        stage('Publish Test Results') {
            steps {
                echo '📊 Publishing TestNG Test Reports...'
                publishHTML (target: [
                    reportDir: 'target/surefire-reports',
                    reportFiles: 'index.html',
                    reportName: 'TestNG Report'
                ])
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo '📦 Archiving build artifacts (HTML reports, logs, etc.)'
                archiveArtifacts artifacts: '**/target/*.html', fingerprint: true
            }
        }
    }

    post {
        success {
            echo '✅ Build and Tests passed successfully!'
        }
        failure {
            echo '❌ Build failed or tests failed.'
        }
        always {
            echo '📢 Stopping Selenium Grid and cleaning workspace...'
            // Stop Selenium Grid
            sh 'docker-compose down'
            // Clean workspace
            cleanWs()
        }
    }
}
