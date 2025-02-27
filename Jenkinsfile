pipeline {
  agent {
    label 'centos7-alljava-maven-docker'
  }

  options {
    parallelsAlwaysFailFast()
    skipStagesAfterUnstable()
  }

  environment {
    GIT = credentials('s-hcom-jenkins-rbac')
    GIT_USERNAME = "${env.GIT_USR}"
    GIT_PASSWORD = "${env.GIT_PSW}"
  }

  stages {
    stage('Build and deploy') {
      steps {
        echo 'Checking out project...'
        checkout scm
        echo 'Building...'
        withMaven(jdk: 'OpenJDK11', maven: 'Maven3.6', mavenSettingsConfig: 'hcomdata-artifactory-maven-settings') {
          sh 'mvn clean deploy jacoco:report checkstyle:checkstyle'
        }
      }
    }

    stage('Release') {
      options {
       timeout(time: 14, unit: 'DAYS')
      }

      input {
        message "Perform release?"
        ok 'Perform release'
        parameters {
          string(description: 'Release version', name: 'RELEASE_VERSION')
          string(description: 'Development version', name: 'DEVELOPMENT_VERSION')
        }
      }

      steps {
        script {
          if (env.RELEASE_VERSION == null || env.DEVELOPMENT_VERSION == null) {
            currentBuild.result = 'ABORTED'
            error 'Invalid version'
          }
        }
        echo 'Performing release...'
        withMaven(jdk: 'OpenJDK11', maven: 'Maven3.6', mavenSettingsConfig: 'hcomdata-artifactory-maven-settings') {
          sh """mvn --batch-mode release:prepare release:perform \
                  -Dresume=false \
                  -DreleaseVersion=${RELEASE_VERSION} \
                  -DdevelopmentVersion=${DEVELOPMENT_VERSION} \
                  -DautoVersionSubmodules=true"""
        }
      }
    }
  }
}
