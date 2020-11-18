library identifier: "pipeline-library@v1.5",
retriever: modernSCM(
  [
    $class: "GitSCMSource",
    remote: "https://github.com/redhat-cop/pipeline-library.git"
  ]
)

openshift.withCluster() {
  env.NAMESPACE = openshift.project()
  env.POM_FILE = env.BUILD_CONTEXT_DIR ? "${env.BUILD_CONTEXT_DIR}/pom.xml" : "pom.xml"
  env.APP_NAME = "${JOB_NAME}".replaceAll(/-build.*/, '')
  echo "Starting Pipeline for ${APP_NAME}..."
  env.BUILD = "${env.NAMESPACE}"
}

pipeline {
  // Use Jenkins Maven slave
  // Jenkins will dynamically provision this as OpenShift Pod
  // All the stages and steps of this Pipeline will be executed on this Pod
  // After Pipeline completes the Pod is killed so every run will have clean
  // workspace
  agent {
    label 'maven'
  }

  // Pipeline Stages start here
  // Requeres at least one stage
  stages {

    // Checkout source code
    // This is required as Pipeline code is originally checkedout to
    // Jenkins Master but this will also pull this same code to this slave
    stage('Git Checkout') {
      steps {
        // Turn off Git's SSL cert check, uncomment if needed
        // sh 'git config --global http.sslVerify false'
        git url: "${APPLICATION_SOURCE_REPO}", branch: "${APPLICATION_SOURCE_REF}"
        script {
          env.commit = sh(script:"git rev-parse HEAD", returnStdout: true).trim()
          env.version = "${env.commit}-${BUILD_NUMBER}"
        }
        echo "Setting version to ${env.version}"
      }
    }

    // Run Maven build, skipping tests
    // stage('Build'){
      // steps {
        //hygieiaBuildPublishStep buildStatus: 'InProgress'
        // sh "mvn -B clean install -DskipTests=true -f ${POM_FILE}"
      // }
      /*
      post {
        failure {
          hygieiaBuildPublishStep buildStatus: 'Failure'
        }
        aborted {
          hygieiaBuildPublishStep buildStatus: 'Aborted'
        }
        success {
          hygieiaBuildPublishStep buildStatus: 'Success'
          hygieiaArtifactPublishStep artifactDirectory: './target/', artifactGroup: 'org.test', artifactName: "shift-rest-0.1.0.jar", artifactVersion: "${env.version}"
        }
      }
      */
    // }

    // Run Maven unit tests
    stage('Unit Test'){
      steps {
        // sh "mvn -B test -f ${POM_FILE}"
        // sh "mvn -B clean test -Dtest=InvokeSoapServiceRouteBuilderTest -f simple-tests/pom.xml"
        sh "mvn -B clean test -f simple-tests/pom.xml"
      }
      post {
        always {
            junit 'simple-tests/target/surefire-reports/**/*.xml'
            // junit '/tmp/workspace/camel-demos/simple-tests/target/surefire-reports/**/*.xml'
            // junit 'build/reports/**/*.xml'
        }
      }
    }

  }
}

