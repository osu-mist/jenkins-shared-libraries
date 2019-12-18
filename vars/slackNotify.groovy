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
  def config = configMap as SlackNotifyConfig
  echo "slackNotify library called with config: ${config.dump()}"

  if (currentBuild.result == 'SUCCESS') {
    if (
      currentBuild.previousBuild?.result
      && currentBuild.previousBuild.result != 'SUCCESS'
      && config.notifyBackToNormal
    ) {
      echo "${currentBuild.fullDisplayName} - Back to normal"
    }
    else if (config.notifySuccess) {
      echo "${currentBuild.fullDisplayName} - Success"
    }
  } else if (currentBuild.result == 'ABORTED' && config.notifyAborted) {
    echo "${currentBuild.fullDisplayName} - Aborted"
  } else if (currentBuild.result == 'NOT_BUILT' && config.notifyNotBuilt) {
    echo "${currentBuild.fullDisplayName} - Not built"
  } else if (currentBuild.result == 'UNSTABLE' && config.notifyUnstable) {
    echo "${currentBuild.fullDisplayName} - Unstable"
  } else if (currentBuild.result == 'FAILURE' && config.notifyFailure) {
    if (currentBuild.previousBuild?.result && currentBuild.previousBuild.result == 'SUCCESS') {
      echo "${currentBuild.fullDisplayName} - Failure"
    } else if (config.notifyRepeatedFailure) {
      echo "${currentBuild.fullDisplayName} - Still failing"
    }
  }
}
