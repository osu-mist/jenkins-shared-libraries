#!/usr/bin/env groovy

import hudson.Util

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
    "<${currentBuild.absoluteUrl}|${currentBuild.fullDisplayName}> - duration: \
${Util.getTimeSpanString(currentBuild.duration)} - ${message}"
  }

  def config = configMap as SlackNotifyConfig

  String successColor  = '#008000' // green
  String abortedColor  = '#808080' // gray
  String notBuiltColor = '#FF1493' // pink
  String unstableColor = '#FFA500' // orange
  String failureColor  = '#FF0000' // red

  if (currentBuild.result == 'SUCCESS') {
    if (
      currentBuild.previousBuild?.result
      && currentBuild.previousBuild.result != 'SUCCESS'
      && config.notifyBackToNormal
    ) {
      // find the initial non-successful build by traversing backwards
      def firstBadBuild = currentBuild.previousBuild
      while (firstBadBuild.previousBuild?.result && firstBadBuild.previousBuild.result != 'SUCCESS') {
        firstBadBuild = firstBadBuild.previousBuild
      }

      // calculate the amount of time that has passed since the initial non-successful build finished
      def backToNormalTime = (
        (currentBuild.startTimeInMillis + currentBuild.duration)
        - (firstBadBuild.startTimeInMillis + firstBadBuild.duration)
      )
      slackSend(
        message: "${formatMessage("Back to normal")} after ${Util.getTimeSpanString(backToNormalTime)}",
        color: successColor
      )
    } else if (config.notifySuccess) {
      slackSend(message: formatMessage("Success"), color: successColor)
    }
  } else if (currentBuild.result == 'ABORTED' && config.notifyAborted) {
    slackSend(message: formatMessage("Aborted"), color: abortedColor)
  } else if (currentBuild.result == 'NOT_BUILT' && config.notifyNotBuilt) {
    slackSend(message: formatMessage("Not built"), color: notBuiltColor)
  } else if (currentBuild.result == 'UNSTABLE' && config.notifyUnstable) {
    slackSend(message: formatMessage("Unstable"), color: unstableColor)
  } else if (currentBuild.result == 'FAILURE' && config.notifyFailure) {
    if (
      currentBuild.previousBuild?.result
      && currentBuild.previousBuild.result == 'FAILURE'
      && config.notifyRepeatedFailure
    ) {
      slackSend(message: formatMessage("Still failing"), color: failureColor)
    } else {
      slackSend(message: formatMessage("Failure"), color: failureColor)
    }
  }
}
