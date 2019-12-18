#!/usr/bin/env groovy

class SlackNotifyConfig {
  boolean notifySuccess = false
  boolean notifyAborted = true
  boolean notifyNotBuilt = true
  boolean notifyUnstable = true
  boolean notifyFailure = true
  boolean notifyRepeatedFailure = false
  boolean notifyBackToNormal = true
}

def call(def currentBuild, Map configMap = [:]) {
  Closure<String> formatMessage = { String message ->
    "${currentBuild.fullDisplayName} - ${message}"
  }

  def config = configMap as SlackNotifyConfig
  echo "slackNotify library called with config: ${config.dump()}"

  if (currentBuild.result == 'SUCCESS') {
    if (
      currentBuild.previousBuild?.result
      && currentBuild.previousBuild.result != 'SUCCESS'
      && config.notifyBackToNormal
    ) {
      echo formatMessage("Back to normal")
    } else if (config.notifySuccess) {
      echo formatMessage("Success")
    }
  } else if (currentBuild.result == 'ABORTED' && config.notifyAborted) {
    echo formatMessage("Aborted")
  } else if (currentBuild.result == 'NOT_BUILT' && config.notifyNotBuilt) {
    echo formatMessage("Not built")
  } else if (currentBuild.result == 'UNSTABLE' && config.notifyUnstable) {
    echo formatMessage("Unstable")
  } else if (currentBuild.result == 'FAILURE' && config.notifyFailure) {
    if (currentBuild.previousBuild?.result && currentBuild.previousBuild.result == 'SUCCESS') {
      echo formatMessage("Failure")
    } else if (config.notifyRepeatedFailure) {
      echo formatMessage("Still failing")
    }
  }
}
