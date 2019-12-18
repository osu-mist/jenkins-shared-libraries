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
    "<${currentBuild.absoluteUrl}|${currentBuild.fullDisplayName}> - ${message}"
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
      slackSend(message: formatMessage("Success"), color: '#008000')
    }
  } else if (currentBuild.result == 'ABORTED' && config.notifyAborted) {
    slackSend(message: formatMessage("Aborted"), color: '#808080')
  } else if (currentBuild.result == 'NOT_BUILT' && config.notifyNotBuilt) {
    slackSend(message: formatMessage("Not built"), color: '#FF1493')
  } else if (currentBuild.result == 'UNSTABLE' && config.notifyUnstable) {
    slackSend(message: formatMessage("Unstable"), color: '#FFA500')
  } else if (currentBuild.result == 'FAILURE' && config.notifyFailure) {
    if (
      currentBuild.previousBuild?.result
      && currentBuild.previousBuild.result == 'FAILURE'
      && config.notifyRepeatedFailure
    ) {
      slackSend(message: formatMessage("Still failing"), color: '#FF0000')
    } else {
      slackSend(message: formatMessage("Failure"), color: '#FF0000')
    }
  }
}
