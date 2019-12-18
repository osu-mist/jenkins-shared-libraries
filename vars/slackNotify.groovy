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

  if (currentBuild.result == 'SUCCESS') {
    if (
      currentBuild.previousBuild?.result
      && currentBuild.previousBuild.result != 'SUCCESS'
      && config.notifyBackToNormal
    ) {
      slackSend(message: formatMessage("Back to normal"), color: '#008000')
    } else if (config.notifySuccess) {
      slackSend(message: formatMessage("Success"), color: '#0000FF')
    }
  } else if (currentBuild.result == 'ABORTED' && config.notifyAborted) {
    slackSend(message: formatMessage("Aborted"), color: '#000000')
  } else if (currentBuild.result == 'NOT_BUILT' && config.notifyNotBuilt) {
    slackSend(message: formatMessage("Not built"), color: '#808080')
  } else if (currentBuild.result == 'UNSTABLE' && config.notifyUnstable) {
    slackSend(message: formatMessage("Unstable"), color: '#FFFF00')
  } else if (currentBuild.result == 'FAILURE' && config.notifyFailure) {
    if (currentBuild.previousBuild?.result && currentBuild.previousBuild.result == 'SUCCESS') {
      slackSend(message: formatMessage("Failure"), color: '#FF0000')
    } else if (config.notifyRepeatedFailure) {
      slackSend(message: formatMessage("Still failing"), color: '#8B0000')
    }
  }
}
